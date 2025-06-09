package modelo;

import modelo.Tabuleiro;
import modelo.Casa;

public abstract class Peca {

    // Enumeração para as cores das peças
    public enum Cor {
        BRANCA,
        PRETA
    }

    // Atributo
    private Cor cor;

    // Construtor
    public Peca(Cor cor) {
        this.cor = cor;
    }

    // getter
    public Cor getCor() {
        return cor;
    }

    /**
     * Método abstrato que deve ser implementado pelas subclasses para 
     * verificar se o movimento da peça é válido.
     *
     * @param tabuleiro O tabuleiro onde a peça está sendo movida.
     * @param origem A casa de origem da peça.
     * @param destino A casa de destino da peça.
     * @return true se o movimento for válido, false caso contrário.
     */
    public abstract boolean isMovimentoValido(Tabuleiro tabuleiro, Casa origem, Casa destino);
}
