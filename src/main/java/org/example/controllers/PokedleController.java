package org.example.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.example.App;
import org.example.dao.PokemonDAO;
import org.example.model.Pokemon;
import org.example.service.PokedleService;
import org.example.service.AudioService;
import org.example.util.SpriteImageLoader;

public class PokedleController {

    public static String primeiraMaiuscula(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    // COMPONENTES
    @FXML private ComboBox<Pokemon> campoPalpite;
    @FXML private Label tentativasLabel;
    @FXML private Label mensagemLabel;
    @FXML private VBox historicoTentativas;
    @FXML private Button botaoTentar;

    private PokedleService jogo;
    private int tentativasFeitas;

    // LISTA PARA A BUSCA
    private final ObservableList<Pokemon> todosPokemons =
            FXCollections.observableArrayList();

    private FilteredList<Pokemon> pokemonsFiltrados;


    // INICIALIZAR
    @FXML
    private void initialize() {

        try {
            PokemonDAO dao = new PokemonDAO();
            todosPokemons.setAll(dao.listarTodos());

            configurarBusca();
            novoJogo();

        } catch (SQLException e) {
            campoPalpite.setDisable(true);
            botaoTentar.setDisable(true);
            mensagemLabel.setText("Erro ao carregar os Pokémon.");
            mostrarErro("Erro ao carregar busca", e);
        }
    }


    // CONFIGURAR BUSCA DOS POKEMON
    private void configurarBusca() {

        campoPalpite.setEditable(true);
        campoPalpite.setVisibleRowCount(7);

        // Como o Pokémon aparece no campo
        campoPalpite.setConverter(new StringConverter<>() {

            @Override
            public String toString(Pokemon pokemon) {
                return pokemon == null ? "" : formatarPokemon(pokemon);
            }

            @Override
            public Pokemon fromString(String texto) {
                return procurarPokemon(texto);
            }
        });

        // Lista que pode ser filtrada
        pokemonsFiltrados = new FilteredList<>(todosPokemons);

        campoPalpite.setItems(pokemonsFiltrados);

        // Células com imagem e nome
        campoPalpite.setCellFactory(combo -> criarCelulaPokemon());
        campoPalpite.setButtonCell(criarCelulaPokemon());

        TextField editor = campoPalpite.getEditor();

        // Filtrar enquanto o usuário digita
        editor.textProperty().addListener(
                (observable, anterior, texto) -> {

                    Pokemon selecionado = campoPalpite.getValue();

                    if (selecionado != null &&
                            formatarPokemon(selecionado).equals(texto)) {
                        return;
                    }

                    atualizarFiltroBusca();

                    if (texto != null && !texto.isBlank()
                            && !pokemonsFiltrados.isEmpty()) {

                        campoPalpite.show();

                    } else {
                        campoPalpite.hide();
                    }
                }
        );

        // Pressionar Enter envia o palpite
        editor.setOnAction(event -> enviarPalpite());

        // Selecionar um Pokémon preenche o campo
        campoPalpite.setOnAction(event -> {

            Pokemon escolhido = campoPalpite.getValue();

            if (escolhido != null) {
                campoPalpite.getEditor().setText(
                        formatarPokemon(escolhido)
                );
            }
        });
    }


    // FILTRAR RESULTADOS
    private void atualizarFiltroBusca() {

        String texto = campoPalpite.getEditor().getText();

        pokemonsFiltrados.setPredicate(
                pokemon -> correspondeBusca(pokemon, texto)
        );
    }


    // VERIFICAR SE CORRESPONDE A BUSCA
    private boolean correspondeBusca(Pokemon pokemon, String texto) {

        if (texto == null || texto.isBlank()) {
            return true;
        }

        String termo = texto.trim().toLowerCase(Locale.ROOT);

        String nome = pokemon.getNome().toLowerCase(Locale.ROOT);

        String numero = String.valueOf(pokemon.getNumero());

        String numeroFormatado = String.format(
                "#%03d", pokemon.getNumero()
        );

        return nome.contains(termo)
                || numero.contains(termo.replace("#", ""))
                || numeroFormatado.contains(termo)
                || formatarPokemon(pokemon)
                .toLowerCase(Locale.ROOT).contains(termo);
    }


    // CRIAR OPÇÃO COM SPRITE
    private ListCell<Pokemon> criarCelulaPokemon() {

        return new ListCell<>() {

            @Override
            protected void updateItem(Pokemon pokemon, boolean vazio) {

                super.updateItem(pokemon, vazio);

                if (vazio || pokemon == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                // Imagem
                ImageView sprite = new ImageView();

                sprite.setFitWidth(40);
                sprite.setFitHeight(40);
                sprite.setPreserveRatio(true);
                sprite.setSmooth(false);

                sprite.setImage(SpriteImageLoader.loadPokemon(pokemon.getNumero(), 40));
                sprite.getStyleClass().add("pixel-art");

                // Nome e número
                Label nome = new Label(formatarPokemon(pokemon));

                nome.setStyle(
                        "-fx-font-size: 15px;" +
                                "-fx-text-fill: #172033;"
                );

                HBox conteudo = new HBox(12, sprite, nome);
                conteudo.setAlignment(Pos.CENTER_LEFT);

                setText(null);
                setGraphic(conteudo);
            }
        };
    }


    // FORMATAR NOME E NUMERO
    private String formatarPokemon(Pokemon pokemon) {

        return String.format(
                "#%03d %s",
                pokemon.getNumero(),
                primeiraMaiuscula(pokemon.getNome())
        );
    }


    // ENCONTRAR POKEMON SELECIONADO
    private Pokemon procurarPokemon(String texto) {

        if (texto == null || texto.isBlank()) {
            return null;
        }

        String busca = texto.trim().toLowerCase(Locale.ROOT);

        // Procura pelo número
        String numeroBusca = busca.replaceFirst("^#", "").trim();

        if (numeroBusca.matches("\\d+")) {

            try {
                int numero = Integer.parseInt(numeroBusca);

                for (Pokemon pokemon : todosPokemons) {
                    if (pokemon.getNumero() == numero) {
                        return pokemon;
                    }
                }

            } catch (NumberFormatException e) {
                return null;
            }
        }

        // Procura pelo nome ou texto formatado
        for (Pokemon pokemon : todosPokemons) {

            if (pokemon.getNome().equalsIgnoreCase(busca)
                    || formatarPokemon(pokemon)
                    .equalsIgnoreCase(busca)) {

                return pokemon;
            }
        }

        return null;
    }


    // LIMPAR CAMPO DA BUSCA
    private void limparCampoBusca() {

        campoPalpite.setValue(null);
        campoPalpite.getEditor().clear();
        campoPalpite.hide();
    }


    // NOVA PARTIDA
    @FXML
    private void novoJogo() {

        try {
            jogo = new PokedleService();
            tentativasFeitas = 0;

            historicoTentativas.getChildren().clear();

            limparCampoBusca();

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

        // Recebe o texto do ComboBox
        String texto = campoPalpite.getEditor().getText();

        if (texto == null || texto.isBlank()) {
            mensagemLabel.setText("Digite um Pokémon!");
            AudioService.tocarErro();
            return;
        }

        // Descobre qual Pokémon foi escolhido
        Pokemon selecionado = procurarPokemon(texto);

        String nome = selecionado != null
                ? selecionado.getNome()
                : texto.trim();

        try {
            int tentativasAntes = jogo.tentativasRestantes();

            String resultado = jogo.tentar(nome);

            // POKÉMON INVÁLIDO OU REPETIDO
            if (tentativasAntes == jogo.tentativasRestantes()) {

                mensagemLabel.setText(resultado);
                AudioService.tocarErro();
                return;
            }

            tentativasFeitas++;

            // Adicionar ao histórico
            adicionarAoHistorico(nome, resultado);

            atualizarTentativas();
            limparCampoBusca();

            // ACERTOU
            if (resultado.startsWith("PARABÉNS!")) {

                AudioService.tocarAcerto();

                mensagemLabel.setText(
                        "Parabéns! Você acertou!"
                );

            } else {

                // PALPITE ERRADO
                AudioService.tocarErro();

                if (jogo.terminou()) {
                    mensagemLabel.setText(
                            "Fim de jogo! Era " +
                                    primeiraMaiuscula(
                                            jogo.getSecreto().getNome()
                                    )
                    );
                } else {
                    mensagemLabel.setText("Continue tentando!");
                }
            }

            // FINALIZAR PARTIDA
            if (jogo.terminou()) {

                campoPalpite.setDisable(true);
                botaoTentar.setDisable(true);

            } else {
                campoPalpite.getEditor().requestFocus();
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

        HBox cartao = new HBox(15);
        cartao.setAlignment(Pos.CENTER_LEFT);
        cartao.setMaxWidth(Double.MAX_VALUE);
        cartao.getStyleClass().add("guess-card");

        Label titulo = new Label(
                "Tentativa " + tentativasFeitas + " - " + primeiraMaiuscula(nome)
        );
        titulo.getStyleClass().add("guess-title");
        VBox dicas = criarDicas(resultado);

        VBox textos = new VBox(8, titulo, dicas);
        textos.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(textos, Priority.ALWAYS);

        cartao.getChildren().add(textos);

        // Sprite do Pokémon
        try {
            PokemonDAO dao = new PokemonDAO();

            for (Pokemon pokemon : dao.buscarPorNome(nome)) {

                if (pokemon.getNome().equalsIgnoreCase(nome)) {

                    ImageView sprite = new ImageView(
                            SpriteImageLoader.loadPokemon(pokemon.getNumero(), 90));
                    sprite.setFitWidth(90);
                    sprite.setFitHeight(90);
                    sprite.setPreserveRatio(true);
                    sprite.setSmooth(false);
                    sprite.getStyleClass().add("pixel-art");
                    cartao.getChildren().add(sprite);

                    break;
                }
            }

        } catch (Exception e) {
            System.err.println(
                    "Erro ao carregar sprite: " + e.getMessage()
            );
        }

        historicoTentativas.getChildren().add(0, cartao);
        cartao.setOpacity(0);
        cartao.setTranslateY(8);
        ParallelTransition entrada = new ParallelTransition(
                new FadeTransition(Duration.millis(200), cartao),
                new TranslateTransition(Duration.millis(200), cartao)
        );
        ((FadeTransition) entrada.getChildren().get(0)).setToValue(1);
        ((TranslateTransition) entrada.getChildren().get(1)).setToY(0);
        entrada.play();
    }

    private VBox criarDicas(String resultado) {
        VBox dicas = new VBox(5);
        dicas.getStyleClass().add("guess-hints");
        for (String linha : resultado.split("\\R")) {
            String texto = linha.trim();
            if (texto.isEmpty()) {
                continue;
            }
            if (texto.startsWith("PARABÉNS!")) {
                String nomeAcertado = texto.substring(texto.lastIndexOf(' ') + 1);
                Label sucesso = new Label("Parabéns! Você acertou: "
                        + primeiraMaiuscula(nomeAcertado));
                sucesso.getStyleClass().add("success-badge");
                dicas.getChildren().add(sucesso);
                continue;
            }

            int separador = texto.indexOf(':');
            if (separador < 0) {
                Label informacao = new Label(texto);
                informacao.getStyleClass().add("hint-label");
                dicas.getChildren().add(informacao);
                continue;
            }
            String rotulo = texto.substring(0, separador + 1);
            String valor = texto.substring(separador + 1).trim();
            String indicador = encontrarIndicador(valor);
            String detalhe = indicador.isEmpty()
                    ? valor : valor.substring(0, valor.lastIndexOf(indicador)).trim();
            if ("Pokémon:".equals(rotulo)) {
                detalhe = primeiraMaiuscula(detalhe);
            }

            HBox linhaDica = new HBox(7);
            linhaDica.setAlignment(Pos.CENTER_LEFT);
            Label informacao = new Label(rotulo + " " + detalhe);
            informacao.getStyleClass().add("hint-label");
            linhaDica.getChildren().add(informacao);
            if (!indicador.isEmpty()) {
                Label estado = new Label(indicador);
                estado.getStyleClass().add("hint-" + classeIndicador(indicador));
                linhaDica.getChildren().add(estado);
            }
            dicas.getChildren().add(linhaDica);
        }
        return dicas;
    }

    private String encontrarIndicador(String texto) {
        for (String indicador : new String[]{"IGUAL ✓", "INCORRETO ×", "MAIOR ↑", "MENOR ↓"}) {
            if (texto.endsWith(indicador)) {
                return indicador;
            }
        }
        return "";
    }

    private String classeIndicador(String indicador) {
        if (indicador.startsWith("IGUAL")) {
            return "equal";
        }
        if (indicador.startsWith("INCORRETO")) {
            return "incorrect";
        }
        return indicador.startsWith("MAIOR") ? "higher" : "lower";
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


    // MOSTRAR ERROS
    private void mostrarErro(String titulo, Exception erro) {

        Alert alerta = new Alert(Alert.AlertType.ERROR);

        alerta.setTitle(titulo);
        alerta.setHeaderText("Não foi possível executar a operação.");
        alerta.setContentText(erro.getMessage());

        alerta.showAndWait();
    }
}
