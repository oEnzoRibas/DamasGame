package modelo;

import modelo.Tabuleiro;
import modelo.Casa;

public abstract class Peca {

    public enum Cor {
        BRANCA,
        PRETA
    }

    private Cor cor;

    public Peca(Cor cor) {
        this.cor = cor;
    }

    public Cor getCor() {
        return cor;
    }

    public abstract boolean isMovimentoValido(Tabuleiro tabuleiro, Casa origem, Casa destino);
}