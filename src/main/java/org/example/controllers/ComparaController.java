package org.example.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseButton;
import javafx.util.StringConverter;
import org.example.App;
import org.example.dao.FavoritoDAO;
import org.example.dao.PokemonDAO;
import org.example.model.Pokemon;

public class ComparaController {

    private static final int MAX_POKEMON = 4;
    private static final double COLUNA_ROTULOS = 18;
    private static final String STAR_ON = "/sprites/Star-on.png";
    private static final String STAR_OFF = "/sprites/Star-offpng.png";
    private static final java.util.Map<String, String> CORES_TIPO = java.util.Map.ofEntries(
            java.util.Map.entry("bug", "#91a119"), java.util.Map.entry("dark", "#624d4e"),
            java.util.Map.entry("dragon", "#5060e1"), java.util.Map.entry("electric", "#fac000"),
            java.util.Map.entry("fairy", "#ef70ef"), java.util.Map.entry("fighting", "#ff8000"),
            java.util.Map.entry("fire", "#e62829"), java.util.Map.entry("flying", "#81b9ef"),
            java.util.Map.entry("ghost", "#704170"), java.util.Map.entry("grass", "#3fa129"),
            java.util.Map.entry("ground", "#915121"), java.util.Map.entry("ice", "#3dcef3"),
            java.util.Map.entry("normal", "#9fa19f"), java.util.Map.entry("poison", "#9141cb"),
            java.util.Map.entry("psychic", "#ef4179"), java.util.Map.entry("rock", "#afa981"),
            java.util.Map.entry("steel", "#60a1b8"), java.util.Map.entry("water", "#2980ef")
    );

    @FXML private ComboBox<Pokemon> pokemonCombo;
    @FXML private Button adicionarButton;
    @FXML private Button limparButton;
    @FXML private Label orientacaoLabel;
    @FXML private Label contadorLabel;
    @FXML private VBox comparisonContent;

    private final ObservableList<Pokemon> todosPokemons = FXCollections.observableArrayList();
    private FilteredList<Pokemon> pokemonsFiltrados;
    private final ObservableList<Pokemon> selecionados = FXCollections.observableArrayList();
    private final Set<Integer> numerosFavoritos = new HashSet<>();
    private final PokemonDAO pokemonDAO = new PokemonDAO();
    private final FavoritoDAO favoritoDAO = new FavoritoDAO();

    @FXML
    private void initialize() {
        try {
            todosPokemons.setAll(pokemonDAO.listarTodos());
            favoritoDAO.listarFavoritos().forEach(pokemon -> numerosFavoritos.add(pokemon.getNumero()));
            configurarSeletor();
            atualizarComparacao();
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar a comparação", e);
            adicionarButton.setDisable(true);
        }
    }

    private void configurarSeletor() {
        pokemonCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Pokemon pokemon) {
                return pokemon == null ? "" : formatarPokemon(pokemon);
            }

