package com.example.application.data.repository;

import com.example.application.data.entity.Prescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PrescriptionRepository implements Repository<Prescription> {
    private static final Logger log = LoggerFactory.getLogger(PrescriptionRepository.class);
    private final HikariDataSource dataSource;

    public PrescriptionRepository(@Autowired HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String getTableName() {
        return "prescriptions";
    }

    @Override
    public HikariDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public Class<Prescription> getType() {
        return Prescription.class;
    }

    @Override
    public void save(Prescription prescription) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT create_prescription(?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, prescription.getDoctor_id());
            preparedStatement.setLong(2, prescription.getPatient_id());
            preparedStatement.setString(3, prescription.getPrescription_date());
            preparedStatement.setString(4, prescription.getPrescription_time());
            preparedStatement.execute();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public int update(Prescription prescription) {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT update_prescription(?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, prescription.getDoctor_id());
            preparedStatement.setLong(2, prescription.getPatient_id());
            preparedStatement.setString(3, prescription.getPrescription_date());
            preparedStatement.setString(4, prescription.getPrescription_time());
            preparedStatement.setLong(5, prescription.getId());
            preparedStatement.execute();
            return 1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public Prescription findByEverything(long doctor_id, long patient_id, String prescription_date, String prescription_time) {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "select * from find_prescription_by_everything(?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, doctor_id);
            preparedStatement.setLong(2, patient_id);
            preparedStatement.setString(3, prescription_date);
            preparedStatement.setString(4, prescription_time);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String json = resultSet.getString(1);
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    return objectMapper.readValue(json, getType());
                } catch (JsonProcessingException e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException("Error mapping JSON to object", e);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void saveMedicines(Prescription prescription, String[] symptoms, String[] intensities) {
        try (Connection connection = dataSource.getConnection()) {
            for (int i = 0; i < symptoms.length; i++) {
                String sql = "SELECT create_prescription_medicine(?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setLong(1, prescription.getId());
                preparedStatement.setString(2, symptoms[i]);
                preparedStatement.setString(3, intensities[i]);
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String getMedicine(Prescription prescription, String symptom) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT * FROM find_medicine_of_symptom(?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, prescription.getId());
            preparedStatement.setString(2, symptom);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void setDosageAndDuration(Prescription prescription, Map<String, Double> dosages, Map<String, Integer> durations) {
        try (Connection connection = dataSource.getConnection()) {
            for (String medicine : dosages.keySet()) {
                String sql = "SELECT set_dosage_duration(?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setLong(1, prescription.getId());
                preparedStatement.setString(2, medicine);
                preparedStatement.setDouble(3, dosages.get(medicine));
                preparedStatement.setInt(4, durations.get(medicine));
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String[] getMedicinePharmacy(String medicine, double dosage, int duration) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT * FROM find_medicine_pharmacies(?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, medicine);
            preparedStatement.setDouble(2, dosage);
            preparedStatement.setInt(3, duration);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> pharmacies = new ArrayList<>();
            while (resultSet.next()) {
                String json = resultSet.getString(1);
                JSONObject jsonObject = new JSONObject(json);
                String pharmacy = jsonObject.getString("pharmacy_name");
                pharmacies.add(pharmacy);
            }
            return pharmacies.toArray(new String[0]);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public Map<String, String> getMedicines(long prescription_id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT * FROM find_prescription_medicines(?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, prescription_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Map<String, String> medicines = new HashMap<>();
            while (resultSet.next()) {
                String json = resultSet.getString(1);
                JSONObject jsonObject = new JSONObject(json);
                String medicine = jsonObject.getString("name");
                String dosageAndDuration = jsonObject.getString("dosage_per_day") +
                        " pastile pe zi, timp de " +
                        jsonObject.getString("treatment_time") + " zile.";
                medicines.put(medicine, dosageAndDuration);
            }
            return medicines;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public void removeMedicines(long prescription_id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT remove_medicines(?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, prescription_id);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
    public void removeMedicine(long prescription_id, String medicine) throws SQLException {
        String sql = "SELECT remove_medicine(?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, prescription_id);
            preparedStatement.setString(2, medicine);
            preparedStatement.execute();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
