package controle;

import modelo.Tabuleiro;

public class Jogo {
    private Tabuleiro tabuleiro;

    public void iniciar() {
        tabuleiro = new Tabuleiro();
        tabuleiro.mostrar();
    }
}
