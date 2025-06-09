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
    void testIsMovimentoValidoPecaRegularParaTrasValido() { // Renamed
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(3, 0);
        Casa destino = tabuleiro.getCasa(2, 1);
        origem.setPeca(new PecaRegular(Peca.Cor.PRETA)); // Preta at 3,0 CAN now move to 2,1
        assertTrue(tabuleiro.isMovimentoValido(origem, destino)); // Changed to assertTrue
    }

    /* // Commented out as per subtask instructions
    @Test
    void testIsMovimentoValidoPecaRegularCorErrada() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(2,1); // Should be PRETA from initial setup
        Casa destino = tabuleiro.getCasa(1,0); // Trying to move like BRANCA
        origem.setPeca(new PecaRegular(Peca.Cor.PRETA));
        assertFalse(origem.getPeca().isMovimentoValido(tabuleiro, origem, destino), "PecaRegular Preta cannot move backwards (like Branca)");
    }
    */

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
    void testIsCapturaValidaPecaRegularBrancaParaTrasValido() { // Renamed and logic updated
        limparTabuleiro();
        Peca pecaBranca = new PecaRegular(Peca.Cor.BRANCA);
        Peca pecaPreta = new PecaRegular(Peca.Cor.PRETA);

        Casa origem = tabuleiro.getCasa(3, 3);
        Casa intermediaria = tabuleiro.getCasa(4, 4); // Backward for white
        Casa destino = tabuleiro.getCasa(5, 5);

        origem.setPeca(pecaBranca);
        intermediaria.setPeca(pecaPreta);
        // destino is empty

        assertTrue(tabuleiro.isCapturaValida(origem, intermediaria, destino), "PecaRegular BRANCA should now be able to capture backward.");
    }

    @Test
    void testIsCapturaValidaPecaRegularPretaParaTrasValido() { // New test for Preta capturing backward
        limparTabuleiro();
        Peca pecaPreta = new PecaRegular(Peca.Cor.PRETA);
        Peca pecaBranca = new PecaRegular(Peca.Cor.BRANCA);

        Casa origem = tabuleiro.getCasa(4, 4);
        Casa intermediaria = tabuleiro.getCasa(3, 3); // Backward for black
        Casa destino = tabuleiro.getCasa(2, 2);

        origem.setPeca(pecaPreta);
        intermediaria.setPeca(pecaBranca);
        // destino is empty

        assertTrue(tabuleiro.isCapturaValida(origem, intermediaria, destino), "PecaRegular PRETA should now be able to capture backward.");
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
        // Block path with a FRIENDLY piece to ensure it's not a capture attempt
        tabuleiro.getCasa(2, 2).setPeca(new PecaRegular(Peca.Cor.BRANCA));

        assertThrows(MovimentoInvalidoException.class, () -> {
            tabuleiro.moverPeca(origem, destino); // Should try simple move, fail due to block, and throw.
        }, "Dama's simple move blocked by a friendly piece should throw MovimentoInvalidoException");
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
        origem.setPeca(new Dama(Peca.Cor.BRANCA));
        pecaCapturada.setPeca(new PecaRegular(Peca.Cor.PRETA));

        // Expected landing spots after capturing (4,4) -> (5,5), (6,6), (7,7)
        Casa destino1 = tabuleiro.getCasa(5, 5);
        Casa destino2 = tabuleiro.getCasa(6, 6);
        Casa destino3 = tabuleiro.getCasa(7, 7);

        List<Casa> capturas = tabuleiro.getPossiveisCapturas(origem);
        assertEquals(3, capturas.size(), "Dama should find 3 landing spots after single capture");
        assertTrue(capturas.contains(destino1), "Should contain (5,5)");
        assertTrue(capturas.contains(destino2), "Should contain (6,6)");
        assertTrue(capturas.contains(destino3), "Should contain (7,7)");
    }

    @Test
    void testGetPossiveisCapturasMultiplasPossiveisDama() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(3, 3); // Dama Branca
        origem.setPeca(new Dama(Peca.Cor.BRANCA));

        // Piece to capture at (2,2) -> landing (1,1), (0,0)
        tabuleiro.getCasa(2, 2).setPeca(new PecaRegular(Peca.Cor.PRETA));
        Casa destinoA1 = tabuleiro.getCasa(1,1);
        Casa destinoA2 = tabuleiro.getCasa(0,0);

        // Piece to capture at (2,4) -> landing (1,5), (0,6)
        tabuleiro.getCasa(2, 4).setPeca(new PecaRegular(Peca.Cor.PRETA));
        Casa destinoB1 = tabuleiro.getCasa(1,5);
        Casa destinoB2 = tabuleiro.getCasa(0,6);

        // Piece to capture at (4,2) -> landing (5,1), (6,0)
        tabuleiro.getCasa(4, 2).setPeca(new PecaRegular(Peca.Cor.PRETA));
        Casa destinoC1 = tabuleiro.getCasa(5,1);
        Casa destinoC2 = tabuleiro.getCasa(6,0);

        // Piece to capture at (4,4) -> landing (5,5), (6,6), (7,7)
        tabuleiro.getCasa(4, 4).setPeca(new PecaRegular(Peca.Cor.PRETA));
        Casa destinoD1 = tabuleiro.getCasa(5,5);
        Casa destinoD2 = tabuleiro.getCasa(6,6);
        Casa destinoD3 = tabuleiro.getCasa(7,7);

        List<Casa> capturas = tabuleiro.getPossiveisCapturas(origem);
        assertEquals(9, capturas.size(), "Dama should find 9 landing spots from 4 captures");

        assertTrue(capturas.contains(destinoA1));
        assertTrue(capturas.contains(destinoA2));
        assertTrue(capturas.contains(destinoB1));
        assertTrue(capturas.contains(destinoB2));
        assertTrue(capturas.contains(destinoC1));
        assertTrue(capturas.contains(destinoC2));
        assertTrue(capturas.contains(destinoD1));
        assertTrue(capturas.contains(destinoD2));
        assertTrue(capturas.contains(destinoD3));
    }

    @Test
    void testGetPossiveisCapturasPecaRegularIncluiTras() { // Renamed and logic updated
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(3,3);
        Peca pecaBranca = new PecaRegular(Peca.Cor.BRANCA);
        origem.setPeca(pecaBranca);

        // Forward capture opportunity for White
        tabuleiro.getCasa(2,2).setPeca(new PecaRegular(Peca.Cor.PRETA));
        Casa destinoFrente = tabuleiro.getCasa(1,1);

        // Backward capture opportunity for White
        tabuleiro.getCasa(4,4).setPeca(new PecaRegular(Peca.Cor.PRETA));
        Casa destinoTras = tabuleiro.getCasa(5,5);

        List<Casa> capturas = tabuleiro.getPossiveisCapturas(origem);
        assertEquals(2, capturas.size(), "Should find two captures (forward and backward)");
        assertTrue(capturas.contains(destinoFrente), "Should contain forward capture destination");
        assertTrue(capturas.contains(destinoTras), "Should contain backward capture destination");
    }

    @Test
    void testGetPossiveisCapturasCasaOrigemVazia() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(3,3); // Empty
        assertTrue(tabuleiro.getPossiveisCapturas(origem).isEmpty());
    }

    // --- Tests for Dama's "Flying Capture" ---

    @Test
    void testIsCapturaValidaDamaFlyingCapture_PathToEnemyAndLandingClear() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(1, 1);
        Casa pecaCapturada = tabuleiro.getCasa(3, 3);
        Casa destino = tabuleiro.getCasa(4, 4);
        origem.setPeca(new Dama(Peca.Cor.BRANCA));
        pecaCapturada.setPeca(new PecaRegular(Peca.Cor.PRETA));
        // Path (2,2) is clear. Path from (3,3) to (4,4) is clear.
        assertTrue(tabuleiro.isCapturaValida(origem, pecaCapturada, destino), "Dama FC: (1,1) captures (3,3) lands (4,4)");

        Casa destinoLonge = tabuleiro.getCasa(5,5);
        // Path (2,2) clear. Path (4,4) to (5,5) clear.
        assertTrue(tabuleiro.isCapturaValida(origem, pecaCapturada, destinoLonge), "Dama FC: (1,1) captures (3,3) lands (5,5)");
    }

    @Test
    void testIsCapturaValidaDamaFlyingCapture_PathToEnemyBlocked() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(1, 1);
        Casa pecaCapturada = tabuleiro.getCasa(4, 4);
        Casa destino = tabuleiro.getCasa(5, 5);
        origem.setPeca(new Dama(Peca.Cor.BRANCA));
        pecaCapturada.setPeca(new PecaRegular(Peca.Cor.PRETA));
        tabuleiro.getCasa(2, 2).setPeca(new PecaRegular(Peca.Cor.BRANCA)); // Blocker on path to enemy

        assertFalse(tabuleiro.isCapturaValida(origem, pecaCapturada, destino), "Dama FC: Path to enemy blocked at (2,2)");
    }

    @Test
    void testIsCapturaValidaDamaFlyingCapture_LandingPathBlocked() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(1, 1);
        Casa pecaCapturada = tabuleiro.getCasa(2, 2);
        origem.setPeca(new Dama(Peca.Cor.BRANCA));
        pecaCapturada.setPeca(new PecaRegular(Peca.Cor.PRETA));

        Casa destino1 = tabuleiro.getCasa(3,3); // This landing is clear
        assertTrue(tabuleiro.isCapturaValida(origem, pecaCapturada, destino1), "Dama FC: Landing at (3,3) should be valid");

        tabuleiro.getCasa(4,4).setPeca(new PecaRegular(Peca.Cor.BRANCA)); // Blocker for further landing
        Casa destino2 = tabuleiro.getCasa(5,5);
        assertFalse(tabuleiro.isCapturaValida(origem, pecaCapturada, destino2), "Dama FC: Landing at (5,5) path blocked by (4,4)");
    }

    @Test
    void testIsCapturaValidaDamaFlyingCapture_AttemptToCaptureNonFirstEnemy() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(0,0);
        Casa firstEnemy = tabuleiro.getCasa(2,2);
        Casa secondEnemy = tabuleiro.getCasa(4,4); // This is the target for isCapturaValida call
        Casa destino = tabuleiro.getCasa(5,5);

        origem.setPeca(new Dama(Peca.Cor.BRANCA));
        firstEnemy.setPeca(new PecaRegular(Peca.Cor.PRETA));
        secondEnemy.setPeca(new PecaRegular(Peca.Cor.PRETA));

        // isCapturaValida checks the specific path from origem to 'capturada' (secondEnemy here).
        // The path to secondEnemy (4,4) is blocked by firstEnemy (2,2).
        assertFalse(tabuleiro.isCapturaValida(origem, secondEnemy, destino), "Dama FC: Cannot target second enemy if path to it is blocked by first");
    }

    @Test
    void testIsCapturaValidaDamaFlyingCapture_TargetingFriendlyPieceAsCaptured() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(0,0);
        Casa friendlyPiece = tabuleiro.getCasa(2,2);
        Casa destino = tabuleiro.getCasa(3,3);

        origem.setPeca(new Dama(Peca.Cor.BRANCA));
        friendlyPiece.setPeca(new PecaRegular(Peca.Cor.BRANCA)); // Friendly piece

        assertFalse(tabuleiro.isCapturaValida(origem, friendlyPiece, destino), "Dama FC: Cannot target friendly piece for capture");
    }

    @Test
    void testGetPossiveisCapturasDamaFlyingCapture_SingleEnemyMultipleLandings() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(0,0);
        origem.setPeca(new Dama(Peca.Cor.BRANCA));
        tabuleiro.getCasa(2,2).setPeca(new PecaRegular(Peca.Cor.PRETA)); // Enemy

        // Empty squares: (1,1), (3,3), (4,4), (5,5)
        // Expected landing spots after capturing (2,2)
        Casa destino1 = tabuleiro.getCasa(3,3);
        Casa destino2 = tabuleiro.getCasa(4,4);
        Casa destino3 = tabuleiro.getCasa(5,5);
        Casa destino4 = tabuleiro.getCasa(6,6);
        Casa destino5 = tabuleiro.getCasa(7,7);

        List<Casa> capturas = tabuleiro.getPossiveisCapturas(origem);
        assertEquals(5, capturas.size(), "Dama FC: Should find 5 landing spots"); // Corrected expected size
        assertTrue(capturas.contains(destino1));
        assertTrue(capturas.contains(destino2));
        assertTrue(capturas.contains(destino3));
        assertTrue(capturas.contains(destino4)); // Added check
        assertTrue(capturas.contains(destino5)); // Added check
    }

    @Test
    void testGetPossiveisCapturasDamaFlyingCapture_LandingPathPartiallyBlocked() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(0,0);
        origem.setPeca(new Dama(Peca.Cor.BRANCA));
        tabuleiro.getCasa(2,2).setPeca(new PecaRegular(Peca.Cor.PRETA)); // Enemy
        // (1,1), (3,3) are empty
        tabuleiro.getCasa(4,4).setPeca(new PecaRegular(Peca.Cor.BRANCA)); // Blocker

        // Expected landing spot after capturing (2,2)
        Casa destino1 = tabuleiro.getCasa(3,3);

        List<Casa> capturas = tabuleiro.getPossiveisCapturas(origem);
        assertEquals(1, capturas.size(), "Dama FC: Should find 1 landing spot before blocker");
        assertTrue(capturas.contains(destino1));
    }

    @Test
    void testGetPossiveisCapturasDamaFlyingCapture_PathToEnemyBlocked() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(0,0);
        origem.setPeca(new Dama(Peca.Cor.BRANCA));
        tabuleiro.getCasa(1,1).setPeca(new PecaRegular(Peca.Cor.BRANCA)); // Blocker (friendly)
        tabuleiro.getCasa(2,2).setPeca(new PecaRegular(Peca.Cor.PRETA)); // Enemy behind blocker

        List<Casa> capturas = tabuleiro.getPossiveisCapturas(origem);
        assertTrue(capturas.isEmpty(), "Dama FC: Path to enemy is blocked, no captures expected in this direction");
    }

    @Test
    void testGetPossiveisCapturasDamaFlyingCapture_MultipleEnemiesInDifferentDirections() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(3,3);
        origem.setPeca(new Dama(Peca.Cor.BRANCA));

        // Enemy 1 at (1,1) (path (2,2) clear). Landing: (0,0)
        tabuleiro.getCasa(1,1).setPeca(new PecaRegular(Peca.Cor.PRETA));
        Casa destino1 = tabuleiro.getCasa(0,0);

        // Enemy 2 at (5,5) (path (4,4) clear). Landing: (6,6), (7,7)
        tabuleiro.getCasa(5,5).setPeca(new PecaRegular(Peca.Cor.PRETA));
        Casa destino2a = tabuleiro.getCasa(6,6);
        Casa destino2b = tabuleiro.getCasa(7,7);

        // Enemy 3 at (1,5) (path (2,4) clear). Landing: (0,6)
        tabuleiro.getCasa(1,5).setPeca(new PecaRegular(Peca.Cor.PRETA));
        Casa destino3 = tabuleiro.getCasa(0,6);

        List<Casa> capturas = tabuleiro.getPossiveisCapturas(origem);
        assertEquals(1 + 2 + 1, capturas.size(), "Dama FC: Should find captures in multiple directions");
        assertTrue(capturas.contains(destino1));
        assertTrue(capturas.contains(destino2a));
        assertTrue(capturas.contains(destino2b));
        assertTrue(capturas.contains(destino3));
    }

    @Test
    void testGetPossiveisCapturasDamaFlyingCapture_CapturesOnlyFirstEnemyInLine() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(0,0);
        origem.setPeca(new Dama(Peca.Cor.BRANCA));
        tabuleiro.getCasa(2,2).setPeca(new PecaRegular(Peca.Cor.PRETA)); // First enemy
        tabuleiro.getCasa(4,4).setPeca(new PecaRegular(Peca.Cor.PRETA)); // Second enemy (should be ignored for capture, but acts as empty for path if first captured)

        // Expected landing spots after capturing first enemy at (2,2)
        Casa destino1 = tabuleiro.getCasa(3,3); // Path to here is clear
        // (4,4) is where secondEnemy is, blocking further landing for this capture.
        // Casa destino2 = tabuleiro.getCasa(5,5); // This is not expected anymore

        List<Casa> capturas = tabuleiro.getPossiveisCapturas(origem);

        // Expected: Only (3,3) is a valid landing spot as (4,4) (location of second enemy) blocks the path.
        assertEquals(1, capturas.size(), "Dama FC: Should find 1 landing spot after capturing the first enemy, path blocked by second enemy"); // Corrected size
        assertTrue(capturas.contains(destino1), "Landing at (3,3) by capturing (2,2)");
        // assertFalse(capturas.contains(destino2), "Landing at (5,5) should not be possible as (4,4) blocks path"); // Optional: verify (5,5) is NOT there
    }

    @Test
    void testMoverPecaDama_ExecutesFlyingCaptureCorrectly() throws MovimentoInvalidoException {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(0,0);
        Casa pecaCapturadaCasa = tabuleiro.getCasa(2,2);
        Casa destino = tabuleiro.getCasa(5,5);
        Peca dama = new Dama(Peca.Cor.BRANCA);
        origem.setPeca(dama);
        pecaCapturadaCasa.setPeca(new PecaRegular(Peca.Cor.PRETA));
        // Path (1,1) is empty. Path (3,3) and (4,4) to (5,5) are empty.

        tabuleiro.moverPeca(origem, destino);

        assertTrue(origem.estaVazia(), "Origin (0,0) should be empty");
        assertTrue(tabuleiro.getCasa(1,1).estaVazia(), "Path (1,1) should be empty");
        assertTrue(pecaCapturadaCasa.estaVazia(), "Captured piece at (2,2) should be removed");
        assertTrue(tabuleiro.getCasa(3,3).estaVazia(), "Path (3,3) should be empty");
        assertTrue(tabuleiro.getCasa(4,4).estaVazia(), "Path (4,4) should be empty");
        assertEquals(dama, destino.getPeca(), "Dama should be at destination (5,5)");
    }

    @Test
    void testIsMovimentoValidoDamaMultiplasCasasCaminhoBloqueadoPorPecaAmiga() {
        limparTabuleiro();
        Casa origem = tabuleiro.getCasa(1, 1);
        Casa destino = tabuleiro.getCasa(4, 4);
        origem.setPeca(new Dama(Peca.Cor.BRANCA));
        tabuleiro.getCasa(2, 2).setPeca(new PecaRegular(Peca.Cor.BRANCA)); // Block path with friendly piece
        assertFalse(tabuleiro.isMovimentoValido(origem, destino), "Dama simple move should be blocked by friendly piece");
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
