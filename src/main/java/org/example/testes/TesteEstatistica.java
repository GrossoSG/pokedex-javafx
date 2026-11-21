package org.example.testes;

import org.example.dao.PokemonDAO;
import org.example.model.Pokemon;
import org.example.service.EstatisticaService;

import java.sql.SQLException;
import java.util.List;

public class TesteEstatistica {

    public static void main(String[] args)
            throws SQLException {

        PokemonDAO dao = new PokemonDAO();

        // Carrega os Pokémon uma única vez
        List<Pokemon> lista = dao.listarTodos();

        EstatisticaService estatisticas =
                new EstatisticaService(lista);


        System.out.printf("Média de HP: %.2f%n",
                estatisticas.mediaAtributo("hp"));

        System.out.printf("Média geral dos totais: %.2f%n",
                estatisticas.mediaAtributo("total"));

        Pokemon maisForte = estatisticas.maiorAtributo("total");

        if (maisForte != null) {
            System.out.println("Maior total: " + maisForte.getNome());
        }

        System.out.println("\nTOP 5 MAIS PODEROSOS:");

        for (Pokemon p : estatisticas.rankingPorAtributo("total", 5)) {
            System.out.println(
                    p.getNome() + " - " + p.calcularTotal()
            );
        }

    }
}
