package modelo;

import modelo.Peca;

public class Casa {


    /**
     * Representa uma casa no tabuleiro de xadrez.
     * Cada casa tem uma posição definida por linha e coluna, e pode conter uma peça.
     */

    private int linha;
    private int coluna;
    private Peca peca;


    /**
     * Construtor da classe Casa.
     *
     * @param linha A linha da casa no tabuleiro.
     * @param coluna A coluna da casa no tabuleiro.
     */

    public Casa(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
        this.peca = null;
    }


    /**
     * Retorna a linha da casa.
     *
     * @return A linha da casa.
     */

    public int getLinha() {
        return linha;
    }


    /**
     * Retorna a linha da casa.
     *
     * @return A linha da casa.
     */

    public int getColuna() {
        return coluna;
    }


    /**
     * Retorna a peça que está na casa.
     *
     * @return A peça na casa, ou null se a casa estiver vazia.
     */

    public Peca getPeca() {
        return peca;
    }


    /**
     * Define a peça na casa.
     *
     * @param peca A peça a ser colocada na casa.
     */

    public void setPeca(Peca peca) {
        this.peca = peca;
    }


    /**
     * Verifica se a casa está vazia (sem peça).
     *
     * @return true se a casa estiver vazia, false caso contrário.
     */
    public boolean estaVazia() {
        return peca == null;
    }
}
