package com.example.application.data.repository;

import com.example.application.data.entity.Pharmacist;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class PharmacistRepository implements Repository<Pharmacist> {

    private static final Logger log = LoggerFactory.getLogger(PharmacistRepository.class);
    private final HikariDataSource dataSource;

    public PharmacistRepository(@Autowired HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String getTableName() {
        return "pharmacists";
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public Class<Pharmacist> getType() {
        return Pharmacist.class;
    }

    @Override
    public void save(Pharmacist pharmacist) throws SQLException {
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT create_pharmacist(?, ?, ?, ?)")) {
            preparedStatement.setString(1, pharmacist.getFirst_name());
            preparedStatement.setString(2, pharmacist.getLast_name());
            preparedStatement.setString(3, pharmacist.getPharmacy());
            preparedStatement.setLong(4, pharmacist.getUser_id());
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException(e);
        }
    }

    @Override
    public int update(Pharmacist pharmacist) {
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT update_pharmacist(?, ?, ?, ?)")) {
            preparedStatement.setString(1, pharmacist.getFirst_name());
            preparedStatement.setString(2, pharmacist.getLast_name());
            preparedStatement.setString(3, pharmacist.getPharmacy());
            preparedStatement.setLong(4, pharmacist.getId());
            preparedStatement.execute();
            return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public List<Pharmacist> findByPharmacy(String pharmacy) {
        String sql = "SELECT * FROM find_pharmacist_by_pharmacy(?)";
        RowMapper<Pharmacist> rowMapper = (resultSet, rowNum) -> {
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
            preparedStatement.setString(1, pharmacy);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Pharmacist> pharmacists = new ArrayList<>();
                while (resultSet.next()) {
                    Pharmacist pharmacist = rowMapper.mapRow(resultSet, resultSet.getRow());
                    pharmacists.add(pharmacist);
                }
                return pharmacists;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Pharmacist> findByFullName(String firstName, String lastName) {
        String sql = "SELECT * FROM find_by_full_name(?, ?, ?)";
        RowMapper<Pharmacist> rowMapper = (resultSet, rowNum) -> {
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
            preparedStatement.setString(1, getTableName());
            preparedStatement.setString(2, firstName);
            preparedStatement.setString(3, lastName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Pharmacist> pharmacists = new ArrayList<>();
                while (resultSet.next()) {
                    Pharmacist pharmacist = rowMapper.mapRow(resultSet, resultSet.getRow());
                    pharmacists.add(pharmacist);
                }
                return pharmacists;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Pharmacist findByUserId(long id) throws SQLException {
        String sql = "SELECT * FROM find_pharmacist_by_user_id(?)";
        RowMapper<Pharmacist> rowMapper = (resultSet, rowNum) -> {
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
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return rowMapper.mapRow(resultSet, 1);
                }
                return null;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException(e);
        }
    }

    public boolean checkStock(String medicine, String pharmacy, double dosage, int duration) {
        String sql = "SELECT * FROM find_pharmacy_stock(?, ?)";
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, medicine);
            preparedStatement.setString(2, pharmacy);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) >= dosage * duration;
                }
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void updateStock(String medicine, String pharmacy, double dosage, int duration) throws SQLException {
        String sql = "SELECT update_stock(?, ?, ?, ?)";
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, medicine);
            preparedStatement.setString(2, pharmacy);
            preparedStatement.setDouble(3, dosage);
            preparedStatement.setInt(4, duration);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException(e);
        }
    }

    public Map<String, String> getMedicines(String pharmacy) throws SQLException {
        String sql = "SELECT * FROM find_pharmacy_medicines(?)";
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, pharmacy);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                Map<String, String> medicines = new HashMap<>();
                while (resultSet.next()) {
                    String json = resultSet.getString(1);
                    JSONObject jsonObject = new JSONObject(json);
                    String medicine = jsonObject.getString("medicine");
                    String stock = jsonObject.getString("stock");
                    medicines.put(medicine, stock);
                }
                return medicines;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException(e);
        }
    }

    public void refillMedicine(String pharmacy, String medicine) throws SQLException {
        String sql = "SELECT refill_medicine(?, ?)";
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, pharmacy);
            preparedStatement.setString(2, medicine);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException(e);
        }
    }
}
