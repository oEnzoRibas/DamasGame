package modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CasaTest {
    @Test
    void testCriarCasa() {
        Casa casa = new Casa(1, 2);
        assertEquals(1, casa.getLinha());
        assertEquals(2, casa.getColuna());
        assertTrue(casa.estaVazia());
        assertNull(casa.getPeca());
    }

    @Test
    void testColocarRemoverPeca() {
        Casa casa = new Casa(0, 0);
        Peca peca = new Peca(Peca.Cor.BRANCA) {
            @Override
            public boolean isMovimentoValido(Tabuleiro tabuleiro, Casa origem, Casa destino) {
                return false;
            }
        };

        casa.setPeca(peca);
        assertFalse(casa.estaVazia());
        assertEquals(peca, casa.getPeca());

        casa.setPeca(null);
        assertTrue(casa.estaVazia());
        assertNull(casa.getPeca());
    }
}