            @Override
            public Pokemon fromString(String texto) {
                return procurarPokemon(texto);
            }
        });
        pokemonsFiltrados = new FilteredList<>(todosPokemons);
        pokemonCombo.setItems(pokemonsFiltrados);
        pokemonCombo.setCellFactory(combo -> criarCelulaPokemon());
        pokemonCombo.setButtonCell(criarCelulaPokemon());
        TextField editor = pokemonCombo.getEditor();
        editor.textProperty().addListener((observable, anterior, texto) -> {
            if (pokemonCombo.getValue() != null
                    && formatarPokemon(pokemonCombo.getValue()).equals(texto)) {
                return;
            }
            atualizarFiltroSeletor();
            if (pokemonsFiltrados.size() > 0 && texto != null && !texto.isBlank()) {
                pokemonCombo.show();
            }
        });
        editor.setOnAction(event -> adicionarPokemon());
        pokemonCombo.setOnAction(event -> {
            Pokemon escolhido = pokemonCombo.getValue();
            if (escolhido != null) {
                pokemonCombo.getEditor().setText(formatarPokemon(escolhido));
            }
        });
    }

    @FXML
    private void adicionarPokemon() {
        String textoEditor = pokemonCombo.getEditor().getText();
        Pokemon pokemon = textoEditor == null || textoEditor.isBlank()
                ? pokemonCombo.getValue()
                : procurarPokemon(textoEditor);
        if (pokemon == null) {
            orientacaoLabel.setText("Selecione um Pokémon válido para adicionar.");
            return;
        }
        adicionarPokemon(pokemon);
    }

    private void adicionarPokemon(Pokemon pokemon) {
        Pokemon candidato = pokemon;
        if (selecionados.stream().anyMatch(item -> item.getNumero() == candidato.getNumero())) {
            orientacaoLabel.setText("Esse Pokémon já está na comparação.");
            return;
        }
        if (selecionados.size() >= MAX_POKEMON) {
            atualizarComparacao();
            return;
        }

        selecionados.add(pokemon);
        pokemonCombo.setValue(null);
        pokemonCombo.getEditor().clear();
        pokemonCombo.hide();
        atualizarComparacao();
    }

    @FXML
    private void limparComparacao() {
        selecionados.clear();
        atualizarComparacao();
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

    private void atualizarComparacao() {
        int quantidade = selecionados.size();
        contadorLabel.setText(quantidade + " de " + MAX_POKEMON + " Pokémon");
        boolean noLimite = quantidade >= MAX_POKEMON;
        pokemonCombo.setDisable(noLimite);
        adicionarButton.setDisable(noLimite);
        orientacaoLabel.setText(quantidade < 2
                ? "Adicione pelo menos 2 Pokémon para comparar."
                : noLimite ? "Limite máximo de 4 Pokémon atingido." : "Compare até 4 Pokémon.");
        renderizarTabela();
        pokemonCombo.setCellFactory(combo -> criarCelulaPokemon());
        pokemonCombo.setButtonCell(criarCelulaPokemon());
        atualizarFiltroSeletor();
    }

    private void atualizarFiltroSeletor() {
        String busca = pokemonCombo.getEditor().getText();
        String termo = busca == null ? "" : busca.trim();
        pokemonsFiltrados.setPredicate(pokemon -> correspondeBusca(pokemon, termo));
    }

    private void renderizarTabela() {
        comparisonContent.getChildren().clear();
        if (selecionados.isEmpty()) {
            Label vazio = new Label("Adicione Pokémon acima para iniciar a comparação.");
            vazio.getStyleClass().add("empty-state");
            vazio.setMinHeight(120);
            vazio.setMaxWidth(Double.MAX_VALUE);
            vazio.setAlignment(Pos.CENTER);
            comparisonContent.getChildren().add(vazio);
            return;
        }

        GridPane grade = new GridPane();
        grade.getStyleClass().add("comparison-grid");
        grade.setMaxWidth(Double.MAX_VALUE);
        configurarColunas(grade);
        int colunasTabela = selecionados.size() + 1;
        int linha = 0;

        Label rotuloCabecalho = new Label("Atributo");
        rotuloCabecalho.getStyleClass().add("table-header-label");
        grade.add(rotuloCabecalho, 0, linha);
        for (int i = 0; i < selecionados.size(); i++) {
            grade.add(criarCabecalhoPokemon(selecionados.get(i)), i + 1, linha);
        }
        linha++;

        linha = adicionarFaixa(grade, "Estatísticas Base", linha, colunasTabela);
        String[] nomesStats = {"HP", "Ataque", "Defesa", "Atq. Esp.", "Def. Esp.", "Velocidade", "Total"};
        for (int i = 0; i < nomesStats.length; i++) {
            int indiceStat = i;
            adicionarRotulo(grade, nomesStats[i], linha, "stat-row-label");
            for (int coluna = 0; coluna < selecionados.size(); coluna++) {
                Pokemon pokemon = selecionados.get(coluna);
                int valor = obterStat(pokemon, indiceStat);
                int maior = selecionados.stream()
                        .mapToInt(item -> obterStat(item, indiceStat))
                        .max().orElse(valor);
                grade.add(criarCelulaStat(valor, maior, indiceStat == nomesStats.length - 1), coluna + 1, linha);
            }
            linha++;
        }

        linha = adicionarFaixa(grade, "Relações de Tipagem", linha, colunasTabela);
        linha = adicionarLinhaTipos(grade, "Fraquezas", linha, Pokemon::getFraquezas);
        linha = adicionarLinhaTipos(grade, "Resistências", linha, Pokemon::getResistencias);
        adicionarLinhaTipos(grade, "Imunidades", linha, Pokemon::getImunidades);

        comparisonContent.getChildren().add(grade);
    }

    private void configurarColunas(GridPane grade) {
        grade.getColumnConstraints().clear();
        ColumnConstraints rotulo = new ColumnConstraints();
        rotulo.setPercentWidth(COLUNA_ROTULOS);
        rotulo.setMinWidth(105);
        rotulo.setHgrow(Priority.NEVER);
        grade.getColumnConstraints().add(rotulo);

        double percentual = (100 - COLUNA_ROTULOS) / selecionados.size();
        for (int i = 0; i < selecionados.size(); i++) {
            ColumnConstraints coluna = new ColumnConstraints();
            coluna.setPercentWidth(percentual);
            coluna.setHgrow(Priority.ALWAYS);
            coluna.setFillWidth(true);
            grade.getColumnConstraints().add(coluna);
        }
    }

    private VBox criarCabecalhoPokemon(Pokemon pokemon) {
        VBox cabecalho = new VBox(7);
        cabecalho.setAlignment(Pos.CENTER);
        cabecalho.getStyleClass().add("pokemon-column-header");

        HBox topo = new HBox();
        topo.setAlignment(Pos.CENTER_RIGHT);
        Button remover = new Button("✕");
        remover.getStyleClass().add("remove-button");
        remover.setOnAction(event -> {
            selecionados.removeIf(item -> item.getNumero() == pokemon.getNumero());
            atualizarComparacao();
        });
        topo.getChildren().add(remover);

        ImageView sprite = new ImageView();
        sprite.setFitWidth(96);
        sprite.setFitHeight(96);
        sprite.setPreserveRatio(true);
        sprite.setSmooth(false);
        var spriteUrl = getClass().getResource("/sprites/" + pokemon.getNumero() + ".png");
        if (spriteUrl != null) {
            sprite.setImage(new Image(spriteUrl.toExternalForm()));
        }

        Label nome = new Label(capitalizar(pokemon.getNome()));
        nome.getStyleClass().add("pokemon-name");
        Label numero = new Label(String.format("#%03d", pokemon.getNumero()));
        numero.getStyleClass().add("pokemon-number");

        FlowPane tipos = new FlowPane(6, 6);
        tipos.setAlignment(Pos.CENTER);
        adicionarBadgeTipo(tipos, pokemon.getTipo1());
        adicionarBadgeTipo(tipos, pokemon.getTipo2());

        Button favorito = new Button();
        favorito.getStyleClass().add("favorite-button");
        favorito.setGraphic(criarIconeFavorito(pokemon));
        atualizarTooltipFavorito(favorito, pokemon);
        favorito.setOnAction(event -> alternarFavorito(pokemon, favorito));

        cabecalho.getChildren().addAll(topo, sprite, nome, numero, tipos, favorito);
        return cabecalho;
    }

    private int adicionarFaixa(GridPane grade, String titulo, int linha, int colunas) {
        Label faixa = new Label(titulo);
        faixa.getStyleClass().add("section-band");
        faixa.setMaxWidth(Double.MAX_VALUE);
        GridPane.setColumnSpan(faixa, colunas);
        grade.add(faixa, 0, linha);
        return linha + 1;
    }

    private void adicionarRotulo(GridPane grade, String texto, int linha, String estilo) {
        Label rotulo = new Label(texto);
        rotulo.getStyleClass().add(estilo);
        rotulo.setMaxWidth(Double.MAX_VALUE);
        rotulo.setMinHeight(56);
        rotulo.setAlignment(Pos.CENTER_LEFT);
        grade.add(rotulo, 0, linha);
    }

    private VBox criarCelulaStat(int valor, int maior, boolean total) {
        VBox celula = new VBox(6);
        celula.setAlignment(Pos.CENTER);
        celula.getStyleClass().add("stat-cell");
        Label numero = new Label(String.valueOf(valor));
        boolean destacado = selecionados.size() >= 2 && valor == maior;
        numero.getStyleClass().add(destacado ? "stat-number-best" : "stat-number");
        ProgressBar barra = new ProgressBar(total ? Math.min(1, valor / 765.0) : Math.min(1, valor / 255.0));
        barra.setMaxWidth(Double.MAX_VALUE);
        barra.getStyleClass().add("stat-progress");
        if (total) {
            barra.getStyleClass().add("total-progress");
        }
        if (destacado) {
            barra.getStyleClass().add("stat-progress-best");
        }
        celula.getChildren().addAll(numero, barra);
        return celula;
    }

    private int adicionarLinhaTipos(GridPane grade, String rotulo, int linha,
                                    java.util.function.Function<Pokemon, List<String>> fornecedor) {
        adicionarRotulo(grade, rotulo, linha, "relation-label");
        for (int i = 0; i < selecionados.size(); i++) {
            FlowPane badges = new FlowPane(6, 6);
            badges.setAlignment(Pos.CENTER);
            badges.setMinHeight(58);
            badges.getStyleClass().add("relation-cell");
            List<String> tipos = fornecedor.apply(selecionados.get(i));
            if (tipos != null) {
                tipos.stream().filter(tipo -> tipo != null && !tipo.isBlank())
                        .distinct().forEach(tipo -> adicionarBadgeTipo(badges, tipo));
            }
            grade.add(badges, i + 1, linha);
        }
        return linha + 1;
    }

    private void adicionarBadgeTipo(FlowPane destino, String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return;
        }
        Label badge = new Label(capitalizar(tipo));
        badge.getStyleClass().add("type-badge");
        badge.setStyle("-fx-background-color: "
                + CORES_TIPO.getOrDefault(tipo.toLowerCase(Locale.ROOT), "#64748b") + ";");
        destino.getChildren().add(badge);
    }

    private void alternarFavorito(Pokemon pokemon, Button botao) {
        int numero = pokemon.getNumero();
        boolean eraFavorito = numerosFavoritos.contains(numero);
        try {
            if (eraFavorito) {
                favoritoDAO.removerFavorito(numero);
                numerosFavoritos.remove(numero);
            } else {
                favoritoDAO.adicionarFavorito(numero);
                numerosFavoritos.add(numero);
            }
            botao.setGraphic(criarIconeFavorito(pokemon));
            atualizarTooltipFavorito(botao, pokemon);
        } catch (SQLException e) {
            mostrarErro("Erro ao atualizar favoritos", e);
        }
    }

    private ImageView criarIconeFavorito(Pokemon pokemon) {
        boolean favorito = numerosFavoritos.contains(pokemon.getNumero());
        var url = getClass().getResource(favorito ? STAR_ON : STAR_OFF);
        if (url == null) {
            throw new IllegalStateException("Ícone de favorito não encontrado.");
        }
        ImageView icone = new ImageView(new Image(url.toExternalForm()));
        icone.setFitWidth(20);
        icone.setFitHeight(20);
        icone.setPreserveRatio(true);
        icone.setSmooth(false);
        return icone;
    }

    private void atualizarTooltipFavorito(Button botao, Pokemon pokemon) {
        botao.setText(numerosFavoritos.contains(pokemon.getNumero()) ? "Favorito" : "Favoritar");
        botao.setAccessibleText(botao.getText() + " " + pokemon.getNome());
    }

    private ListCell<Pokemon> criarCelulaPokemon() {
        ListCell<Pokemon> celula = new ListCell<>() {
            @Override
            protected void updateItem(Pokemon pokemon, boolean vazio) {
                super.updateItem(pokemon, vazio);
                if (vazio || pokemon == null) {
                    setText(null);
                    setGraphic(null);
                    setDisable(false);
                    return;
                }

                ImageView sprite = new ImageView();
                sprite.setFitWidth(40);
                sprite.setFitHeight(40);
                sprite.setPreserveRatio(true);
                sprite.setSmooth(false);
                var spriteUrl = getClass().getResource("/sprites/" + pokemon.getNumero() + ".png");
                if (spriteUrl != null) {
                    sprite.setImage(new Image(spriteUrl.toExternalForm()));
                }

                Label nome = new Label(formatarPokemon(pokemon));
                nome.getStyleClass().add("pokemon-option-name");
                HBox conteudo = new HBox(10, sprite, nome);
                conteudo.setAlignment(Pos.CENTER_LEFT);
                if (jaAdicionado(pokemon)) {
                    Label adicionado = new Label("Adicionado");
                    adicionado.getStyleClass().add("added-tag");
                    HBox.setHgrow(nome, Priority.ALWAYS);
                    conteudo.getChildren().add(adicionado);
                    setDisable(true);
                } else {
                    setDisable(false);
                }
                setText(null);
                setGraphic(conteudo);
                setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);
            }
        };
        celula.setOnMouseClicked(event -> {
            Pokemon pokemon = celula.getItem();
            if (event.getButton() == MouseButton.PRIMARY && pokemon != null && !jaAdicionado(pokemon)) {
                adicionarPokemon(pokemon);
                event.consume();
            }
        });
        return celula;
    }

    private boolean jaAdicionado(Pokemon pokemon) {
        return selecionados.stream().anyMatch(item -> item.getNumero() == pokemon.getNumero());
    }

    private boolean correspondeBusca(Pokemon pokemon, String texto) {
        if (texto == null || texto.isBlank()) {
            return true;
        }
        String termo = texto.trim().toLowerCase(Locale.ROOT);
        String nome = pokemon.getNome() == null ? "" : pokemon.getNome().toLowerCase(Locale.ROOT);
        String numeroTexto = termo.replace("#", "").replaceAll("\\s+", "");
        String numeroNormalizado = numeroTexto.replaceFirst("^0+(?!$)", "");
        return nome.contains(termo)
                || formatarPokemon(pokemon).toLowerCase(Locale.ROOT).contains(termo)
                || String.valueOf(pokemon.getNumero()).contains(numeroTexto)
                || String.valueOf(pokemon.getNumero()).contains(numeroNormalizado);
    }

    private Pokemon procurarPokemon(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        String busca = texto.trim().toLowerCase(Locale.ROOT);
        String numeroBusca = busca.replaceFirst("^#", "").trim();
        if (numeroBusca.matches("\\d+")) {
            try {
                int numero = Integer.parseInt(numeroBusca);
                return todosPokemons.stream()
                        .filter(pokemon -> pokemon.getNumero() == numero)
                        .findFirst()
                        .orElse(null);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        for (Pokemon pokemon : todosPokemons) {
            String formatado = formatarPokemon(pokemon).toLowerCase(Locale.ROOT);
            if (formatado.equals(busca)
                    || pokemon.getNome().equalsIgnoreCase(busca)) {
                return pokemon;
            }
        }
        return null;
    }

    private String formatarPokemon(Pokemon pokemon) {
        return String.format("#%03d %s", pokemon.getNumero(), capitalizar(pokemon.getNome()));
    }

    private int obterStat(Pokemon pokemon, int indice) {
        return switch (indice) {
            case 0 -> pokemon.getHp();
            case 1 -> pokemon.getAtaque();
            case 2 -> pokemon.getDefesa();
            case 3 -> pokemon.getAtaqueEspecial();
            case 4 -> pokemon.getDefesaEspecial();
            case 5 -> pokemon.getVelocidade();
            case 6 -> pokemon.calcularTotal();
            default -> 0;
        };
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
        alerta.setHeaderText("A comparação não pôde acessar os dados.");
        alerta.setContentText(erro.getMessage());
        alerta.showAndWait();
    }
}
