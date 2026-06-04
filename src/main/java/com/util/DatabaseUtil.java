package com.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DatabaseUtil {

    private static final String URL = createDatabaseUrl();
    private static final String USER = AppConfig.get("db.user", "SYSDBA");
    private static final String PASSWORD = AppConfig.get("db.password", "masterkey");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String createDatabaseUrl() {
        String explicitUrl = AppConfig.get("db.url", "").trim();
        if (!explicitUrl.isEmpty()) {
            return explicitUrl;
        }

        String host = AppConfig.get("db.host", "localhost").trim();
        String port = AppConfig.get("db.port", "3051").trim();
        String path = resolveDatabasePath(AppConfig.get("db.path", "test").trim());

        return "jdbc:firebirdsql://" + host + ":" + port + "/" + path;
    }

    private static String resolveDatabasePath(String path) {
        if (path.contains("/") || path.contains("\\") || path.toLowerCase().endsWith(".fdb")) {
            Path databasePath = Paths.get(path);
            if (!databasePath.isAbsolute()) {
                databasePath = Paths.get(System.getProperty("user.dir")).resolve(databasePath);
            }
            return databasePath.normalize().toString().replace("\\", "/");
        }

        return path;
    }
}
