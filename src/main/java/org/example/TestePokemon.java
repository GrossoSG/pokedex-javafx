package org.example;

import org.example.dao.PokemonDAO;
import org.example.model.Pokemon;

import java.sql.SQLException;
import java.util.List;

public class TestePokemon {
    public static void main(String[] args) throws SQLException {
        PokemonDAO dao = new PokemonDAO();

        List<Pokemon> lista = dao.listarTodos();
        System.out.println("Total: " + lista.size());

        for (Pokemon p : lista){
            System.out.println(
                    p.getNumero() + " - " +
                    p.getNome() + " - " +
                    p.calcularTotal() + " - " +
                    p.getTipo1() + " - " +
                    p.getTipo2()
                    );
        }


        Pokemon busca = dao.buscarPorNumero(152);

        if (busca != null) {
            System.out.println("\n" + busca.getNome());
            System.out.println(busca.getHp());
            System.out.println(busca.calcularTotal());
        } else {
            System.out.println("Pokémon não encontrado!");
        }

        List<Pokemon> encontrados = dao.buscarPorNome("char");

        for (Pokemon p : encontrados) {
            System.out.println(p.getNome());
        }
    }
}
