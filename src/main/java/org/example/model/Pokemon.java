package org.example.model;

import java.util.ArrayList;
import java.util.List;

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

    private double altura;
    private double peso;
    private String categoria;

    private String habilidade1;
    private String habilidade2;
    private String habilidadeOculta;

    private int estagioEvolutivo;
    private String evoluiDe;
    private String evoluiPara;

    private boolean lendario;
    private boolean mitico;
    private boolean pseudoLendario;

    private String descricao;

    private final List<String> fraquezas = new ArrayList<>();
    private final List<String> resistencias = new ArrayList<>();
    private final List<String> imunidades = new ArrayList<>();

    // Construtor

    public Pokemon(
            int numero, String nome,
            String tipo1, String tipo2,
            int hp, int ataque, int defesa,
            int ataqueEspecial, int defesaEspecial,
            int velocidade,
            double altura, double peso,
            String categoria,
            String habilidade1, String habilidade2,
            String habilidadeOculta,
            int estagioEvolutivo,
            String evoluiDe, String evoluiPara,
            boolean pseudoLendario,
            boolean lendario, boolean mitico,
            String descricao
    ) {
        // Identificação
        this.numero = numero;
        this.nome = nome;
        this.tipo1 = tipo1;
        this.tipo2 = tipo2;
        // Atributos
        this.hp = hp;
        this.ataque = ataque;
        this.defesa = defesa;
        this.ataqueEspecial = ataqueEspecial;
        this.defesaEspecial = defesaEspecial;
        this.velocidade = velocidade;
        // Informações físicas
        this.altura = altura;
        this.peso = peso;
        this.categoria = categoria;
        // Habilidades
        this.habilidade1 = habilidade1;
        this.habilidade2 = habilidade2;
        this.habilidadeOculta = habilidadeOculta;
        // Evoluções
        this.estagioEvolutivo = estagioEvolutivo;
        this.evoluiDe = evoluiDe;
        this.evoluiPara = evoluiPara;
        // Classificações
        this.pseudoLendario = pseudoLendario;
        this.lendario = lendario;
        this.mitico = mitico;
        // Descrição
        this.descricao = descricao;
    }

    // Total

    public int calcularTotal(){
        int total = hp + ataque + defesa + ataqueEspecial + defesaEspecial + velocidade;
        return total;
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

    public double getAltura() {
        return altura;
    }

    public double getPeso() {
        return peso;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getHabilidade1() {
        return habilidade1;
    }

    public String getHabilidade2() {
        return habilidade2;
    }

    public String getHabilidadeOculta() {
        return habilidadeOculta;
    }

    public int getEstagioEvolutivo() {
        return estagioEvolutivo;
    }

    public String getEvoluiDe() {
        return evoluiDe;
    }

    public String getEvoluiPara() {
        return evoluiPara;
    }

    public boolean isLendario() {
        return lendario;
    }

    public boolean isMitico() {
        return mitico;
    }

    public boolean isPseudoLendario() {
        return pseudoLendario;
    }

    public String getDescricao() {
        return descricao;
    }

    public List<String> getFraquezas() {
        return List.copyOf(fraquezas);
    }

    public List<String> getResistencias() {
        return List.copyOf(resistencias);
    }

    public List<String> getImunidades() {
        return List.copyOf(imunidades);
    }

    // Relação defensiva


    public void adicionarRelacaoDefensiva(String tipo, String relacao) {

        switch (relacao) {
            case "fraqueza":
                fraquezas.add(tipo);
                break;

            case "resistencia":
                resistencias.add(tipo);
                break;

            case "imunidade":
                imunidades.add(tipo);
                break;
        }
    }


}
