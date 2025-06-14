package modelo;

import modelo.Peca.Cor;
import modelo.Tabuleiro;
import modelo.Casa;

public class Dama extends Peca {

    public Dama(Cor cor) {
        super(cor);
    }

    @Override
    public boolean isMovimentoValido(Tabuleiro tabuleiro, Casa origem, Casa destino) {
        if (origem == null || destino == null) {
            return false;
        }
        if (origem.estaVazia() || !destino.estaVazia()) {
            return false;
        }

        // Garante que o movimento é diagonal
        // A dama pode se mover qualquer número de casas diagonalmente
        return Math.abs(origem.getLinha() - destino.getLinha()) == Math.abs(origem.getColuna() - destino.getColuna());
    }
}

