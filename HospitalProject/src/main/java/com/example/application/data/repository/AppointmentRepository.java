package com.example.application.data.repository;

import com.example.application.data.entity.Appointment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class AppointmentRepository implements Repository<Appointment> {
    private static final Logger log = LoggerFactory.getLogger(AppointmentRepository.class);
    private final HikariDataSource dataSource;

    public AppointmentRepository(@Autowired HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String getTableName() {
        return "appointments";
    }

    @Override
    public HikariDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public Class<Appointment> getType() {
        return Appointment.class;
    }

    @Override
    public void save(Appointment appointment) throws SQLException {
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT create_appointment(?, ?, ?, ?)")) {
            preparedStatement.setLong(1, appointment.getDoctor_id());
            preparedStatement.setLong(2, appointment.getPatient_id());
            preparedStatement.setString(3, appointment.getAppointment_date());
            preparedStatement.setString(4, appointment.getAppointment_time());
            preparedStatement.execute();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public int update(Appointment appointment) {
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT update_appointment(?, ?, ?, ?, ?)")) {
            preparedStatement.setLong(1, appointment.getDoctor_id());
            preparedStatement.setLong(2, appointment.getPatient_id());
            preparedStatement.setString(3, appointment.getAppointment_date());
            preparedStatement.setString(4, appointment.getAppointment_time());
            preparedStatement.setLong(5, appointment.getId());
            preparedStatement.execute();
            return 1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public Appointment findByEverything(long doctor_id, String appointment_date, String appointment_time) {
        String sql = "select * from find_appointment_by_everything(?, ?, ?)";
        RowMapper<Appointment> rowMapper = (resultSet, rowNum) -> {
            String json = resultSet.getString(1);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(json, getType());
            } catch (JsonProcessingException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException("Error mapping JSON to object", e);
            }
        };
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, doctor_id);
            preparedStatement.setString(2, appointment_date);
            preparedStatement.setString(3, appointment_time);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? rowMapper.mapRow(resultSet, 1) : null;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error executing query", e);
        }
    }

    public List<Appointment> getAvailableAppointments(String[] symptomsList, String birthDate) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * from find_available_appointments(ARRAY[");
        Arrays.stream(symptomsList).forEach(symptom -> {
            sql.append("'").append(symptom).append("'");
            if (symptom.equals(symptomsList[symptomsList.length - 1])) {
                sql.append("],");
            } else {
                sql.append(",");
            }
        });
        sql.append("'").append(birthDate).append("')");
        System.out.println(sql);
        RowMapper<Appointment> rowMapper = (resultSet, rowNum) -> {
            String json = resultSet.getString(1);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(json, getType());
            } catch (JsonProcessingException e) {
                System.out.println("Error mapping JSON to object " + e);
            }
            return null;
        };
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Appointment> appointments = new ArrayList<>();
                while (resultSet.next()) {
                    Appointment appointment = rowMapper.mapRow(resultSet, resultSet.getRow());
                    appointments.add(appointment);
                }
                return appointments;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException(e);
        }
    }

    public List<Appointment> getAppointmentsByDoctorId(long id) throws SQLException {
        String sql = "select * from find_appointments_by_doctor(?)";
        RowMapper<Appointment> rowMapper = (resultSet, rowNum) -> {
            String json = resultSet.getString(1);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(json, getType());
            } catch (JsonProcessingException e) {
                System.out.println("Error mapping JSON to object " + e);
            }
            return null;
        };
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Appointment> appointments = new ArrayList<>();
                while (resultSet.next()) {
                    Appointment appointment = rowMapper.mapRow(resultSet, resultSet.getRow());
                    appointments.add(appointment);
                }
                return appointments;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException(e);
        }
    }

    public void saveSymptoms(Appointment appointment, String[] symptoms) {
        Arrays.stream(symptoms).forEach(symptom -> {
            try (Connection connection = getDataSource().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("SELECT create_symptoms_patient(?, ?, ?)")) {
                preparedStatement.setLong(1, appointment.getId());
                preparedStatement.setLong(2, appointment.getPatient_id());
                preparedStatement.setString(3, symptom);
                preparedStatement.execute();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        });
    }

    public String[] getSymptoms(Appointment appointment) throws SQLException {
        String sql = "SELECT * FROM find_symptoms(?, ?)";
        Object[] params = {appointment.getId(), appointment.getPatient_id()};

        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, appointment.getId());
            preparedStatement.setLong(2, appointment.getPatient_id());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<String> symptoms = new ArrayList<>();
                while (resultSet.next()) {
                    String json = resultSet.getString(1);
                    JSONObject jsonObject = new JSONObject(json);
                    String symptom = jsonObject.getString("symptom");
                    symptoms.add(symptom);
                }
                return symptoms.toArray(new String[0]);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException(e);
        }
    }

    public void deleteSymptoms(long id) {
        try (Connection connection = getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("select delete_symptoms(" + id + ")");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
