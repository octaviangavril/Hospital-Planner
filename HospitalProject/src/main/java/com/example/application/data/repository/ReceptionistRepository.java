package com.example.application.data.repository;

import com.example.application.data.entity.Doctor;
import com.example.application.data.entity.Receptionist;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Component
public class ReceptionistRepository implements Repository<Receptionist> {

    private static final Logger log = LoggerFactory.getLogger(ReceptionistRepository.class);
    private final HikariDataSource hikariDataSource;

    public ReceptionistRepository(@Autowired HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    @Override
    public String getTableName() {
        return "receptionists";
    }

    @Override
    public HikariDataSource getDataSource() {
        return hikariDataSource;
    }

    @Override
    public Class<Receptionist> getType() {
        return Receptionist.class;
    }

    @Override
    public void save(Receptionist receptionist) throws SQLException {
        String sql = "SELECT create_receptionist(?, ?, ?, ?)";
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, receptionist.getFirst_name());
            preparedStatement.setString(2, receptionist.getLast_name());
            preparedStatement.setLong(3, receptionist.getDesk_id());
            preparedStatement.setLong(4, receptionist.getUser_id());
            preparedStatement.execute();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public int update(Receptionist receptionist) {
        String sql = "SELECT update_receptionist(?, ?, ?, ?)";
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, receptionist.getFirst_name());
            preparedStatement.setString(2, receptionist.getLast_name());
            preparedStatement.setLong(3, receptionist.getDesk_id());
            preparedStatement.setLong(4, receptionist.getId());
            preparedStatement.execute();
            return 1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public List<Receptionist> findByDeskId(Long desk_id) throws SQLException {
        String sql = "SELECT * FROM find_receptionist_by_desk_id(?, ?)";
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, getTableName());
            preparedStatement.setLong(2, desk_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Receptionist> receptionists = new ArrayList<>();
            while (resultSet.next()) {
                String json = resultSet.getString(1);
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    Receptionist receptionist = objectMapper.readValue(json, getType());
                    receptionists.add(receptionist);
                } catch (JsonProcessingException e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException("Error mapping JSON to object", e);
                }
            }
            resultSet.close();
            return receptionists;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    public List<Receptionist> findByFullName(String firstName, String lastName) throws SQLException {
        String sql = "SELECT * FROM find_by_full_name(?, ?, ?)";
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, getTableName());
            preparedStatement.setString(2, firstName);
            preparedStatement.setString(3, lastName);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Receptionist> receptionists = new ArrayList<>();
            while (resultSet.next()) {
                String json = resultSet.getString(1);
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    Receptionist receptionist = objectMapper.readValue(json, getType());
                    receptionists.add(receptionist);
                } catch (JsonProcessingException e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException("Error mapping JSON to object", e);
                }
            }
            resultSet.close();
            return receptionists;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    public Receptionist findByUserId(long id) throws SQLException {
        String sql = "SELECT * FROM find_receptionist_by_user_id(?)";
        RowMapper<Receptionist> rowMapper = (resultSet, rowNum) -> {
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
                    return rowMapper.mapRow(resultSet, 0);
                }
                return null;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException(e);
        }
    }
}
