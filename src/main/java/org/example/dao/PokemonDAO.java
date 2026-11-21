package org.example.dao;
import org.example.database.Conexao;
import org.example.model.Pokemon;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PokemonDAO {

    // Criar Pokemon

    private Pokemon criarPokemon(ResultSet resultado) throws SQLException {
        return new Pokemon(
                resultado.getInt("numero"),
                resultado.getString("nome"),
                resultado.getString("tipo_1"),
                resultado.getString("tipo_2"),
                resultado.getInt("hp"),
                resultado.getInt("atk"),
                resultado.getInt("def"),
                resultado.getInt("spc_atk"),
                resultado.getInt("spc_def"),
                resultado.getInt("speed")
        );
    }

    // Lista todos pokemons

    public List<Pokemon> listarTodos() throws SQLException {

        List<Pokemon> lista = new ArrayList<>();

        // Comando SQL
        String sql = "SELECT * FROM pokemon ORDER BY numero";

        // Abrir conexão e executar SQL
        try (
                Connection conexao = Conexao.abrir();
                Statement comando = conexao.createStatement();
                ResultSet resultado = comando.executeQuery(sql)
        ) {

            // Criando os objetos Pokemon
            while (resultado.next()) {
                Pokemon pokemon = criarPokemon(resultado);
                lista.add(pokemon);
            }
        }

        return lista;
    }

    // Busca de Pokemon por numero

    public Pokemon buscarPorNumero(int numero) throws SQLException {

        String sql = "SELECT * FROM pokemon WHERE numero = ?";

        try (
                Connection conexao = Conexao.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)
        ) {

            // Substitui o ? pelo número informado
            comando.setInt(1, numero);

            ResultSet resultado = comando.executeQuery();

            if (resultado.next()) {
                return criarPokemon(resultado);
            }
        }
        return null;
    }

    // Busca por nome


    public List<Pokemon> buscarPorNome(String nome)
            throws SQLException {

        List<Pokemon> lista = new ArrayList<>();

        String sql = "SELECT * FROM pokemon WHERE nome LIKE ?";

        try (
                Connection conexao = Conexao.abrir();
                PreparedStatement comando =
                        conexao.prepareStatement(sql)
        ) {

            comando.setString(1, "%" + nome + "%");

            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    lista.add(criarPokemon(resultado));
                }
            }
        }

        return lista;
    }

}
