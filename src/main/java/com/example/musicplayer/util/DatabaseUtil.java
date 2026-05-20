package com.example.musicplayer.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    private static final String URL = "jdbc:firebirdsql://localhost:3050/C:/RedDB/test";
    private static final String USER = "SYSDBA";
    private static final String PASSWORD = "masterkey";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}