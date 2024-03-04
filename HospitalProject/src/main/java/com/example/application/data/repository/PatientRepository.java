package com.example.application.data.repository;

import com.example.application.data.entity.Patient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Component
public class PatientRepository implements Repository<Patient> {
    private static final Logger log = LoggerFactory.getLogger(PatientRepository.class);
    private final HikariDataSource dataSource;

    public PatientRepository(@Autowired HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String getTableName() {
        return "patients";
    }

    @Override
    public HikariDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public Class<Patient> getType() {
        return Patient.class;
    }

    @Override
    public void save(Patient patient) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT create_patient(?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, patient.getFirst_name());
            preparedStatement.setString(2, patient.getLast_name());
            preparedStatement.setString(3, patient.getBirthdate());
            preparedStatement.execute();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public int update(Patient patient) {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT update_patient(?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, patient.getFirst_name());
            preparedStatement.setString(2, patient.getLast_name());
            preparedStatement.setString(3, patient.getBirthdate());
            preparedStatement.setLong(4, patient.getId());
            preparedStatement.execute();
            return 1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public Patient findByEverything(String firstName, String lastName, String birthdate) {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "select * from find_patient_by_everything(?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, birthdate);
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
