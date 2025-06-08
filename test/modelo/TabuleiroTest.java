package modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import util.MovimentoInvalidoException;

class TabuleiroTest {
    @Test
    void testInicializacaoTabuleiro() {
        Tabuleiro tabuleiro = new Tabuleiro();
        assertNotNull(tabuleiro);

        // Check a few key positions for initial piece setup
        // Black pieces
        assertNotNull(tabuleiro.getCasa(0, 1).getPeca());
        assertEquals(Peca.Cor.PRETA, tabuleiro.getCasa(0, 1).getPeca().getCor());
        assertNotNull(tabuleiro.getCasa(2, 7).getPeca());
        assertEquals(Peca.Cor.PRETA, tabuleiro.getCasa(2, 7).getPeca().getCor());
        assertNull(tabuleiro.getCasa(0, 0).getPeca()); // Light square

        // White pieces
        assertNotNull(tabuleiro.getCasa(5, 0).getPeca());
        assertEquals(Peca.Cor.BRANCA, tabuleiro.getCasa(5, 0).getPeca().getCor());
        assertNotNull(tabuleiro.getCasa(7, 6).getPeca());
        assertEquals(Peca.Cor.BRANCA, tabuleiro.getCasa(7, 6).getPeca().getCor());
        assertNull(tabuleiro.getCasa(5, 1).getPeca()); // Light square

        // Empty middle rows
        assertTrue(tabuleiro.getCasa(3, 0).estaVazia());
        assertTrue(tabuleiro.getCasa(4, 7).estaVazia());
    }

    @Test
    void testGetCasa() {
        Tabuleiro tabuleiro = new Tabuleiro();
        assertNotNull(tabuleiro.getCasa(0, 0));
        assertNotNull(tabuleiro.getCasa(7, 7));
        assertNull(tabuleiro.getCasa(-1, 0));
        assertNull(tabuleiro.getCasa(0, 8));
    }

    @Test
    void testMovimentoSimplesValido() {
        Tabuleiro tabuleiro = new Tabuleiro();
        Casa origem = tabuleiro.getCasa(2, 1); // Black piece
        Casa destino = tabuleiro.getCasa(3, 0); // Empty diagonal

        Peca pecaMovida = origem.getPeca();
        assertNotNull(pecaMovida);

        try {
            // Temporarily make isMovimentoValido return true for this specific simple move for testing moverPeca
            // This is a hack for now, proper move validation will be tested once implemented
            tabuleiro.moverPeca(origem, destino);
        } catch (MovimentoInvalidoException e) {
            fail("Movimento simples válido não deveria falhar: " + e.getMessage());
        }

        assertNull(origem.getPeca());
        assertEquals(pecaMovida, destino.getPeca());
    }

    @Test
    void testVerificarVitoria() {
        Tabuleiro tabuleiro = new Tabuleiro();
        // Initially, no one should have won
        assertFalse(tabuleiro.verificarVitoria(Peca.Cor.BRANCA));
        assertFalse(tabuleiro.verificarVitoria(Peca.Cor.PRETA));

        // Simulate removing all black pieces
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < Tabuleiro.TAMANHO; j++) {
                if ((i + j) % 2 != 0) { // Only remove pieces from dark squares
                    if (tabuleiro.getCasa(i,j) != null && tabuleiro.getCasa(i,j).getPeca() != null && tabuleiro.getCasa(i,j).getPeca().getCor() == Peca.Cor.PRETA) {
                        tabuleiro.getCasa(i, j).setPeca(null);
                    }
                }
            }
        }
        assertTrue(tabuleiro.verificarVitoria(Peca.Cor.BRANCA)); // White should win
        assertFalse(tabuleiro.verificarVitoria(Peca.Cor.PRETA));
    }
}
