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

        PokemonDAO dao2 = new PokemonDAO();

        Pokemon p = dao2.buscarPorNumero(6);

        if (p != null) {
            System.out.println("Nome: " + p.getNome());
            System.out.println("Tipo: " + p.getTipo1());
            System.out.println("HP: " + p.getHp());
            System.out.println("Total: " + p.calcularTotal());
            System.out.println("Altura: " + p.getAltura());
            System.out.println("Peso: " + p.getPeso());
            System.out.println("Categoria: " + p.getCategoria());
            System.out.println("Habilidade: " + p.getHabilidade1());
            System.out.println("Evolui de: " + p.getEvoluiDe());
            System.out.println("Lendário: " + p.isLendario());
        }

        List<Pokemon> fogo = dao.buscarPorTipo("fire");

        for (Pokemon p2 : fogo) {
            System.out.println(p2.getNumero() + " - " + p2.getNome());
        }

        List<Pokemon> fortes = dao.buscarPorAtaqueMinimo(120);

        for (Pokemon p3 : fortes) {
            System.out.println(p3.getNome() + ": " + p3.getAtaque());
        }

        Pokemon charizard = dao.buscarPorNumero(6);

        if (charizard != null) {
            System.out.println("Pokemon: " + charizard.getNome());

            System.out.println("Fraquezas: " + charizard.getFraquezas());
            System.out.println("Resistencias: " + charizard.getResistencias());
            System.out.println("Imunidades: " + charizard.getImunidades());
        }
    }
}
