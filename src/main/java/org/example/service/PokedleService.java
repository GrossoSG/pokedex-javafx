package org.example.service;

import org.example.dao.PokemonDAO;
import org.example.model.Pokemon;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PokedleService {

    private Pokemon secreto;
    private List<Integer> tentativas = new ArrayList<>();
    private boolean acertou = false;
    private int maxTentativas = 8;

    // CONSTRUTOR
    public PokedleService() throws SQLException {

        PokemonDAO dao = new PokemonDAO();
        List<Pokemon> pokemons = dao.listarTodos();

        Random random = new Random();

        int indice = random.nextInt(pokemons.size());

        secreto = pokemons.get(indice);
    }

    // VERIFICAR SE O JOGO TERMINOU
    public boolean terminou() {
        return acertou || tentativas.size() >= maxTentativas;
    }

    // TENTATIVAS RESTANTES
    public int tentativasRestantes() {
        return maxTentativas - tentativas.size();
    }

    // COMPARAR VALORES NUMÉRICOS
    private String comparar(int palpite, int resposta) {

        if (palpite < resposta) {
            return "MAIOR ↑";
        }

        if (palpite > resposta) {
            return "MENOR ↓";
        }

        return "IGUAL ✓";
    }


    // REALIZAR UMA TENTATIVA

    public String tentar(String nome) throws SQLException {

        PokemonDAO dao = new PokemonDAO();
        List<Pokemon> pokemons = dao.listarTodos();

        if (terminou()) {
            return "O jogo já terminou!";
        }

        Pokemon palpite = null;

        // Procurar o Pokémon pelo nome
        for (Pokemon p : pokemons) {
            if (p.getNome().equalsIgnoreCase(nome.trim())) {
                palpite = p;
                break;
            }
        }

        if (palpite == null) {
            return "Pokémon não encontrado!";
        }

        // Verificar tentativa repetida
        int numero = palpite.getNumero();

        if (tentativas.contains(numero)) {
            return "Você já tentou esse Pokémon!";
        }

        tentativas.add(numero);

        // Verificar se acertou
        if (numero == secreto.getNumero()) {
            acertou = true;
            return "PARABÉNS! Você acertou: " + secreto.getNome();
        }

        // Gerar dicas
        String dicas =
                "Pokémon: " + palpite.getNome() + "\n" +
                "Número: " + comparar(numero, secreto.getNumero()) + "\n" +
                "Tentativas restantes: " + tentativasRestantes();

        if (terminou()) {
            dicas += "\nFim de jogo! Era: " + secreto.getNome();
        }

        return dicas;
    }

}
