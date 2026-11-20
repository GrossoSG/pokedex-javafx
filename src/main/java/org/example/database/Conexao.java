package org.example.database;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

        // Abre a conexão com o SQLite
        return DriverManager.getConnection(
                "jdbc:sqlite:" + caminho
        );
    }
}
