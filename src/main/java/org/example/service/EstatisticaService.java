package org.example.service;

import org.example.model.Pokemon;

import java.util.ArrayList;
import java.util.List;

public class EstatisticaService {

    // VALORES GENÉRICOS

    private int valorAtributo(Pokemon p, String atributo) {

        return switch (atributo) {
            case "hp" -> p.getHp();
            case "ataque" -> p.getAtaque();
            case "defesa" -> p.getDefesa();
            case "ataqueEspecial" -> p.getAtaqueEspecial();
            case "defesaEspecial" -> p.getDefesaEspecial();
            case "velocidade" -> p.getVelocidade();
            case "total" -> p.calcularTotal();

            default -> throw new IllegalArgumentException(
                    "Atributo inválido: " + atributo
            );
        };
    }

    // CONSTRUTOR
    private final List<Pokemon> pokemons;

    public EstatisticaService(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }

    // Quantidade de Pokémon
    public int contarPokemons() {
        return pokemons.size();
    }

    // Médias

    public double mediaAtributo(String atributo) {

        if (pokemons.isEmpty()) {
            return 0;
        }

        int soma = 0;

        for (Pokemon p : pokemons) {
            soma += valorAtributo(p, atributo);
        }

        return (double) soma / pokemons.size();
    }


    // Pokémon maior

    public Pokemon maiorAtributo(String atributo) {

        if (pokemons.isEmpty()) {
            return null;
        }

        Pokemon maior = pokemons.get(0);

        for (Pokemon p : pokemons) {

            if (valorAtributo(p, atributo) >
                    valorAtributo(maior, atributo)) {

                maior = p;
            }
        }

        return maior;
    }


    // Quantidade de Pokémon de determinado tipo
    public int contarPorTipo(String tipo) {

        int quantidade = 0;

        for (Pokemon p : pokemons) {

            if (tipo.equalsIgnoreCase(p.getTipo1()) ||
                    tipo.equalsIgnoreCase(p.getTipo2())) {

                quantidade++;
            }
        }

        return quantidade;
    }

    // Rankings

    public List<Pokemon> rankingPorAtributo(
            String atributo, int quantidade) {

        List<Pokemon> ranking = new ArrayList<>(pokemons);

        ranking.sort((p1, p2) ->
                Integer.compare(
                        valorAtributo(p2, atributo),
                        valorAtributo(p1, atributo)
                )
        );

        if (quantidade <= 0) {
            return new ArrayList<>();
        }

        return new ArrayList<>(
                ranking.subList(
                        0,
                        Math.min(quantidade, ranking.size())
                )
        );
    }

}
