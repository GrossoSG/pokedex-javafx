package org.example.dao;

import org.example.model.Pokemon;

import java.sql.*;

public class PokemonDefesaDAO {

    public void carregarDefesas(Pokemon pokemon, Connection conexao)
            throws SQLException {

        String sql = "SELECT tipo, relacao FROM pokemon_defesa " +
                "WHERE numero_pokemon = ?";

        try (PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, pokemon.getNumero());

            try (ResultSet resultado = comando.executeQuery()) {

                while (resultado.next()) {
                    String tipo = resultado.getString("tipo");
                    String relacao = resultado.getString("relacao");

                    pokemon.adicionarRelacaoDefensiva(tipo, relacao);
                }
            }
        }
    }
}
