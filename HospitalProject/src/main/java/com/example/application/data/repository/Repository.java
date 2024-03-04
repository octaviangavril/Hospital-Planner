package com.example.application.data.repository;

import com.example.application.data.entity.AbstractEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.RowMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public interface Repository<T extends AbstractEntity> {
    public String getTableName();
    public HikariDataSource getDataSource();
    public Class<T> getType();
    public void save(T object) throws SQLException;
    public abstract int update(T object);
    public default void deleteById(Long id) {
        try (Connection connection = getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("select delete_by_id('" + getTableName() + "'," + id + ")");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public default int deleteAll() {
        try (Connection connection = getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("select delete_all('" + getTableName() + "')");
            return 1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }
    public default T findById(Long id) throws SQLException{
        String sql = "select * from find_by_id('" + getTableName() + "'," + id + ")";
        RowMapper<T> rowMapper = (resultSet, rowNum) -> {
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
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? rowMapper.mapRow(resultSet, 1) : null;
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }
    public default List<T> findAll() throws SQLException {
        String sql = "select * from find_all('" + getTableName() + "')";
        RowMapper<T> rowMapper = (resultSet, rowNum) -> {
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
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<T> entities = new ArrayList<>();
                while (resultSet.next()) {
                    T entity = rowMapper.mapRow(resultSet, resultSet.getRow());
                    entities.add(entity);
                }
                return entities;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException(e);
        }
    }
}
