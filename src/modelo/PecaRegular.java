package modelo;

/**
 * Classe que representa uma peça regular no jogo de damas.
 * As peças regulares podem se mover diagonalmente para frente ou para trás,
 * mas apenas uma casa por vez.
 */
public class PecaRegular extends Peca {

    /**
     * Construtor da classe PecaRegular.
     * Inicializa a peça com uma cor específica.
     *
     * @param cor A cor da peça (BRANCA ou PRETA).
     */
    public PecaRegular(Cor cor) {
        super(cor); // Chama o construtor da classe Peca com a cor fornecida
    }

    /**
     * Verifica se o movimento da peça regular é válido.
     * As peças regulares podem se mover uma casa diagonalmente para frente ou para trás.
     *
     * @param tabuleiro O tabuleiro onde a peça está sendo movida.
     * @param origem A casa de origem da peça.
     * @param destino A casa de destino da peça.
     * @return true se o movimento for válido, false caso contrário.
     */
    @Override
    public boolean isMovimentoValido(Tabuleiro tabuleiro, Casa origem, Casa destino) {
        if (origem == null || destino == null) {
            return false;
        }
        if (origem.estaVazia() || !destino.estaVazia()) {
            return false; // A casa de origem deve conter uma peça e a casa de destino deve estar vazia
        }

        // Calcula a diferença entre as casas de origem e destino
        // A peça regular só pode se mover uma casa diagonalmente
        int deltaLinha = destino.getLinha() - origem.getLinha();
        int deltaColuna = Math.abs(destino.getColuna() - origem.getColuna());

        // Verifica se o movimento é diagonal

        if (deltaColuna != 1) { // Deve ser exatamente uma coluna de diferença
            return false;
        }

        // Verifica se a peça está se movendo uma casa para frente ou para trás
        return Math.abs(deltaLinha) == 1;
    }
}
