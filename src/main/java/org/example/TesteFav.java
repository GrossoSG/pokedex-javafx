
package org.example;

import org.example.dao.FavoritoDAO;
import org.example.model.Pokemon;

import java.sql.SQLException;

public class TesteFav {

    public static void main(String[] args) throws SQLException {

        FavoritoDAO favoritos = new FavoritoDAO();

        // Adicionar favoritos
        favoritos.adicionarFavorito(6);
        favoritos.adicionarFavorito(25);

        // Verificar
        System.out.println(
                "Charizard é favorito? " + favoritos.ehFavorito(6)
        );

        // Listar
        System.out.println("\nMEUS FAVORITOS:");

        for (Pokemon p : favoritos.listarFavoritos()) {
            System.out.println(p.getNumero() + " - " + p.getNome());
        }

        // Remover Pikachu
        favoritos.removerFavorito(15);

        System.out.println("\nApós remover Pikachu:");
        System.out.println(favoritos.ehFavorito(25));
    }
}
