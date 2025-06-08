package modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import util.MovimentoInvalidoException;
import java.util.List;

class TabuleiroTest {
    private Tabuleiro tabuleiro;

    @BeforeEach
    void setUp() {
        tabuleiro = new Tabuleiro(); // Creates a board with pieces in initial setup
    }

    // Helper to clear the board for specific test setups
    private void limparTabuleiro() {
        for (int i = 0; i < Tabuleiro.TAMANHO; i++) {
            for (int j = 0; j < Tabuleiro.TAMANHO; j++) {
                tabuleiro.getCasa(i, j).setPeca(null);
            }
        }
    }

    // --- isMovimentoValido() Tests ---
    @Test
    void testIsMovimentoValidoPecaRegularSimples() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(2, 1);
        Casa destino = tabuleiro.getCasa(3, 0);
        origem.setPeca(new PecaRegular(Peca.Cor.PRETA));
        assertTrue(tabuleiro.isMovimentoValido(origem, destino));
    }

    @Test
    void testIsMovimentoValidoPecaRegularParaTrasInvalido() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(3, 0);
        Casa destino = tabuleiro.getCasa(2, 1);
        origem.setPeca(new PecaRegular(Peca.Cor.PRETA)); // Preta at 3,0 cannot move to 2,1
        assertFalse(tabuleiro.isMovimentoValido(origem, destino));
    }

    @Test
    void testIsMovimentoValidoPecaRegularCorErrada() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(2,1); // Should be PRETA from initial setup
        Casa destino = tabuleiro.getCasa(1,0); // Trying to move like BRANCA
        origem.setPeca(new PecaRegular(Peca.Cor.PRETA));
        assertFalse(origem.getPeca().isMovimentoValido(tabuleiro, origem, destino), "PecaRegular Preta cannot move backwards (like Branca)");
    }


    @Test
    void testIsMovimentoValidoDamaSimplesFrente() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(3, 3);
        Casa destino = tabuleiro.getCasa(2, 2);
        origem.setPeca(new Dama(Peca.Cor.BRANCA));
        assertTrue(tabuleiro.isMovimentoValido(origem, destino));
    }

    @Test
    void testIsMovimentoValidoDamaSimplesTras() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(3, 3);
        Casa destino = tabuleiro.getCasa(4, 4);
        origem.setPeca(new Dama(Peca.Cor.BRANCA));
        assertTrue(tabuleiro.isMovimentoValido(origem, destino));
    }

    @Test
    void testIsMovimentoValidoDamaMultiplasCasasCaminhoLivre() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(1, 1);
        Casa destino = tabuleiro.getCasa(4, 4);
        origem.setPeca(new Dama(Peca.Cor.BRANCA));
        assertTrue(tabuleiro.isMovimentoValido(origem, destino));
    }

    @Test
    void testIsMovimentoValidoDamaMultiplasCasasCaminhoBloqueado() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(1, 1);
        Casa destino = tabuleiro.getCasa(4, 4);
        origem.setPeca(new Dama(Peca.Cor.BRANCA));
        tabuleiro.getCasa(2, 2).setPeca(new PecaRegular(Peca.Cor.PRETA)); // Block path
        assertFalse(tabuleiro.isMovimentoValido(origem, destino));
    }

    @Test
    void testIsMovimentoValidoParaCasaOcupadaInvalido() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(1, 1);
        Casa destino = tabuleiro.getCasa(2, 2);
        origem.setPeca(new PecaRegular(Peca.Cor.BRANCA));
        destino.setPeca(new PecaRegular(Peca.Cor.PRETA)); // Destino ocupado
        assertFalse(tabuleiro.isMovimentoValido(origem, destino));
    }

    @Test
    void testIsMovimentoValidoDeCasaVaziaInvalido() {
        limparTabuleiro(); // Ensure casa is empty
        Casa origem = tabuleiro.getCasa(1, 1);
        Casa destino = tabuleiro.getCasa(2, 2);
        assertFalse(tabuleiro.isMovimentoValido(origem, destino));
    }

    // --- isCapturaValida() Tests ---
    @Test
    void testIsCapturaValidaPecaRegularBrancaParaFrente() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(5, 0);
        Casa pecaCapturada = tabuleiro.getCasa(4, 1);
        Casa destino = tabuleiro.getCasa(3, 2);
        origem.setPeca(new PecaRegular(Peca.Cor.BRANCA));
        pecaCapturada.setPeca(new PecaRegular(Peca.Cor.PRETA));
        assertTrue(tabuleiro.isCapturaValida(origem, pecaCapturada, destino));
    }

    @Test
    void testIsCapturaValidaPecaRegularPretaParaFrente() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(2, 1);
        Casa pecaCapturada = tabuleiro.getCasa(3, 2);
        Casa destino = tabuleiro.getCasa(4, 3);
        origem.setPeca(new PecaRegular(Peca.Cor.PRETA));
        pecaCapturada.setPeca(new PecaRegular(Peca.Cor.BRANCA));
        assertTrue(tabuleiro.isCapturaValida(origem, pecaCapturada, destino));
    }

    @Test
    void testIsCapturaValidaPecaRegularParaTrasInvalido() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(2, 1); // Preta
        Casa pecaCapturada = tabuleiro.getCasa(1, 2); // Branca (simulando tentativa de captura para trás)
        Casa destino = tabuleiro.getCasa(0, 3);
        origem.setPeca(new PecaRegular(Peca.Cor.PRETA));
        pecaCapturada.setPeca(new PecaRegular(Peca.Cor.BRANCA));
        assertFalse(tabuleiro.isCapturaValida(origem, pecaCapturada, destino));
    }

    @Test
    void testIsCapturaValidaDamaParaFrente() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(5, 0);
        Casa pecaCapturada = tabuleiro.getCasa(4, 1);
        Casa destino = tabuleiro.getCasa(3, 2);
        origem.setPeca(new Dama(Peca.Cor.BRANCA));
        pecaCapturada.setPeca(new PecaRegular(Peca.Cor.PRETA));
        assertTrue(tabuleiro.isCapturaValida(origem, pecaCapturada, destino));
    }

    @Test
    void testIsCapturaValidaDamaParaTras() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(3, 2);
        Casa pecaCapturada = tabuleiro.getCasa(4, 1);
        Casa destino = tabuleiro.getCasa(5, 0);
        origem.setPeca(new Dama(Peca.Cor.BRANCA));
        pecaCapturada.setPeca(new PecaRegular(Peca.Cor.PRETA));
        assertTrue(tabuleiro.isCapturaValida(origem, pecaCapturada, destino));
    }

    @Test
    void testIsCapturaValidaDestinoOcupadoInvalido() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(5, 0);
        Casa pecaCapturada = tabuleiro.getCasa(4, 1);
        Casa destino = tabuleiro.getCasa(3, 2);
        origem.setPeca(new PecaRegular(Peca.Cor.BRANCA));
        pecaCapturada.setPeca(new PecaRegular(Peca.Cor.PRETA));
        destino.setPeca(new PecaRegular(Peca.Cor.BRANCA)); // Destino ocupado
        assertFalse(tabuleiro.isCapturaValida(origem, pecaCapturada, destino));
    }

    @Test
    void testIsCapturaValidaPecaIntermediariaMesmaCorInvalido() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(5, 0);
        Casa pecaCapturada = tabuleiro.getCasa(4, 1);
        Casa destino = tabuleiro.getCasa(3, 2);
        origem.setPeca(new PecaRegular(Peca.Cor.BRANCA));
        pecaCapturada.setPeca(new PecaRegular(Peca.Cor.BRANCA)); // Peça da mesma cor
        assertFalse(tabuleiro.isCapturaValida(origem, pecaCapturada, destino));
    }

    @Test
    void testIsCapturaValidaCasaIntermediariaVaziaInvalido() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(5, 0);
        Casa pecaCapturadaCasa = tabuleiro.getCasa(4, 1); // Casa está vazia
        Casa destino = tabuleiro.getCasa(3, 2);
        origem.setPeca(new PecaRegular(Peca.Cor.BRANCA));
        assertFalse(tabuleiro.isCapturaValida(origem, pecaCapturadaCasa, destino));
    }

    @Test
    void testIsCapturaValidaSaltoNaoDiagonalDuasCasasInvalido() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(5,0); // Branca
        Casa pecaCapturada = tabuleiro.getCasa(4,0); // Preta, mas não diagonal
        Casa destino = tabuleiro.getCasa(3,0);
        origem.setPeca(new PecaRegular(Peca.Cor.BRANCA));
        pecaCapturada.setPeca(new PecaRegular(Peca.Cor.PRETA));
        assertFalse(tabuleiro.isCapturaValida(origem, pecaCapturada, destino));
    }

    @Test
    void testIsCapturaValidaSaltoMenorOuMaiorQueDuasCasasInvalido() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(5,0);
        origem.setPeca(new PecaRegular(Peca.Cor.BRANCA));
        // Test salto de 1 casa (isMovimentoValido, not isCapturaValida)
        Casa pecaCapturadaCurta = tabuleiro.getCasa(4,1); // Correto intermediario
        // Destino muito perto (salto de 1)
        assertFalse(tabuleiro.isCapturaValida(origem, pecaCapturadaCurta, pecaCapturadaCurta)); // Destino = intermediario, invalido

        // Test salto de 3 casas
        Casa pecaCapturadaSaltoLongo = tabuleiro.getCasa(4,1); // Preta
        pecaCapturadaSaltoLongo.setPeca(new PecaRegular(Peca.Cor.PRETA));
        Casa destinoLonge = tabuleiro.getCasa(2,3); // Salto de 3 casas
        assertFalse(tabuleiro.isCapturaValida(origem, pecaCapturadaSaltoLongo, destinoLonge));
    }


    // --- moverPeca() Tests ---
    @Test
    void testMoverPecaSimplesComSucesso() throws MovimentoInvalidoException {
        // Use initial board setup for this
        Casa origem = tabuleiro.getCasa(5, 0); // White piece
        Casa destino = tabuleiro.getCasa(4, 1); // Empty diagonal
        Peca pecaMovida = origem.getPeca();
        assertNotNull(pecaMovida);
        assertEquals(Peca.Cor.BRANCA, pecaMovida.getCor());

        tabuleiro.moverPeca(origem, destino); // Should call isMovimentoValido then executarMovimentoSimples

        assertTrue(origem.estaVazia());
        assertEquals(pecaMovida, destino.getPeca());
        assertFalse(destino.getPeca() instanceof Dama); // Should not promote yet
    }

    @Test
    void testMoverPecaComCapturaComSucesso() throws MovimentoInvalidoException {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(5, 0);
        Casa pecaCapturadaCasa = tabuleiro.getCasa(4, 1);
        Casa destino = tabuleiro.getCasa(3, 2);

        Peca pecaMovendo = new PecaRegular(Peca.Cor.BRANCA);
        Peca pecaASerCapturada = new PecaRegular(Peca.Cor.PRETA);
        origem.setPeca(pecaMovendo);
        pecaCapturadaCasa.setPeca(pecaASerCapturada);

        tabuleiro.moverPeca(origem, destino); // Should call isCapturaValida then executarMovimentoComCaptura

        assertTrue(origem.estaVazia());
        assertTrue(pecaCapturadaCasa.estaVazia());
        assertEquals(pecaMovendo, destino.getPeca());
        assertFalse(destino.getPeca() instanceof Dama);
    }

    @Test
    void testMoverPecaPromocaoPecaRegularBrancaAposMovimentoSimples() throws MovimentoInvalidoException {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(1, 0);
        Casa destino = tabuleiro.getCasa(0, 1); // Promotion square for white
        Peca pecaMovendo = new PecaRegular(Peca.Cor.BRANCA);
        origem.setPeca(pecaMovendo);

        tabuleiro.moverPeca(origem, destino);
        assertTrue(destino.getPeca() instanceof Dama);
        assertEquals(Peca.Cor.BRANCA, destino.getPeca().getCor());
    }

    @Test
    void testMoverPecaPromocaoPecaRegularPretaAposMovimentoSimples() throws MovimentoInvalidoException {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(6, 1);
        Casa destino = tabuleiro.getCasa(7, 0); // Promotion square for black
        Peca pecaMovendo = new PecaRegular(Peca.Cor.PRETA);
        origem.setPeca(pecaMovendo);

        tabuleiro.moverPeca(origem, destino);
        assertTrue(destino.getPeca() instanceof Dama);
        assertEquals(Peca.Cor.PRETA, destino.getPeca().getCor());
    }

    @Test
    void testMoverPecaPromocaoPecaRegularBrancaAposCaptura() throws MovimentoInvalidoException {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(2,1);
        Casa pecaCapturadaCasa = tabuleiro.getCasa(1,2);
        Casa destino = tabuleiro.getCasa(0,3); // Promotion square for white

        Peca pecaMovendo = new PecaRegular(Peca.Cor.BRANCA);
        Peca pecaASerCapturada = new PecaRegular(Peca.Cor.PRETA);
        origem.setPeca(pecaMovendo);
        pecaCapturadaCasa.setPeca(pecaASerCapturada);

        tabuleiro.moverPeca(origem, destino);
        assertTrue(destino.getPeca() instanceof Dama);
        assertEquals(Peca.Cor.BRANCA, destino.getPeca().getCor());
    }

    @Test
    void testMoverPecaPromocaoPecaRegularPretaAposCaptura() throws MovimentoInvalidoException {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(5,2);
        Casa pecaCapturadaCasa = tabuleiro.getCasa(6,1);
        Casa destino = tabuleiro.getCasa(7,0); // Promotion square for black

        Peca pecaMovendo = new PecaRegular(Peca.Cor.PRETA);
        Peca pecaASerCapturada = new PecaRegular(Peca.Cor.BRANCA);
        origem.setPeca(pecaMovendo);
        pecaCapturadaCasa.setPeca(pecaASerCapturada);

        tabuleiro.moverPeca(origem, destino);
        assertTrue(destino.getPeca() instanceof Dama);
        assertEquals(Peca.Cor.PRETA, destino.getPeca().getCor());
    }


    @Test
    void testMoverPecaDamaNaoPromoveNovamente() throws MovimentoInvalidoException {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(1, 0);
        Casa destino = tabuleiro.getCasa(0, 1); // Promotion square
        Peca pecaMovendo = new Dama(Peca.Cor.BRANCA); // Already a Dama
        origem.setPeca(pecaMovendo);

        tabuleiro.moverPeca(origem, destino);
        assertTrue(destino.getPeca() instanceof Dama); // Still a Dama
        assertSame(pecaMovendo, destino.getPeca()); // Ensure it's the same instance, not a new Dama
    }

    @Test
    void testMoverPecaInvalidoThrowsException() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(1, 1);
        Casa destino = tabuleiro.getCasa(4, 4);
        origem.setPeca(new Dama(Peca.Cor.BRANCA));
        tabuleiro.getCasa(2, 2).setPeca(new PecaRegular(Peca.Cor.PRETA)); // Block path

        assertThrows(MovimentoInvalidoException.class, () -> {
            tabuleiro.moverPeca(origem, destino); // isMovimentoValido (called by moverPeca) should detect blocked path
        });
    }

    // --- getPossiveisCapturas() Tests ---
    @Test
    void testGetPossiveisCapturasNenhumaPossivel() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(2,1);
        origem.setPeca(new PecaRegular(Peca.Cor.PRETA));
        // No pieces to capture
        assertTrue(tabuleiro.getPossiveisCapturas(origem).isEmpty());
    }

    @Test
    void testGetPossiveisCapturasUmaPossivelPecaRegular() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(2, 1); // Preta
        Casa pecaCapturada = tabuleiro.getCasa(3, 2);
        Casa destinoEsperado = tabuleiro.getCasa(4, 3);
        origem.setPeca(new PecaRegular(Peca.Cor.PRETA));
        pecaCapturada.setPeca(new PecaRegular(Peca.Cor.BRANCA));

        List<Casa> capturas = tabuleiro.getPossiveisCapturas(origem);
        assertEquals(1, capturas.size());
        assertEquals(destinoEsperado, capturas.get(0));
    }

    @Test
    void testGetPossiveisCapturasUmaPossivelDama() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(3, 3); // Dama Branca
        Casa pecaCapturada = tabuleiro.getCasa(4, 4); // Preta
        Casa destinoEsperado = tabuleiro.getCasa(5, 5);
        origem.setPeca(new Dama(Peca.Cor.BRANCA));
        pecaCapturada.setPeca(new PecaRegular(Peca.Cor.PRETA));

        List<Casa> capturas = tabuleiro.getPossiveisCapturas(origem);
        assertEquals(1, capturas.size());
        assertEquals(destinoEsperado, capturas.get(0));
    }

    @Test
    void testGetPossiveisCapturasMultiplasPossiveisDama() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(3, 3);
        origem.setPeca(new Dama(Peca.Cor.BRANCA));
        // Capture forward-left
        tabuleiro.getCasa(2, 2).setPeca(new PecaRegular(Peca.Cor.PRETA));
        // Capture forward-right
        tabuleiro.getCasa(2, 4).setPeca(new PecaRegular(Peca.Cor.PRETA));
        // Capture backward-left
        tabuleiro.getCasa(4, 2).setPeca(new PecaRegular(Peca.Cor.PRETA));
        // Capture backward-right
        tabuleiro.getCasa(4, 4).setPeca(new PecaRegular(Peca.Cor.PRETA));

        List<Casa> capturas = tabuleiro.getPossiveisCapturas(origem);
        assertEquals(4, capturas.size());
        // Check if all expected destinations are present (order might vary)
        assertTrue(capturas.contains(tabuleiro.getCasa(1,1)));
        assertTrue(capturas.contains(tabuleiro.getCasa(1,5)));
        assertTrue(capturas.contains(tabuleiro.getCasa(5,1)));
        assertTrue(capturas.contains(tabuleiro.getCasa(5,5)));
    }

    @Test
    void testGetPossiveisCapturasPecaRegularApenasParaFrente() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(3,3);
        origem.setPeca(new PecaRegular(Peca.Cor.BRANCA));
        // Place a black piece that can be captured (forward for white)
        tabuleiro.getCasa(2,2).setPeca(new PecaRegular(Peca.Cor.PRETA));
        // Place a black piece that would require backward capture (invalid for regular white)
        tabuleiro.getCasa(4,4).setPeca(new PecaRegular(Peca.Cor.PRETA));

        List<Casa> capturas = tabuleiro.getPossiveisCapturas(origem);
        assertEquals(1, capturas.size());
        assertEquals(tabuleiro.getCasa(1,1), capturas.get(0)); // Only forward capture
    }

    @Test
    void testGetPossiveisCapturasCasaOrigemVazia() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(3,3); // Empty
        assertTrue(tabuleiro.getPossiveisCapturas(origem).isEmpty());
    }
     @Test
    void testInicializacaoTabuleiro() { // Test from previous version, still relevant
        Tabuleiro t = new Tabuleiro(); // Uses @BeforeEach tabuleiro, or make a new one
        assertNotNull(t);
        assertNotNull(t.getCasa(0, 1).getPeca());
        assertEquals(Peca.Cor.PRETA, t.getCasa(0, 1).getPeca().getCor());
        assertNotNull(t.getCasa(5, 0).getPeca());
        assertEquals(Peca.Cor.BRANCA, t.getCasa(5, 0).getPeca().getCor());
        assertTrue(t.getCasa(3,0).estaVazia());
    }
}
