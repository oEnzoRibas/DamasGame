package modelo;

import modelo.Peca.Cor;

public class Jogador {

    /**
     * Representa um jogador no jogo de damas.
     * Cada jogador tem um nome e uma cor de peças (branca ou preta).
     */

    private String nome;
    private Cor corPecas;

    public Jogador(String nome, Cor corPecas) {
        this.nome = nome;
        this.corPecas = corPecas;
    }

    public String getNome() {
        return nome;
    }

    public Cor getCorPecas() {
        return corPecas;
    }

}

