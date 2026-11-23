package org.example.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.App;
import org.example.dao.PokemonDAO;
import org.example.model.Pokemon;
import org.example.service.AudioService;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Interpolator;
import javafx.util.Duration;
import org.example.util.SpriteImageLoader;

public class PokemonDetalhesController {

    private static final Map<String, String> CORES_TIPO = Map.ofEntries(
            Map.entry("bug", "#91a119"), Map.entry("dark", "#624d4e"),
            Map.entry("dragon", "#5060e1"), Map.entry("electric", "#fac000"),
            Map.entry("fairy", "#ef70ef"), Map.entry("fighting", "#ff8000"),
            Map.entry("fire", "#e62829"), Map.entry("flying", "#81b9ef"),
            Map.entry("ghost", "#704170"), Map.entry("grass", "#3fa129"),
            Map.entry("ground", "#915121"), Map.entry("ice", "#3dcef3"),
            Map.entry("normal", "#9fa19f"), Map.entry("poison", "#9141cb"),
            Map.entry("psychic", "#ef4179"), Map.entry("rock", "#afa981"),
            Map.entry("steel", "#60a1b8"), Map.entry("water", "#2980ef")
    );

    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Label nomePokemonLabel;
    @FXML private ImageView spriteImage;
    @FXML private HBox statsChartContainer;
    @FXML private FlowPane anteriorContainer;
    @FXML private FlowPane posteriorContainer;
    @FXML private Label descricaoLabel;
    @FXML private Label alturaLabel;
    @FXML private Label pesoLabel;
    @FXML private Label sexoLabel;
    @FXML private Label categoriaLabel;
    @FXML private VBox habilidadesContainer;
    @FXML private Label categoriaEspecialLabel;
    @FXML private Label tipo1Label;
    @FXML private Label tipo2Label;
    @FXML private HBox fraquezasRow;
    @FXML private HBox resistenciasRow;
    @FXML private HBox imunidadesRow;
    @FXML private FlowPane fraquezasContainer;
    @FXML private FlowPane resistenciasContainer;
    @FXML private FlowPane imunidadesContainer;


    private List<Pokemon> pokemons = List.of();
    private Map<String, Pokemon> pokemonsPorNome = Map.of();
    private Pokemon pokemonAtual;
    private final List<Timeline> animacoesEstatisticas = new ArrayList<>();

    @FXML
    private void initialize() {
        try {
            pokemons = new PokemonDAO().listarTodos();
            Map<String, Pokemon> porNome = new HashMap<>();
            for (Pokemon pokemon : pokemons) {
                if (pokemon.getNome() != null) {
                    porNome.put(normalizarNome(pokemon.getNome()), pokemon);
                }
            }
            pokemonsPorNome = Map.copyOf(porNome);
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar a Pokédex", e);
        }
    }

    public void setPokemon(Pokemon pokemon) {
        if (pokemon == null) {
            throw new IllegalArgumentException("O Pokémon dos detalhes não pode ser nulo.");
        }
        pokemonAtual = pokemon;
        atualizarTela();
    }

    @FXML
    private void mostrarAnterior() {
        navegarParaPokemon(pokemonAtual.getNumero() - 1);
    }

    @FXML
    private void mostrarProximo() {
        navegarParaPokemon(pokemonAtual.getNumero() + 1);
    }

    @FXML
    private void voltarParaPokedex() throws IOException {
        App.setRoot("pokedex");
    }

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

    private void navegarParaPokemon(int numero) {
        pokemons.stream()
                .filter(pokemon -> pokemon.getNumero() == numero)
                .findFirst()
                .ifPresent(this::setPokemon);
    }

