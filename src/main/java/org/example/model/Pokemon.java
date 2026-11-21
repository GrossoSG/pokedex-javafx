package org.example.model;

public class Pokemon {

    // Atributos

    private int numero;
    private String nome;

    private String tipo1;
    private String tipo2;

    private int hp;
    private int ataque;
    private int defesa;
    private int ataqueEspecial;
    private int defesaEspecial;
    private int velocidade;

    // Construtor

    public Pokemon(int numero, String nome, String tipo1,
                   String tipo2, int hp, int ataque, int defesa,
                   int ataqueEspecial, int defesaEspecial,
                   int velocidade) {
        this.numero = numero;
        this.nome = nome;
        this.tipo1 = tipo1;
        this.tipo2 = tipo2;
        this.hp = hp;
        this.ataque = ataque;
        this.defesa = defesa;
        this.ataqueEspecial = ataqueEspecial;
        this.defesaEspecial = defesaEspecial;
        this.velocidade = velocidade;
    }

    // Getters

    public int getNumero() {
        return numero;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo1() {
        return tipo1;
    }

    public String getTipo2() {
        return tipo2;
    }

    public int getHp() {
        return hp;
    }

    public int getAtaque() {
        return ataque;
    }

    public int getDefesa() {
        return defesa;
    }

    public int getAtaqueEspecial() {
        return ataqueEspecial;
    }

    public int getDefesaEspecial() {
        return defesaEspecial;
    }

    public int getVelocidade() {
        return velocidade;
    }

    // Total

    public int calcularTotal(){
        int total = hp + ataque + defesa + ataqueEspecial + defesaEspecial + velocidade;
        return total;
    }
}
