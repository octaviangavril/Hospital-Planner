package com.example.application.data.repository;

import com.example.application.data.entity.Doctor;
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
public class DoctorRepository implements Repository<Doctor> {

    private static final Logger log = LoggerFactory.getLogger(DoctorRepository.class);
    private final HikariDataSource dataSource;

    public DoctorRepository(@Autowired HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String getTableName() {
        return "doctors";
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public Class<Doctor> getType() {
        return Doctor.class;
    }

    @Override
    public void save(Doctor doctor) throws SQLException {
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT create_doctor(?, ?, ?, ?)")) {
            preparedStatement.setString(1, doctor.getFirst_name());
            preparedStatement.setString(2, doctor.getLast_name());
            preparedStatement.setString(3, doctor.getSpeciality());
            preparedStatement.setLong(4, doctor.getUser_id());
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException(e);
        }
    }

    @Override
    public int update(Doctor doctor) {
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT update_doctor(?, ?, ?, ?)")) {
            preparedStatement.setString(1, doctor.getFirst_name());
            preparedStatement.setString(2, doctor.getLast_name());
            preparedStatement.setString(3, doctor.getSpeciality());
            preparedStatement.setLong(4, doctor.getId());
            preparedStatement.execute();
            return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public List<Doctor> findBySpeciality(String speciality) {
        String sql = "SELECT * FROM find_doctor_by_speciality(?, ?)";
        RowMapper<Doctor> rowMapper = (resultSet, rowNum) -> {
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
            preparedStatement.setString(2, speciality);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Doctor> doctors = new ArrayList<>();
                while (resultSet.next()) {
                    Doctor doctor = rowMapper.mapRow(resultSet, resultSet.getRow());
                    doctors.add(doctor);
                }
                return doctors;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Doctor> findByFullName(String firstName, String lastName) {
        String sql = "SELECT * FROM find_by_full_name(?, ?, ?)";
        RowMapper<Doctor> rowMapper = (resultSet, rowNum) -> {
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
                List<Doctor> doctors = new ArrayList<>();
                while (resultSet.next()) {
                    Doctor doctor = rowMapper.mapRow(resultSet, resultSet.getRow());
                    doctors.add(doctor);
                }
                return doctors;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Doctor findByUserId(long id) throws SQLException {
        String sql = "SELECT * FROM find_doctor_by_user_id(?)";
        RowMapper<Doctor> rowMapper = (resultSet, rowNum) -> {
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