    private void atualizarTela() {
        nomePokemonLabel.setText(capitalizar(pokemonAtual.getNome())
                + " - #" + String.format("%03d", pokemonAtual.getNumero()));
        previousButton.setText(textoVizinho(pokemonAtual.getNumero() - 1, true));
        nextButton.setText(textoVizinho(pokemonAtual.getNumero() + 1, false));
        previousButton.setDisable(pokemonAtual.getNumero() <= 1 || pokemons.isEmpty());
        nextButton.setDisable(pokemonAtual.getNumero() >= 151 || pokemons.isEmpty());
        atualizarSprite(pokemonAtual);

        descricaoLabel.setText(pokemonAtual.getDescricaoSegura());
        alturaLabel.setText(formatarMedida(pokemonAtual.getAltura()) + " m");
        pesoLabel.setText(formatarMedida(pokemonAtual.getPeso()) + " kg");
        sexoLabel.setText(pokemonAtual.getSexo());
        categoriaLabel.setText(textoSeguro(pokemonAtual.getCategoria(), "Indefinida"));
        atualizarHabilidades();
        atualizarCategoriaEspecial();
        atualizarTipo(tipo1Label, pokemonAtual.getTipo1(), true);
        atualizarTipo(tipo2Label, pokemonAtual.getTipo2(), false);
        atualizarDefesas(fraquezasRow, fraquezasContainer, pokemonAtual.getFraquezas());
        atualizarDefesas(resistenciasRow, resistenciasContainer, pokemonAtual.getResistencias());
        atualizarDefesas(imunidadesRow, imunidadesContainer, pokemonAtual.getImunidades());
        atualizarEstatisticas();
        atualizarEvolucoes();
    }

    private String textoVizinho(int numero, boolean anterior) {
        return pokemons.stream()
                .filter(pokemon -> pokemon.getNumero() == numero)
                .findFirst()
                .map(pokemon -> anterior
                        ? "← #" + String.format("%03d", numero) + " " + capitalizar(pokemon.getNome())
                        : capitalizar(pokemon.getNome()) + " #" + String.format("%03d", numero) + " →")
                .orElse(anterior ? "← Fim da Pokédex" : "Fim da Pokédex →");
    }

    private void atualizarSprite(Pokemon pokemon) {
        spriteImage.setImage(SpriteImageLoader.loadPokemon(pokemon.getNumero(), 350));
        spriteImage.setSmooth(false);
        if (!spriteImage.getStyleClass().contains("pixel-art")) {
            spriteImage.getStyleClass().add("pixel-art");
        }
        spriteImage.setOpacity(0);
        spriteImage.setScaleX(0.94);
        spriteImage.setScaleY(0.94);
        ParallelTransition entrada = new ParallelTransition(
                new FadeTransition(Duration.millis(180), spriteImage),
                new ScaleTransition(Duration.millis(180), spriteImage)
        );
        ((FadeTransition) entrada.getChildren().get(0)).setToValue(1);
        ScaleTransition escala = (ScaleTransition) entrada.getChildren().get(1);
        escala.setToX(1);
        escala.setToY(1);
        entrada.play();
    }

    private void atualizarHabilidades() {
        habilidadesContainer.getChildren().clear();
        adicionarHabilidade(pokemonAtual.getHabilidade1(), "Habilidade 1");
        adicionarHabilidade(pokemonAtual.getHabilidade2(), "Habilidade 2");
        adicionarHabilidade(pokemonAtual.getHabilidadeOculta(), "Habilidade Oculta");
        if (habilidadesContainer.getChildren().isEmpty()) {
            habilidadesContainer.getChildren().add(new Label("A definir"));
        }
    }

    private void adicionarHabilidade(String habilidade, String rotulo) {
        if (habilidade == null || habilidade.isBlank()) {
            return;
        }
        Label label = new Label(rotulo + ": " + capitalizar(habilidade));
        label.getStyleClass().add("field-value");
        habilidadesContainer.getChildren().add(label);
    }

    private void atualizarCategoriaEspecial() {
        if (pokemonAtual.isMitico()) {
            categoriaEspecialLabel.setText("Mítico");
        } else if (pokemonAtual.isLendario()) {
            categoriaEspecialLabel.setText("Lendário");
        } else if (pokemonAtual.isPseudoLendario()) {
            categoriaEspecialLabel.setText("Pseudo-lendário");
        } else {
            categoriaEspecialLabel.setText("Comum");
        }
    }

