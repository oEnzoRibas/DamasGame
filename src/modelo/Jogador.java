package modelo;

import modelo.Peca.Cor;

public class Jogador {

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
