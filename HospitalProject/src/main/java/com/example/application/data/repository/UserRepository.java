package com.example.application.data.repository;

import com.example.application.data.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@Component
public class UserRepository implements Repository<User> {
    private static final Logger log = LoggerFactory.getLogger(UserRepository.class);
    private final HikariDataSource hikariDataSource;

    public UserRepository(@Autowired HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    @Override
    public String getTableName() {
        return "users";
    }

    @Override
    public HikariDataSource getDataSource() {
        return hikariDataSource;
    }

    @Override
    public Class<User> getType() {
        return User.class;
    }

    @Override
    public void save(User user) throws SQLException {
        try {
            String sql = "SELECT create_user(?, ?, ?, ?)";
            try (Connection connection = hikariDataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, user.getEmail());
                preparedStatement.setString(2, user.getPassword_hash());
                preparedStatement.setString(3, user.getPassword_salt());
                preparedStatement.setString(4, user.getRole().toString());
                preparedStatement.execute();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new SQLException(e);
        }
    }

    @Override
    public int update(User user) {
        try {
            String sql = "SELECT update_user(?, ?, ?)";
            try (Connection connection = hikariDataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, user.getEmail());
                preparedStatement.setString(2, user.getPassword_hash());
                preparedStatement.setString(3, user.getRole().toString());
                preparedStatement.execute();
                return 1;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM find_user_by_email(?)";
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String json = resultSet.getString(1);
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    return objectMapper.readValue(json, getType());
                } catch (JsonProcessingException e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException("Error mapping JSON to object", e);
                } catch (IllegalArgumentException e1) {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