    private void atualizarTipo(Label label, String tipo, boolean principal) {
        if (tipo == null || tipo.isBlank()) {
            label.setText(principal ? "A definir" : "");
            label.setStyle(principal ? "-fx-text-fill: #94a3b8;" : "");
            return;
        }
        label.setText(capitalizar(tipo));
        label.setStyle(estiloBadge(tipo));
    }

    private void atualizarDefesas(HBox linha, FlowPane container, List<String> tipos) {
        container.getChildren().clear();
        if (tipos != null) {
            tipos.stream()
                    .filter(tipo -> tipo != null && !tipo.isBlank())
                    .distinct()
                    .forEach(tipo -> container.getChildren().add(criarBadge(tipo)));
        }
        boolean possuiTipos = !container.getChildren().isEmpty();
        linha.setVisible(possuiTipos);
        linha.setManaged(possuiTipos);
    }

    private void atualizarEstatisticas() {
        animacoesEstatisticas.forEach(Timeline::stop);
        animacoesEstatisticas.clear();
        statsChartContainer.getChildren().clear();
        adicionarEstatistica("HP", pokemonAtual.getHp());
        adicionarEstatistica("Ataque", pokemonAtual.getAtaque());
        adicionarEstatistica("Defesa", pokemonAtual.getDefesa());
        adicionarEstatistica("Atq. Esp.", pokemonAtual.getAtaqueEspecial());
        adicionarEstatistica("Def. Esp.", pokemonAtual.getDefesaEspecial());
        adicionarEstatistica("Veloc.", pokemonAtual.getVelocidade());
    }

