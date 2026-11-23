package org.example.service;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class AudioService {

    private static MediaPlayer musica;

    private static AudioClip clique;
    private static AudioClip acerto;
    private static AudioClip erro;

    // CARREGAR EFEITO SONORO
    private static AudioClip carregarSom(String arquivo) {

        URL url = AudioService.class.getResource(
                "/audio/" + arquivo
        );

        if (url == null) {
            System.err.println("Áudio não encontrado: " + arquivo);
            return null;
        }

        try {
            return new AudioClip(url.toExternalForm());
        } catch (Exception e) {
            System.err.println("Erro ao carregar " + arquivo
                    + ": " + e.getMessage());
            return null;
        }
    }


    // CLIQUE
    public static void tocarClique() {

        if (clique == null) {
            clique = carregarSom("click.wav");

            if (clique != null) {
                clique.setVolume(0.4);
            }
        }

        if (clique != null) {
            clique.play();
        }
    }


    // ACERTO
    public static void tocarAcerto() {

        if (acerto == null) {
            acerto = carregarSom("acerto.wav");

            if (acerto != null) {
                acerto.setVolume(0.6);
            }
        }

        if (acerto != null) {
            acerto.play();
        }
    }


    // ERRO
    public static void tocarErro() {

        if (erro == null) {
            erro = carregarSom("erro.wav");

            if (erro != null) {
                erro.setVolume(0.4);
            }
        }

        if (erro != null) {
            erro.play();
        }
    }


    // MÚSICA DE FUNDO
    public static void iniciarMusica() {

        if (musica != null) {
            return;
        }

        URL url = AudioService.class.getResource(
                "/audio/fundo.mp3"
        );

        if (url == null) {
            System.err.println("Música não encontrada!");
            return;
        }

        Media media = new Media(url.toExternalForm());

        musica = new MediaPlayer(media);

        musica.setCycleCount(MediaPlayer.INDEFINITE);
        musica.setVolume(0.2);

        musica.setOnError(() ->
                System.err.println("Erro na música: "
                        + musica.getError())
        );

        musica.play();
    }


    // ENCERRAR MÚSICA
    public static void pararMusica() {

        if (musica != null) {
            musica.stop();
            musica.dispose();
            musica = null;
        }
    }
}
