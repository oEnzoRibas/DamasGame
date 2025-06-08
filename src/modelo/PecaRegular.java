package modelo;

public class PecaRegular extends Peca {

    public PecaRegular(Cor cor) {
        super(cor);
    }

    @Override
    public boolean isMovimentoValido(Tabuleiro tabuleiro, Casa origem, Casa destino) {
        if (origem == null || destino == null) {
            return false;
        }
        if (origem.estaVazia() || !destino.estaVazia()) {
            // This check might be redundant if Tabuleiro.isMovimentoValido also checks it,
            // but good for piece-specific logic.
            return false;
        }

        int deltaLinha = destino.getLinha() - origem.getLinha();
        int deltaColuna = Math.abs(destino.getColuna() - origem.getColuna());

        if (deltaColuna != 1) { // Must move diagonally by one column
            return false;
        }

        // Check direction based on piece color
        if (getCor() == Cor.BRANCA) {
            // White pieces move "up" the board (decreasing row index)
            return deltaLinha == -1;
        } else { // PRETA
            // Black pieces move "down" the board (increasing row index)
            return deltaLinha == 1;
        }
    }
}
