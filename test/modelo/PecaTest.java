package modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PecaTest {

    private Tabuleiro tabuleiro;
    private Casa casa33, casa22, casa44, casa24, casa42, casa55, casa11, casa00, casa77; // Common houses

    @BeforeEach
    void setUp() {
        tabuleiro = new Tabuleiro(); // Real Tabuleiro, pieces will be placed manually for tests
        // Initialize some common houses for convenience
        casa33 = tabuleiro.getCasa(3, 3);
        casa22 = tabuleiro.getCasa(2, 2);
        casa44 = tabuleiro.getCasa(4, 4);
        casa24 = tabuleiro.getCasa(2, 4);
        casa42 = tabuleiro.getCasa(4, 2);
        casa55 = tabuleiro.getCasa(5, 5);
        casa11 = tabuleiro.getCasa(1, 1);
        casa00 = tabuleiro.getCasa(0,0);
        casa77 = tabuleiro.getCasa(7,7);
    }

    // --- Tests for PecaRegular.isMovimentoValido ---

    @Test
    void testPecaRegularBrancaMovimentoValidoDiagonalParaFrente() {
        Peca peca = new PecaRegular(Peca.Cor.BRANCA);
        casa33.setPeca(peca); // White piece at 3,3
        // Valid moves for white: (2,2) or (2,4)
        assertTrue(peca.isMovimentoValido(tabuleiro, casa33, casa22), "Branca: 3,3 -> 2,2 (frente-esquerda)");
        assertTrue(peca.isMovimentoValido(tabuleiro, casa33, casa24), "Branca: 3,3 -> 2,4 (frente-direita)");
    }

    @Test
    void testPecaRegularPretaMovimentoValidoDiagonalParaFrente() {
        Peca peca = new PecaRegular(Peca.Cor.PRETA);
        casa33.setPeca(peca); // Black piece at 3,3
        // Valid moves for black: (4,2) or (4,4)
        assertTrue(peca.isMovimentoValido(tabuleiro, casa33, casa42), "Preta: 3,3 -> 4,2 (frente-esquerda)");
        assertTrue(peca.isMovimentoValido(tabuleiro, casa33, casa44), "Preta: 3,3 -> 4,4 (frente-direita)");
    }

    @Test

    void testPecaRegularBrancaMovimentoValidoDiagonalParaTras() { // Renamed
        Peca peca = new PecaRegular(Peca.Cor.BRANCA);
        casa33.setPeca(peca);
        assertTrue(peca.isMovimentoValido(tabuleiro, casa33, casa44), "Branca: 3,3 -> 4,4 (para trás agora válido)"); // assert changed
    }

    @Test
    void testPecaRegularPretaMovimentoValidoDiagonalParaTras() { // Renamed
        Peca peca = new PecaRegular(Peca.Cor.PRETA);
        casa33.setPeca(peca);
        assertTrue(peca.isMovimentoValido(tabuleiro, casa33, casa22), "Preta: 3,3 -> 2,2 (para trás agora válido)"); // assert changed

    }

    @Test
    void testPecaRegularMovimentoInvalidoNaoDiagonal() {
        Peca peca = new PecaRegular(Peca.Cor.BRANCA);
        casa33.setPeca(peca);
        assertFalse(peca.isMovimentoValido(tabuleiro, casa33, tabuleiro.getCasa(3, 4)), "Movimento horizontal"); // 3,3 -> 3,4
        assertFalse(peca.isMovimentoValido(tabuleiro, casa33, tabuleiro.getCasa(2, 3)), "Movimento vertical"); // 3,3 -> 2,3
    }

    @Test
    void testPecaRegularMovimentoInvalidoDistanciaMaiorQueUm() {
        Peca peca = new PecaRegular(Peca.Cor.BRANCA);
        casa33.setPeca(peca);
        assertFalse(peca.isMovimentoValido(tabuleiro, casa33, casa11), "Branca: 3,3 -> 1,1 (distância > 1)");
        Peca pecaPreta = new PecaRegular(Peca.Cor.PRETA);
        casa33.setPeca(pecaPreta);
        assertFalse(pecaPreta.isMovimentoValido(tabuleiro, casa33, casa55), "Preta: 3,3 -> 5,5 (distância > 1)");
    }

    @Test
    void testPecaRegularMovimentoInvalidoOrigemVazia() {
        Peca peca = new PecaRegular(Peca.Cor.BRANCA); // Piece not on board
        // casa33 is empty by default in this test setup if not setPeca
        assertFalse(peca.isMovimentoValido(tabuleiro, casa33, casa22));
    }

    @Test
    void testPecaRegularMovimentoInvalidoDestinoOcupado() {
        Peca pecaMovendo = new PecaRegular(Peca.Cor.BRANCA);
        Peca pecaNoDestino = new PecaRegular(Peca.Cor.PRETA);
        casa33.setPeca(pecaMovendo);
        casa22.setPeca(pecaNoDestino); // Destino 2,2 is occupied
        assertFalse(pecaMovendo.isMovimentoValido(tabuleiro, casa33, casa22));
    }

    // --- Tests for Dama.isMovimentoValido ---

    @Test
    void testDamaMovimentoValidoDiagonalParaFrenteUmaCasa() {
        Peca dama = new Dama(Peca.Cor.BRANCA);
        casa33.setPeca(dama);
        assertTrue(dama.isMovimentoValido(tabuleiro, casa33, casa22), "Dama Branca: 3,3 -> 2,2");
        assertTrue(dama.isMovimentoValido(tabuleiro, casa33, casa24), "Dama Branca: 3,3 -> 2,4");
    }

    @Test
    void testDamaMovimentoValidoDiagonalParaTrasUmaCasa() {
        Peca dama = new Dama(Peca.Cor.BRANCA);
        casa33.setPeca(dama);
        assertTrue(dama.isMovimentoValido(tabuleiro, casa33, casa42), "Dama Branca: 3,3 -> 4,2");
        assertTrue(dama.isMovimentoValido(tabuleiro, casa33, casa44), "Dama Branca: 3,3 -> 4,4");
    }

    @Test
    void testDamaMovimentoValidoDiagonalMultiplasCasas() {
        Peca dama = new Dama(Peca.Cor.BRANCA);
        casa33.setPeca(dama);
        // Dama's own isMovimentoValido only checks if destination is empty and move is diagonal.
        // Path clearing is Tabuleiro's responsibility.
        assertTrue(dama.isMovimentoValido(tabuleiro, casa33, casa11), "Dama Branca: 3,3 -> 1,1"); // Path is clear in empty board
        assertTrue(dama.isMovimentoValido(tabuleiro, casa33, casa55), "Dama Branca: 3,3 -> 5,5"); // Path is clear
        assertTrue(dama.isMovimentoValido(tabuleiro, casa33, casa00), "Dama Branca: 3,3 -> 0,0");
        assertTrue(dama.isMovimentoValido(tabuleiro, casa33, casa77), "Dama Branca: 3,3 -> 7,7");
    }

    @Test
    void testDamaMovimentoInvalidoNaoDiagonal() {
        Peca dama = new Dama(Peca.Cor.BRANCA);
        casa33.setPeca(dama);
        assertFalse(dama.isMovimentoValido(tabuleiro, casa33, tabuleiro.getCasa(3,5)), "Dama: 3,3 -> 3,5 (horizontal)");
        assertFalse(dama.isMovimentoValido(tabuleiro, casa33, tabuleiro.getCasa(1,3)), "Dama: 3,3 -> 1,3 (vertical)");
    }

    @Test
    void testDamaMovimentoInvalidoOrigemVazia() {
        Peca dama = new Dama(Peca.Cor.BRANCA);
        assertFalse(dama.isMovimentoValido(tabuleiro, casa33, casa22));
    }

    @Test
    void testDamaMovimentoInvalidoDestinoOcupado() {
        Peca damaMovendo = new Dama(Peca.Cor.BRANCA);
        Peca pecaNoDestino = new PecaRegular(Peca.Cor.PRETA);
        casa33.setPeca(damaMovendo);
        casa11.setPeca(pecaNoDestino); // Destino 1,1 is occupied
        assertFalse(damaMovendo.isMovimentoValido(tabuleiro, casa33, casa11));
    }
}
