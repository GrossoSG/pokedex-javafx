package org.example.util;

import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public final class SpriteImageLoader {

    private record ImageKey(String path, int width, int height) {}

    private static final ConcurrentMap<ImageKey, Image> CACHE = new ConcurrentHashMap<>();

    private SpriteImageLoader() {}

    public static Image loadPokemon(int numero, int tamanho) {
        return loadResource("/sprites/" + numero + ".png", tamanho, tamanho);
    }

    public static Image loadResource(String caminho, int largura, int altura) {
        if (largura <= 0 || altura <= 0) {
            throw new IllegalArgumentException("As dimensões da imagem devem ser positivas.");
        }
        ImageKey chave = new ImageKey(caminho, largura, altura);
        if (largura > 128 || altura > 128) {
            return carregarEAmpliar(chave);
        }
        return CACHE.computeIfAbsent(chave, SpriteImageLoader::carregarEAmpliar);
    }

    private static Image carregarEAmpliar(ImageKey chave) {
        URL recurso = SpriteImageLoader.class.getResource(chave.path());
        if (recurso == null) {
            throw new IllegalArgumentException("Imagem não encontrada: " + chave.path());
        }

        Image original = new Image(recurso.toExternalForm(), 0, 0, true, false);
        if (original.isError()) {
            throw new IllegalStateException("Não foi possível carregar a imagem: " + chave.path(),
                    original.getException());
        }

        int larguraOriginal = (int) original.getWidth();
        int alturaOriginal = (int) original.getHeight();
        if (larguraOriginal == chave.width() && alturaOriginal == chave.height()) {
            return original;
        }

        PixelReader leitor = original.getPixelReader();
        if (leitor == null) {
            throw new IllegalStateException("A imagem não permite leitura de pixels: " + chave.path());
        }

        WritableImage redimensionada = new WritableImage(chave.width(), chave.height());
        PixelWriter escritor = redimensionada.getPixelWriter();
        for (int y = 0; y < chave.height(); y++) {
            int origemY = Math.min(alturaOriginal - 1, (int) ((y + 0.5) * alturaOriginal / chave.height()));
            for (int x = 0; x < chave.width(); x++) {
                int origemX = Math.min(larguraOriginal - 1,
                        (int) ((x + 0.5) * larguraOriginal / chave.width()));
                escritor.setArgb(x, y, leitor.getArgb(origemX, origemY));
            }
        }
        return redimensionada;
    }
}
