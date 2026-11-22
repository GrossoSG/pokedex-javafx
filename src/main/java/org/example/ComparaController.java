package org.example;

import java.io.IOException;
import javafx.fxml.FXML;

public class ComparaController {

    @FXML
    private void switchToCompara() throws IOException {
        App.setRoot("compara");
    }

    @FXML
    private void switchToPokedle() throws IOException {
        App.setRoot("pokedle");
    }

    @FXML
    private void switchToPokedex() throws IOException {
        App.setRoot("pokedex");
    }

}