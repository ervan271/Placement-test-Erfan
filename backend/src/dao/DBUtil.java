package dao;

import java.sql.*;

public class DBUtil {
    private static final String URL = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5433/apotek_sehat_sentosa");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "postgres");
    private static final String PASS = System.getenv().getOrDefault("DB_PASS", "Ervan271");

    static {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("[DBUtil] PostgreSQL JDBC Driver loaded");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("[DBUtil] ✓ Database connected successfully");
        } catch (SQLException e) {
            System.err.println("[DBUtil] ✗ Database connection failed: " + e.getMessage());
        }
    }
}