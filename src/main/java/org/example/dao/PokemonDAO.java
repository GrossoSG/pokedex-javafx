package org.example.dao;
import org.example.database.Conexao;
import org.example.model.Pokemon;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PokemonDAO {

    // CRIAR POKEMON
    private Pokemon criarPokemon(ResultSet resultado, Connection conexao)
            throws SQLException {

        Pokemon pokemon = new Pokemon(
                // Identificação
                resultado.getInt("numero"),
                resultado.getString("nome"),
                resultado.getString("tipo_1"),
                resultado.getString("tipo_2"),

                // Atributos
                resultado.getInt("hp"),
                resultado.getInt("atk"),
                resultado.getInt("def"),
                resultado.getInt("spc_atk"),
                resultado.getInt("spc_def"),
                resultado.getInt("speed"),

                // Informações físicas
                resultado.getDouble("altura_m"),
                resultado.getDouble("peso_kg"),
                resultado.getString("categoria"),

                // Habilidades
                resultado.getString("habilidade_1"),
                resultado.getString("habilidade_2"),
                resultado.getString("habilidade_hidden"),

                // Evoluções
                resultado.getInt("estagio_evolutivo"),
                resultado.getString("evolui_de"),
                resultado.getString("evolui_para"),

                // Classificações
                resultado.getBoolean("pseudo_lendario"),
                resultado.getBoolean("lendario"),
                resultado.getBoolean("mitico"),

                // Descrição
                resultado.getString("descricao")
        );

        // Carregar fraquezas, resistências e imunidades
        PokemonDefesaDAO defesaDAO = new PokemonDefesaDAO();
        defesaDAO.carregarDefesas(pokemon, conexao);

        return pokemon;
    }


    // LISTAR TODOS OS POKEMON
    public List<Pokemon> listarTodos() throws SQLException {

        List<Pokemon> lista = new ArrayList<>();

        String sql = "SELECT * FROM pokemon ORDER BY numero";

        try (
                Connection conexao = Conexao.abrir();
                Statement comando = conexao.createStatement();
                ResultSet resultado = comando.executeQuery(sql)
        ) {

            while (resultado.next()) {
                Pokemon pokemon = criarPokemon(resultado, conexao);
                lista.add(pokemon);
            }
        }

        return lista;
    }


    // BUSCAR POKEMON POR NUMERO
    public Pokemon buscarPorNumero(int numero) throws SQLException {

        String sql = "SELECT * FROM pokemon WHERE numero = ?";

        try (
                Connection conexao = Conexao.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)
        ) {

            comando.setInt(1, numero);

            try (ResultSet resultado = comando.executeQuery()) {

                if (resultado.next()) {
                    return criarPokemon(resultado, conexao);
                }
            }
        }

        return null;
    }


    // BUSCAR POKEMON POR NOME
    public List<Pokemon> buscarPorNome(String nome)
            throws SQLException {

        List<Pokemon> lista = new ArrayList<>();

        String sql = "SELECT * FROM pokemon WHERE nome LIKE ? ORDER BY numero";

        try (
                Connection conexao = Conexao.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)
        ) {

            comando.setString(1, "%" + nome + "%");

            try (ResultSet resultado = comando.executeQuery()) {

                while (resultado.next()) {
                    lista.add(criarPokemon(resultado, conexao));
                }
            }
        }

        return lista;
    }


    // BUSCAR POKEMON POR TIPO
    public List<Pokemon> buscarPorTipo(String tipo)
            throws SQLException {

        List<Pokemon> lista = new ArrayList<>();

        String sql = "SELECT * FROM pokemon " +
                "WHERE tipo_1 = ? OR tipo_2 = ? " +
                "ORDER BY numero";

        try (
                Connection conexao = Conexao.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)
        ) {

            comando.setString(1, tipo);
            comando.setString(2, tipo);

            try (ResultSet resultado = comando.executeQuery()) {

                while (resultado.next()) {
                    lista.add(criarPokemon(resultado, conexao));
                }
            }
        }

        return lista;
    }


    // BUSCAR POKEMON POR ATAQUE MINIMO
    public List<Pokemon> buscarPorAtaqueMinimo(int minimo)
            throws SQLException {

        List<Pokemon> lista = new ArrayList<>();

        String sql = "SELECT * FROM pokemon WHERE atk >= ? ORDER BY numero";

        try (
                Connection conexao = Conexao.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)
        ) {

            comando.setInt(1, minimo);

            try (ResultSet resultado = comando.executeQuery()) {

                while (resultado.next()) {
                    lista.add(criarPokemon(resultado, conexao));
                }
            }
        }

        return lista;
    }
}
