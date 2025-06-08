package modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PecaTest {
    @Test
    void testCriarPeca() {
        Peca pecaBranca = new Peca(Peca.Cor.BRANCA);
        assertEquals(Peca.Cor.BRANCA, pecaBranca.getCor());
        assertFalse(pecaBranca.isDama());

        Peca pecaPreta = new Peca(Peca.Cor.PRETA);
        assertEquals(Peca.Cor.PRETA, pecaPreta.getCor());
        assertFalse(pecaPreta.isDama());
    }

    @Test
    void testPromoverPeca() {
        Peca peca = new Peca(Peca.Cor.BRANCA);
        assertFalse(peca.isDama());
        peca.promover();
        assertTrue(peca.isDama());
    }
}