    private void adicionarEstatistica(String nome, int valor) {
        VBox coluna = new VBox(4);
        coluna.getStyleClass().add("stat-column");
        HBox.setHgrow(coluna, javafx.scene.layout.Priority.ALWAYS);

        Label valorLabel = new Label(String.valueOf(valor));
        valorLabel.getStyleClass().add("stat-value");
        valorLabel.setMinHeight(16);
        valorLabel.setMaxWidth(Double.MAX_VALUE);
        valorLabel.setAlignment(javafx.geometry.Pos.CENTER);

        Region barra = new Region();
        barra.getStyleClass().add("stat-bar");
        double altura = Math.max(3, Math.min(120, Math.max(0, valor) / 255.0 * 120));
        barra.setMinHeight(0);
        barra.setPrefHeight(0);
        barra.setMaxHeight(0);

        StackPane trilho = new StackPane(barra);
        trilho.getStyleClass().add("stat-track");
        trilho.setMinWidth(0);
        StackPane.setAlignment(barra, Pos.BOTTOM_CENTER);

        Label nomeLabel = new Label(nome);
        nomeLabel.getStyleClass().add("stat-name");
        nomeLabel.setMinHeight(28);
        nomeLabel.setMaxWidth(Double.MAX_VALUE);
        nomeLabel.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        nomeLabel.setWrapText(true);

        coluna.getChildren().addAll(valorLabel, trilho, nomeLabel);
        statsChartContainer.getChildren().add(coluna);
        Timeline animacao = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(barra.prefHeightProperty(), 0),
                        new KeyValue(barra.maxHeightProperty(), 0)),
                new KeyFrame(Duration.millis(250),
                        new KeyValue(barra.prefHeightProperty(), altura, Interpolator.EASE_OUT),
                        new KeyValue(barra.maxHeightProperty(), altura, Interpolator.EASE_OUT))
        );
        animacoesEstatisticas.add(animacao);
        animacao.play();
    }

    private void atualizarEvolucoes() {
        anteriorContainer.setAlignment(Pos.CENTER);
        posteriorContainer.setAlignment(Pos.CENTER);
        anteriorContainer.getChildren().clear();
        posteriorContainer.getChildren().clear();

        List<Pokemon> anteriores = resolverEvolucoes(pokemonAtual.getEvoluiDe());
        if (anteriores.isEmpty()) {
            anteriorContainer.getChildren().add(placeholder("Estágio inicial / Não possui evolução anterior"));
        } else {
            anteriores.stream().findFirst()
                    .ifPresent(pokemon -> anteriorContainer.getChildren().add(criarCartaoEvolucao(pokemon)));
        }

        List<Pokemon> posteriores = resolverEvolucoes(pokemonAtual.getEvoluiPara());
        if (posteriores.isEmpty()) {
            posteriorContainer.getChildren().add(placeholder("Estágio final / Não evolui"));
        } else {
            posteriores.forEach(pokemon ->
                    posteriorContainer.getChildren().add(criarCartaoEvolucao(pokemon)));
        }
    }

    private List<Pokemon> resolverEvolucoes(String nomes) {
        if (nomes == null || nomes.isBlank()) {
            return List.of();
        }
        List<Pokemon> encontrados = new ArrayList<>();
        for (String nome : nomes.split(",")) {
            Pokemon pokemon = pokemonsPorNome.get(normalizarNome(nome));
            if (pokemon != null) {
                encontrados.add(pokemon);
            }
        }
        return encontrados;
    }

    private HBox criarCartaoEvolucao(Pokemon pokemon) {
        ImageView sprite = new ImageView();
        sprite.setFitWidth(64);
        sprite.setFitHeight(64);
        sprite.setPreserveRatio(true);
        sprite.setSmooth(false);
        sprite.setImage(SpriteImageLoader.loadPokemon(pokemon.getNumero(), 64));
        sprite.getStyleClass().add("pixel-art");

        Label nome = new Label(capitalizar(pokemon.getNome()));
        nome.getStyleClass().add("evolution-name");
        Label numero = new Label("#" + String.format("%03d", pokemon.getNumero()));
        numero.getStyleClass().add("evolution-number");
        VBox identificacao = new VBox(4, nome, numero);
        identificacao.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        HBox cartao = new HBox(8, sprite, identificacao);
        cartao.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        cartao.getStyleClass().add("evolution-entry");
        cartao.setCursor(Cursor.HAND);
        cartao.setOnMouseEntered(event -> animarCardEvolucao(cartao, -3));
        cartao.setOnMouseExited(event -> animarCardEvolucao(cartao, 0));
        cartao.setOnMouseClicked(event -> {
            AudioService.tocarClique();
            setPokemon(pokemon);
            event.consume();
        });
        return cartao;
    }

    private void animarCardEvolucao(HBox cartao, double translateY) {
        javafx.animation.TranslateTransition transicao =
                new javafx.animation.TranslateTransition(Duration.millis(160), cartao);
        transicao.setToY(translateY);
        transicao.play();
        cartao.setEffect(translateY < 0
                ? new javafx.scene.effect.DropShadow(10, 0, 3, javafx.scene.paint.Color.rgb(191, 8, 38, 0.16))
                : null);
    }

    private Label criarBadge(String tipo) {
        Label badge = new Label(capitalizar(tipo));
        badge.setStyle(estiloBadge(tipo));
        return badge;
    }

    private String estiloBadge(String tipo) {
        String cor = CORES_TIPO.getOrDefault(tipo.toLowerCase(Locale.ROOT), "#64748b");
        return "-fx-background-color: " + cor + "; -fx-text-fill: white; "
                + "-fx-background-radius: 20; -fx-padding: 5 10; -fx-font-size: 11px; -fx-font-weight: bold;";
    }

    private Label placeholder(String texto) {
        Label label = new Label(texto);
        label.getStyleClass().add("placeholder");
        label.setWrapText(true);
        return label;
    }

    private String formatarMedida(double medida) {
        DecimalFormat formato = new DecimalFormat("0.#",
                DecimalFormatSymbols.getInstance(Locale.forLanguageTag("pt-BR")));
        return formato.format(medida);
    }

    private String textoSeguro(String valor, String padrao) {
        return valor == null || valor.isBlank() ? padrao : capitalizar(valor);
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isBlank()) {
            return "";
        }
        return texto.substring(0, 1).toUpperCase(Locale.ROOT) + texto.substring(1);
    }

    private String normalizarNome(String nome) {
        return nome == null ? "" : nome.trim().toLowerCase(Locale.ROOT);
    }

    private void mostrarErro(String titulo, Exception erro) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText("Não foi possível carregar todos os dados da Pokédex.");
        alerta.setContentText(erro.getMessage());
        alerta.showAndWait();
    }
}
