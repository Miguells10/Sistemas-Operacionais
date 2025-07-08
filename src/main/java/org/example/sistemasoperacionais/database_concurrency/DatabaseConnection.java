package org.example.sistemasoperacionais.database_concurrency;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/produtos_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Cria a tabela se não existir
            String sql = "CREATE TABLE IF NOT EXISTS produtos (" +
                        "id SERIAL PRIMARY KEY," +
                        "nome VARCHAR(255) NOT NULL," +
                        "preco DOUBLE PRECISION NOT NULL)";
            
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao inicializar o banco de dados", e);
        }
    }
} 