package org.example.testes;

import org.example.dao.PokemonDAO;
import org.example.model.Pokemon;
import org.example.service.ComparacaoService;

import java.sql.SQLException;

public class TestePokemon {
    public static void main(String[] args) throws SQLException {
       /*
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

        */

        PokemonDAO dao = new PokemonDAO();
        ComparacaoService comparador = new ComparacaoService();

        // Busca dois Pokémon no banco
        Pokemon p1 = dao.buscarPorNumero(150);
        Pokemon p2 = dao.buscarPorNumero(151);

        if (p1 == null || p2 == null) {
            System.out.println("Pokémon não encontrado!");
            return;
        }

        System.out.println(p1.getNome() + " VS " + p2.getNome());

        // Compara todos os atributos
        System.out.println("HP: " +
                comparador.diferenca(p1.getHp(), p2.getHp()));

        System.out.println("Ataque: " +
                comparador.diferenca(p1.getAtaque(), p2.getAtaque()));

        System.out.println("Defesa: " +
                comparador.diferenca(p1.getDefesa(), p2.getDefesa()));

        System.out.println("Ataque Especial: " +
                comparador.diferenca(p1.getAtaqueEspecial(),
                        p2.getAtaqueEspecial()));

        System.out.println("Defesa Especial: " +
                comparador.diferenca(p1.getDefesaEspecial(),
                        p2.getDefesaEspecial()));

        System.out.println("Velocidade: " +
                comparador.diferenca(p1.getVelocidade(),
                        p2.getVelocidade()));

        // Resultado geral
        System.out.println("Diferença total: " +
                comparador.diferencaTotal(p1, p2));

        Pokemon vencedor = comparador.maiorTotal(p1, p2);

        if (vencedor != null) {
            System.out.println("Maior total: " + vencedor.getNome());
        } else {
            System.out.println("Empate!");
        }

    }
}
