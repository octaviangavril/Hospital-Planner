package com.example.application;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@SpringBootApplication
public class Application {
//  FEBRA OBOSEALA TUSE -> MEDICINA INTERNA
//  Slăbiciune sau amorțeală în membre, Durere -> Neurologie
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/schema.sql"))) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:postgresql://localhost:5432/hospitalPlanner");
            config.setUsername("postgres");
            config.setPassword("octavian");

            HikariDataSource dataSource = new HikariDataSource(config);
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();

            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sql.append(line).append("\n");
            }
            statement.execute(sql.toString());
            connection.close();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }

        SpringApplication.run(Application.class, args);
    }

}
