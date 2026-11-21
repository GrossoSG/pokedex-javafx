package org.example.dao;

import org.example.database.Conexao;
import org.example.model.Pokemon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavoritoDAO {

    // ADICIONAR FAVORITO

    public boolean adicionarFavorito(int numero)
            throws SQLException {

        String sql = "INSERT OR IGNORE INTO favorito " +
                "(numero_pokemon) VALUES (?)";

        try (
                Connection conexao = Conexao.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)
        ) {
            comando.setInt(1, numero);

            return comando.executeUpdate() > 0;
        }
    }

    // REMOVER FAVORITO

    public boolean removerFavorito(int numero)
            throws SQLException {

        String sql = "DELETE FROM favorito WHERE numero_pokemon = ?";

        try (
                Connection conexao = Conexao.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)
        ) {
            comando.setInt(1, numero);

            return comando.executeUpdate() > 0;
        }
    }

    // É FAVORITO?

    public boolean ehFavorito(int numero)
            throws SQLException {

        String sql = "SELECT 1 FROM favorito WHERE numero_pokemon = ?";

        try (
                Connection conexao = Conexao.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)
        ) {
            comando.setInt(1, numero);

            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next();
            }
        }
    }

    // LISTAR FAVORITOS

    public List<Pokemon> listarFavoritos()
            throws SQLException {

        List<Pokemon> lista = new ArrayList<>();

        String sql = "SELECT numero_pokemon FROM favorito " +
                "ORDER BY numero_pokemon";

        PokemonDAO pokemonDAO = new PokemonDAO();

        try (
                Connection conexao = Conexao.abrir();
                Statement comando = conexao.createStatement();
                ResultSet resultado = comando.executeQuery(sql)
        ) {
            while (resultado.next()) {

                int numero = resultado.getInt("numero_pokemon");

                Pokemon pokemon = pokemonDAO.buscarPorNumero(numero);

                if (pokemon != null) {
                    lista.add(pokemon);
                }
            }
        }

        return lista;
    }

}
