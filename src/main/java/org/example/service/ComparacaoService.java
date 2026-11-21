package org.example.service;

import org.example.model.Pokemon;

public class ComparacaoService {

    // Calcula a diferença entre dois atributos
    public int diferenca(int valor1, int valor2) {
        return valor1 - valor2;
    }

    // Compara o total de atributos
    public int diferencaTotal(Pokemon p1, Pokemon p2) {
        return p1.calcularTotal() - p2.calcularTotal();
    }

    // Retorna o Pokémon com maior total
    public Pokemon maiorTotal(Pokemon p1, Pokemon p2) {

        if (p1.calcularTotal() > p2.calcularTotal()) {
            return p1;
        }

        if (p2.calcularTotal() > p1.calcularTotal()) {
            return p2;
        }

        return null; // Empate
    }
}
