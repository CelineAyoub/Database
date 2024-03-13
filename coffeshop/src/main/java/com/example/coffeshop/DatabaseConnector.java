package com.example.coffeshop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;


public class DatabaseConnector {


    private static final String URL = "jdbc:mysql://localhost:3306/coffeshop";
    private static final String USER = "root";
    private static final String PASSWORD = "Password";
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load MySQL JDBC driver. Check your classpath.");
        }
    }
    public static Connection connect() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the database.");
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to the database. Check your database configuration.");
        }

    }

}