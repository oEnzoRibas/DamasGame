package modelo;

import modelo.Casa;
import modelo.Peca;
import util.MovimentoInvalidoException;

public class Tabuleiro {

    public static final int TAMANHO = 8;
    private Casa[][] casas;

    public Tabuleiro() {
        casas = new Casa[TAMANHO][TAMANHO];
        for (int linha = 0; linha < TAMANHO; linha++) {
            for (int coluna = 0; coluna < TAMANHO; coluna++) {
                casas[linha][coluna] = new Casa(linha, coluna);
            }
        }
        inicializarPecas();
    }

    private void inicializarPecas() {
        // Peças pretas
        for (int linha = 0; linha < 3; linha++) {
            for (int coluna = 0; coluna < TAMANHO; coluna++) {
                if ((linha + coluna) % 2 != 0) {
                    casas[linha][coluna].setPeca(new Peca(Peca.Cor.PRETA));
                }
            }
        }

        // Peças brancas
        for (int linha = 5; linha < TAMANHO; linha++) {
            for (int coluna = 0; coluna < TAMANHO; coluna++) {
                if ((linha + coluna) % 2 != 0) {
                    casas[linha][coluna].setPeca(new Peca(Peca.Cor.BRANCA));
                }
            }
        }
    }

    public void mostrar() {
        System.out.print("  ");
        for (int coluna = 0; coluna < TAMANHO; coluna++) {
            System.out.print(" " + coluna + " ");
        }
        System.out.println();

        for (int linha = 0; linha < TAMANHO; linha++) {
            System.out.print(linha + " ");
            for (int coluna = 0; coluna < TAMANHO; coluna++) {
                Peca peca = casas[linha][coluna].getPeca();
                if (casas[linha][coluna].estaVazia()) {
                    System.out.print(" . ");
                } else if (peca.getCor() == Peca.Cor.BRANCA) {
                    // Se for Dama, imprime D, senão B
                    if (peca.isDama()) {
                        System.out.print(" D ");
                    } else {
                        System.out.print(" B ");
                    }
                } else {
                    // Se for Dama, imprime A, senão P (A de Dama preta - Andina)
                    if (peca.isDama()) {
                        System.out.print(" A ");
                    } else {
                        System.out.print(" P ");
                    }
                }
            }
            System.out.println();
        }
    }

    public Casa getCasa(int linha, int coluna) {
        if (linha >= 0 && linha < TAMANHO && coluna >= 0 && coluna < TAMANHO) {
            return casas[linha][coluna];
        } else {
            // Considerar lançar uma exceção aqui se for mais apropriado para o design do jogo
            return null;
        }
    }

    public void moverPeca(Casa origem, Casa destino) throws MovimentoInvalidoException {
        if (origem.estaVazia()) {
            throw new MovimentoInvalidoException("A casa de origem está vazia.");
        }
        if (!destino.estaVazia()) {
            throw new MovimentoInvalidoException("A casa de destino não está vazia.");
        }

        Peca peca = origem.getPeca();
        destino.setPeca(peca);
        origem.setPeca(null);

        // Verificar promoção
        if (peca.getCor() == Peca.Cor.BRANCA && destino.getLinha() == 0) {
            if (!peca.isDama()) {
                destino.setPeca(new Dama(Peca.Cor.BRANCA));
            }
        } else if (peca.getCor() == Peca.Cor.PRETA && destino.getLinha() == TAMANHO - 1) {
            if (!peca.isDama()) {
                destino.setPeca(new Dama(Peca.Cor.PRETA));
            }
        }
    }

    public boolean isMovimentoValido(Casa origem, Casa destino) {
        // Implementação básica, será refinada
        if (origem == null || destino == null || origem.estaVazia()) {
            return false;
        }
        // Adicionar outras validações aqui conforme as regras do jogo
        return true;
    }

    public boolean isCapturaValida(Casa origem, Casa intermediaria, Casa destino) {
        // Implementação básica, será refinada
        return false;
    }

    public boolean verificarVitoria(Peca.Cor corJogadorAtual) {
        Peca.Cor corOponente = (corJogadorAtual == Peca.Cor.BRANCA) ? Peca.Cor.PRETA : Peca.Cor.BRANCA;
        int contadorOponente = 0;
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                Casa casa = casas[i][j];
                if (!casa.estaVazia() && casa.getPeca().getCor() == corOponente) {
                    contadorOponente++;
                }
            }
        }
        return contadorOponente == 0;
    }
}
