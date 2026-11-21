package org.example.database;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexao {

    public static Connection abrir() throws SQLException {

        Path caminho = Path.of("data/pokedex_novo.db")
                .toAbsolutePath();

        // Verifica se estamos usando o banco correto
        if (!Files.isRegularFile(caminho)) {
            throw new SQLException(
                    "Banco não encontrado: " + caminho
            );
        }

        // AAbre a conexão com o SQLite

        Connection conexao = DriverManager.getConnection(
                "jdbc:sqlite:" + caminho
        );

        try (Statement comando = conexao.createStatement()) {
            comando.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            conexao.close();
            throw e;
        }

        return conexao;

    }
}
