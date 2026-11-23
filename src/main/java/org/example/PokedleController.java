package org.example;

import java.io.IOException;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.dao.PokemonDAO;
import org.example.model.Pokemon;
import org.example.service.PokedleService;

public class PokedleController {

    public static String primeiraMaiuscula(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    @FXML private TextField campoPalpite;
    @FXML private Label tentativasLabel;
    @FXML private Label mensagemLabel;
    @FXML private VBox historicoTentativas;
    @FXML private Button botaoTentar;

    private PokedleService jogo;
    private int tentativasFeitas;


    @FXML
    private void initialize() {
        campoPalpite.setOnAction(event -> enviarPalpite());
        novoJogo();
    }


    // NOVA PARTIDA
    @FXML
    private void novoJogo() {

        try {
            jogo = new PokedleService();
            tentativasFeitas = 0;

            historicoTentativas.getChildren().clear();

            campoPalpite.clear();
            campoPalpite.setDisable(false);
            botaoTentar.setDisable(false);

            mensagemLabel.setText("Adivinhe o Pokémon secreto!");

            atualizarTentativas();

        } catch (SQLException e) {
            jogo = null;

            campoPalpite.setDisable(true);
            botaoTentar.setDisable(true);

            mensagemLabel.setText("Erro ao iniciar o Pokedle.");
            mostrarErro("Erro ao iniciar", e);
        }
    }


    // ENVIAR PALPITE
    @FXML
    private void enviarPalpite() {

        if (jogo == null || jogo.terminou()) {
            return;
        }

        String nome = campoPalpite.getText().trim();

        if (nome.isEmpty()) {
            mensagemLabel.setText("Digite um Pokémon!");
            return;
        }

        try {
            int tentativasAntes = jogo.tentativasRestantes();

            // Verifica o palpite pelo PokedleService
            String resultado = jogo.tentar(nome);

            // Se não consumiu uma tentativa, o palpite foi inválido
            if (tentativasAntes == jogo.tentativasRestantes()) {
                mensagemLabel.setText(resultado);
                return;
            }

            tentativasFeitas++;

            // Adicionar ao histórico
            adicionarAoHistorico(nome, resultado);

            // Atualizar a interface
            atualizarTentativas();
            campoPalpite.clear();

            // Verificar fim do jogo
            if (jogo.terminou()) {

                campoPalpite.setDisable(true);
                botaoTentar.setDisable(true);

                if (resultado.startsWith("PARABÉNS!")) {
                    mensagemLabel.setText("Parabéns! Você acertou!");
                } else {
                    mensagemLabel.setText("Fim de jogo! Era " + primeiraMaiuscula(jogo.getSecreto().getNome()));
                }

            } else {
                mensagemLabel.setText("Continue tentando!");
                campoPalpite.requestFocus();
            }

        } catch (Exception e) {
            mostrarErro("Erro ao verificar palpite", e);
        }
    }


    // CONTADOR DE TENTATIVAS
    private void atualizarTentativas() {

        tentativasLabel.setText(
                "Tentativas restantes: " + jogo.tentativasRestantes()
        );
    }


    // HISTÓRICO DE PALPITES

    private void adicionarAoHistorico(String nome, String resultado) {

        // Cartão horizontal: texto à esquerda e imagem à direita
        HBox cartao = new HBox(15);
        cartao.setAlignment(Pos.CENTER_LEFT);
        cartao.setMaxWidth(Double.MAX_VALUE);

        cartao.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #e2e8f0;" +
                        "-fx-border-radius: 12;" +
                        "-fx-padding: 15;"
        );

        // Título da tentativa
        Label titulo = new Label(
                "Tentativa " + tentativasFeitas + " - " + nome
        );

        titulo.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #172033;"
        );

        // Informações do palpite
        Label dicas = new Label(resultado);
        dicas.setWrapText(true);

        dicas.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #475569;"
        );

        // Agrupa título e informações na esquerda
        VBox textos = new VBox(8, titulo, dicas);
        textos.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(textos, Priority.ALWAYS);

        cartao.getChildren().add(textos);

        // Busca o Pokémon para descobrir o número do sprite
        try {
            PokemonDAO dao = new PokemonDAO();

            for (Pokemon pokemon : dao.buscarPorNome(nome)) {

                if (pokemon.getNome().equalsIgnoreCase(nome)) {

                    String caminho = "/sprites/" +
                            pokemon.getNumero() + ".png";

                    var url = getClass().getResource(caminho);

                    if (url != null) {
                        ImageView sprite = new ImageView(
                                new Image(url.toExternalForm())
                        );

                        sprite.setFitWidth(90);
                        sprite.setFitHeight(90);
                        sprite.setPreserveRatio(true);
                        sprite.setSmooth(false);

                        // Adiciona a imagem à direita
                        cartao.getChildren().add(sprite);
                    }

                    break;
                }
            }

        } catch (Exception e) {
            System.err.println(
                    "Erro ao carregar sprite: " + e.getMessage()
            );
        }

        // Tentativa mais recente aparece primeiro
        historicoTentativas.getChildren().add(0, cartao);
    }


    // TROCAS DE ABA

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

    private void mostrarErro(String titulo, Exception erro) {

        Alert alerta = new Alert(Alert.AlertType.ERROR);

        alerta.setTitle(titulo);
        alerta.setHeaderText("Não foi possível executar a operação.");
        alerta.setContentText(erro.getMessage());

        alerta.showAndWait();
    }

}
