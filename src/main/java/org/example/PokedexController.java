package org.example;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.dao.PokemonDAO;
import org.example.model.Pokemon;
import org.example.service.AudioService;

public class PokedexController {

    private static final int COLUNAS = 5;
    private static final List<String> TIPOS = List.of(
            "bug", "dark", "dragon", "electric", "fairy", "fighting",
            "fire", "flying", "ghost", "grass", "ground", "ice",
            "normal", "poison", "psychic", "rock", "steel", "water"
    );
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

    @FXML private TextField campoBusca;
    @FXML private ComboBox<String> ordenacaoCombo;
    @FXML private ComboBox<String> tipoCombo;
    @FXML private ComboBox<String> quantidadeTiposCombo;
    @FXML private Label resultadoLabel;
    @FXML private GridPane gradePokemons;

    private List<Pokemon> pokemons = List.of();

    @FXML
    private void initialize() {
        for (int i = 0; i < COLUNAS; i++) {
            ColumnConstraints coluna = new ColumnConstraints();
            coluna.setPercentWidth(100.0 / COLUNAS);
            coluna.setHgrow(Priority.ALWAYS);
            gradePokemons.getColumnConstraints().add(coluna);
        }
        ordenacaoCombo.getItems().setAll("Menor para maior número", "Maior para menor número");
        ordenacaoCombo.getSelectionModel().selectFirst();
        tipoCombo.getItems().setAll("Todos os tipos");
        tipoCombo.getItems().addAll(TIPOS);
        tipoCombo.getSelectionModel().selectFirst();
        quantidadeTiposCombo.getItems().setAll("Qualquer combinação", "Tipo único", "Tipo duplo");
        quantidadeTiposCombo.getSelectionModel().selectFirst();
        campoBusca.textProperty().addListener((observable, anterior, atual) -> atualizarGrade());

        try {
            pokemons = new PokemonDAO().listarTodos();
            atualizarGrade();
        } catch (SQLException e) {
            resultadoLabel.setText("Não foi possível carregar os Pokémon.");
            mostrarErro("Erro ao carregar a Pokédex", e);
        }
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

    @FXML
    private void buscarPokemon() {
        atualizarGrade();
    }

    @FXML
    private void aplicarFiltros() {
        atualizarGrade();
    }

    @FXML
    private void limparFiltros() {
        campoBusca.clear();
        ordenacaoCombo.getSelectionModel().selectFirst();
        tipoCombo.getSelectionModel().selectFirst();
        quantidadeTiposCombo.getSelectionModel().selectFirst();
        atualizarGrade();
    }

    private void atualizarGrade() {
        if (gradePokemons == null) {
            return;
        }

        String busca = campoBusca.getText() == null
                ? ""
                : campoBusca.getText().trim().toLowerCase(Locale.ROOT);
        String tipoSelecionado = tipoCombo.getValue();
        String quantidadeTipos = quantidadeTiposCombo.getValue();
        Comparator<Pokemon> ordem = "Maior para menor número".equals(ordenacaoCombo.getValue())
                ? Comparator.comparingInt(Pokemon::getNumero).reversed()
                : Comparator.comparingInt(Pokemon::getNumero);

        List<Pokemon> filtrados = pokemons.stream()
                .filter(pokemon -> correspondeBusca(pokemon, busca))
                .filter(pokemon -> correspondeTipo(pokemon, tipoSelecionado, quantidadeTipos))
                .sorted(ordem)
                .collect(Collectors.toList());

        gradePokemons.getChildren().clear();
        for (int i = 0; i < filtrados.size(); i++) {
            int coluna = i % COLUNAS;
            int linha = i / COLUNAS;
            gradePokemons.add(criarCartao(filtrados.get(i)), coluna, linha);
            GridPane.setHgrow(gradePokemons.getChildren().get(gradePokemons.getChildren().size() - 1), Priority.ALWAYS);
        }
        resultadoLabel.setText(filtrados.size() + (filtrados.size() == 1
                ? " Pokémon encontrado"
                : " Pokémon encontrados"));
    }

    private boolean correspondeBusca(Pokemon pokemon, String busca) {
        if (busca.isEmpty()) {
            return true;
        }
        return pokemon.getNome().toLowerCase(Locale.ROOT).contains(busca)
                || String.valueOf(pokemon.getNumero()).contains(busca);
    }

    private boolean correspondeTipo(Pokemon pokemon, String tipoSelecionado, String quantidadeTipos) {
        boolean tipoSelecionadoAplica = tipoSelecionado != null && !"Todos os tipos".equals(tipoSelecionado);
        if (tipoSelecionadoAplica
                && !tipoSelecionado.equals(pokemon.getTipo1())
                && !tipoSelecionado.equals(pokemon.getTipo2())) {
            return false;
        }

        boolean temSegundoTipo = pokemon.getTipo2() != null && !pokemon.getTipo2().isBlank();
        if ("Tipo único".equals(quantidadeTipos)) {
            return !temSegundoTipo;
        }
        if ("Tipo duplo".equals(quantidadeTipos)) {
            return temSegundoTipo;
        }
        return true;
    }

    private VBox criarCartao(Pokemon pokemon) {
        VBox cartao = new VBox(7);
        cartao.setAlignment(javafx.geometry.Pos.CENTER);
        cartao.setPrefWidth(190);
        cartao.setMinWidth(150);
        cartao.setMaxWidth(Double.MAX_VALUE);
        cartao.setCursor(Cursor.HAND);
        cartao.setStyle("-fx-background-color: white; -fx-background-radius: 12; "
                + "-fx-border-color: #e2e8f0; -fx-border-radius: 12; -fx-padding: 12;");
        cartao.setOnMouseClicked(event -> {
            AudioService.tocarClique();
            try {
                App.mostrarDetalhes(pokemon);
            } catch (IOException e) {
                mostrarErro("Erro ao abrir os detalhes", e);
            }
        });

        Label numero = new Label(String.format("#%03d", pokemon.getNumero()));
        numero.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-font-weight: bold;");

        ImageView sprite = new ImageView();
        sprite.setFitWidth(112);
        sprite.setFitHeight(112);
        sprite.setPreserveRatio(true);
        var spriteUrl = getClass().getResource("/sprites/" + pokemon.getNumero() + ".png");
        if (spriteUrl != null) {
            sprite.setImage(new Image(spriteUrl.toExternalForm(), true));
        }

        Label nome = new Label(capitalizar(pokemon.getNome()));
        nome.setStyle("-fx-text-fill: #172033; -fx-font-size: 16px; -fx-font-weight: bold;");

        javafx.scene.layout.HBox tipos = new javafx.scene.layout.HBox(6);
        tipos.setAlignment(javafx.geometry.Pos.CENTER);
        if (pokemon.getTipo1() != null && !pokemon.getTipo1().isBlank()) {
            tipos.getChildren().add(criarEtiquetaTipo(pokemon.getTipo1()));
        }
        if (pokemon.getTipo2() != null && !pokemon.getTipo2().isBlank()) {
            tipos.getChildren().add(criarEtiquetaTipo(pokemon.getTipo2()));
        }

        cartao.getChildren().addAll(numero, sprite, nome, tipos);
        return cartao;
    }

    private Label criarEtiquetaTipo(String tipo) {
        Label etiqueta = new Label(capitalizar(tipo));
        String cor = CORES_TIPO.getOrDefault(tipo, "#64748b");
        etiqueta.setStyle("-fx-background-color: " + cor + "; -fx-text-fill: white; "
                + "-fx-background-radius: 20; -fx-padding: 4 9; -fx-font-size: 11px; -fx-font-weight: bold;");
        return etiqueta;
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isBlank()) {
            return "";
        }
        return texto.substring(0, 1).toUpperCase(Locale.ROOT) + texto.substring(1);
    }

    private void mostrarErro(String titulo, Exception erro) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText("A Pokédex não pôde acessar os dados.");
        alerta.setContentText(erro.getMessage());
        alerta.showAndWait();
    }
}
