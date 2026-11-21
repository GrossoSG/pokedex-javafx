package org.example.testes;

import org.example.service.PokedleService;

import java.sql.SQLException;
import java.util.Scanner;

public class TestePokedle {
    public static void main(String[] args) throws SQLException {

        PokedleService pokedle = new PokedleService();

        Scanner scan = new Scanner(System.in);

        System.out.println("=== POKEDLE ===");
        System.out.println("Adivinhe o Pokémon secreto!");

        while (!pokedle.terminou()) {

            System.out.print("\nDigite o nome do Pokémon: ");

            String nome = scan.nextLine();

            System.out.println(pokedle.tentar(nome));
        }

        scan.close();

    }
}
