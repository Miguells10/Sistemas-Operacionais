package org.example.sistemasoperacionais.database_concurrency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoService {
    private final DatabaseConcurrencyManager concurrencyManager;

    public ProdutoService() {
        this.concurrencyManager = new DatabaseConcurrencyManager();
        DatabaseConnection.initializeDatabase();
    }

    // Create
    public Produto criar(Produto produto) throws InterruptedException {
        concurrencyManager.startWriting();
        try {
            System.out.println("\n>>> Tentando criar novo produto...");
            System.out.println(">>> Iniciando criação do produto");
            
            // Delay artificial de 3 segundos
            Thread.sleep(3000);
            
            String sql = "INSERT INTO produtos (nome, preco) VALUES (?, ?) RETURNING id";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, produto.getNome());
                stmt.setDouble(2, produto.getPreco());
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    produto.setId(rs.getLong("id"));
                }
                System.out.println(">>> Produto criado com sucesso!");
                return produto;
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao criar produto", e);
            }
        } finally {
            concurrencyManager.finishWriting();
        }
    }

    // Read
    public Produto buscarPorId(Long id) throws InterruptedException {
        try {
            concurrencyManager.startReading();
            String sql = "SELECT * FROM produtos WHERE id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setLong(1, id);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new Produto(
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getDouble("preco")
                    );
                }
                return null;
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao buscar produto", e);
            }
        } finally {
            concurrencyManager.finishReading();
        }
    }

    public List<Produto> listarTodos() throws InterruptedException {
        try {
            concurrencyManager.startReading();
            String sql = "SELECT * FROM produtos";
            List<Produto> produtos = new ArrayList<>();
            
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    produtos.add(new Produto(
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getDouble("preco")
                    ));
                }
                return produtos;
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao listar produtos", e);
            }
        } finally {
            concurrencyManager.finishReading();
        }
    }

    // Update
    public Produto atualizar(Produto produto) throws InterruptedException {
        try {
            concurrencyManager.startWriting();
            String sql = "UPDATE produtos SET nome = ?, preco = ? WHERE id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, produto.getNome());
                stmt.setDouble(2, produto.getPreco());
                stmt.setLong(3, produto.getId());
                
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new RuntimeException("Produto não encontrado para atualização");
                }
                return produto;
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao atualizar produto", e);
            }
        } finally {
            concurrencyManager.finishWriting();
        }
    }

    // Delete
    public void deletar(Long id) throws InterruptedException {
        try {
            concurrencyManager.startWriting();
            String sql = "DELETE FROM produtos WHERE id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setLong(1, id);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao deletar produto", e);
            }
        } finally {
            concurrencyManager.finishWriting();
        }
    }

    public Produto buscarUltimoProduto() throws InterruptedException {
        concurrencyManager.startReading();
        try {
            String sql = "SELECT * FROM produtos ORDER BY id DESC LIMIT 1";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                if (rs.next()) {
                    return new Produto(
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getDouble("preco")
                    );
                }
                return null;
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao buscar último produto", e);
            }
        } finally {
            concurrencyManager.finishReading();
        }
    }

    public int getNumeroLeitoresAtivos() {
        return concurrencyManager.getActiveReaders();
    }
} 