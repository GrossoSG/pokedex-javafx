package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.example.controllers.PokemonDetalhesController;
import org.example.model.Pokemon;
import org.example.service.AudioService;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonBase;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;



import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("pokedex"), 1280, 720);


        // CLIQUE GLOBAL DOS BOTÕES
        scene.addEventFilter(ActionEvent.ACTION, event -> {

            if (event.getTarget() instanceof ButtonBase) {
                AudioService.tocarClique();
            }

        });
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> animarPressionado(event.getTarget(), 0.97));
        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> animarPressionado(event.getTarget(), 1));


        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });

        stage.setScene(scene);

        //abre a janela já maximizada
        //stage.setMaximized(true);
        //abre a janela já em tela cheia
        stage.setFullScreen(true);

        stage.show();
        AudioService.iniciarMusica();
    }

    private void animarPressionado(Object alvo, double escala) {
        if (!(alvo instanceof Node node)) {
            return;
        }
        Node clicavel = node;
        while (clicavel != null && !(clicavel instanceof ButtonBase)) {
            Parent parent = clicavel.getParent();
            clicavel = parent;
        }
        if (clicavel != null) {
            ScaleTransition transicao = new ScaleTransition(Duration.millis(120), clicavel);
            transicao.setToX(escala);
            transicao.setToY(escala);
            transicao.play();
        }
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static void mostrarDetalhes(Pokemon pokemon) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("pokemon-detalhes.fxml"));
        Parent root = loader.load();
        PokemonDetalhesController controller = loader.getController();
        controller.setPokemon(pokemon);
        scene.setRoot(root);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() {
        AudioService.pararMusica();
    }

}