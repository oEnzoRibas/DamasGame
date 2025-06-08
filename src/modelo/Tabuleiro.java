package modelo;

import modelo.Casa;
import modelo.Peca;
import util.MovimentoInvalidoException;
import java.util.List;
import java.util.ArrayList;

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
                    casas[linha][coluna].setPeca(new PecaRegular(Peca.Cor.PRETA));
                }
            }
        }

        // Peças brancas
        for (int linha = 5; linha < TAMANHO; linha++) {
            for (int coluna = 0; coluna < TAMANHO; coluna++) {
                if ((linha + coluna) % 2 != 0) {
                    casas[linha][coluna].setPeca(new PecaRegular(Peca.Cor.BRANCA));
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
                    if (peca instanceof Dama) {
                        System.out.print(" D ");
                    } else {
                        System.out.print(" B ");
                    }
                } else {
                    // Se for Dama, imprime A, senão P (A de Dama preta - Andina)
                    if (peca instanceof Dama) {
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
            if (!(peca instanceof Dama)) {
                destino.setPeca(new Dama(Peca.Cor.BRANCA));
            }
        } else if (peca.getCor() == Peca.Cor.PRETA && destino.getLinha() == TAMANHO - 1) {
            if (!(peca instanceof Dama)) {
                destino.setPeca(new Dama(Peca.Cor.PRETA));
            }
        }
    }

    // Renamed from moverPeca to clarify its role for simple moves
    private void executarMovimentoSimples(Casa origem, Casa destino) throws MovimentoInvalidoException {
        if (origem.estaVazia()) {
            throw new MovimentoInvalidoException("A casa de origem está vazia.");
        }
        if (!destino.estaVazia()) {
            throw new MovimentoInvalidoException("A casa de destino não está vazia para movimento simples.");
        }

        Peca peca = origem.getPeca();
        destino.setPeca(peca);
        origem.setPeca(null);

        // Verificar promoção
        if (!(peca instanceof Dama) &&
            ((peca.getCor() == Peca.Cor.BRANCA && destino.getLinha() == 0) ||
             (peca.getCor() == Peca.Cor.PRETA && destino.getLinha() == TAMANHO - 1))) {
            destino.setPeca(new Dama(peca.getCor()));
        }
    }

    private void executarMovimentoComCaptura(Casa origem, Casa destino, Casa casaPecaCapturada) throws MovimentoInvalidoException {
        if (origem.estaVazia()) {
            throw new MovimentoInvalidoException("A casa de origem está vazia para captura.");
        }
        if (casaPecaCapturada == null || casaPecaCapturada.estaVazia()) {
            throw new MovimentoInvalidoException("Peça a ser capturada inválida.");
        }
        if (!destino.estaVazia()) {
            throw new MovimentoInvalidoException("A casa de destino para captura não está vazia.");
        }

        Peca pecaMovendo = origem.getPeca();
        Peca pecaCapturada = casaPecaCapturada.getPeca(); // Ensure this isn't null before calling getCor()

        if (pecaCapturada == null) { // Defensive check, though casaPecaCapturada.estaVazia() should cover it
             throw new MovimentoInvalidoException("Peça a ser capturada não existe.");
        }
        if (pecaMovendo.getCor() == pecaCapturada.getCor()) {
            throw new MovimentoInvalidoException("Não pode capturar peça da mesma cor.");
        }

        destino.setPeca(pecaMovendo);
        origem.setPeca(null);
        casaPecaCapturada.setPeca(null);

        // Promotion logic after capture
        if (!(pecaMovendo instanceof Dama) &&
            ((pecaMovendo.getCor() == Peca.Cor.BRANCA && destino.getLinha() == 0) ||
             (pecaMovendo.getCor() == Peca.Cor.PRETA && destino.getLinha() == TAMANHO - 1))) {
            destino.setPeca(new Dama(pecaMovendo.getCor()));
        }
    }

    public void moverPeca(Casa origem, Casa destino) throws MovimentoInvalidoException {
        if (origem == null || destino == null) {
            throw new MovimentoInvalidoException("Origem ou destino nulos.");
        }
        if (origem.estaVazia()) {
            throw new MovimentoInvalidoException("A casa de origem está vazia.");
        }

        int deltaLinha = Math.abs(origem.getLinha() - destino.getLinha());
        int deltaColuna = Math.abs(origem.getColuna() - destino.getColuna());

        if (deltaLinha == 1 && deltaColuna == 1) {
            // Validar com isMovimentoValido antes de executar
            if (isMovimentoValido(origem, destino)) {
                executarMovimentoSimples(origem, destino);
            } else {
                throw new MovimentoInvalidoException("Movimento simples inválido.");
            }
        } else if (deltaLinha == 2 && deltaColuna == 2) {
            int linhaIntermediaria = origem.getLinha() + (destino.getLinha() - origem.getLinha()) / 2;
            int colunaIntermediaria = origem.getColuna() + (destino.getColuna() - origem.getColuna()) / 2;
            Casa casaPecaCapturada = getCasa(linhaIntermediaria, colunaIntermediaria);

            // Validar com isCapturaValida antes de executar
            if (isCapturaValida(origem, casaPecaCapturada, destino)) {
                executarMovimentoComCaptura(origem, destino, casaPecaCapturada);
            } else {
                throw new MovimentoInvalidoException("Movimento de captura inválido.");
            }
        } else {
            throw new MovimentoInvalidoException("Movimento não é simples nem captura (distância inválida).");
        }
    }

    public boolean isMovimentoValido(Casa origem, Casa destino) {
        if (origem == null || destino == null || origem.estaVazia()) {
            return false;
        }
        if (!destino.estaVazia()) {
            return false; // Peças só podem mover para casas vazias (captura é separada)
        }

        Peca peca = origem.getPeca();
        if (!peca.isMovimentoValido(this, origem, destino)) {
            return false;
        }

        // Path Clearing for Dama (King)
        if (peca instanceof Dama) {
            int linhaDiff = destino.getLinha() - origem.getLinha();
            int colDiff = destino.getColuna() - origem.getColuna();

            int stepLinha = Integer.signum(linhaDiff);
            int stepColuna = Integer.signum(colDiff);

            int currentLinha = origem.getLinha() + stepLinha;
            int currentColuna = origem.getColuna() + stepColuna;

            while (currentLinha != destino.getLinha() || currentColuna != destino.getColuna()) {
                if (currentLinha < 0 || currentLinha >= TAMANHO || currentColuna < 0 || currentColuna >= TAMANHO) {
                    // This case should ideally be prevented by Peca.isMovimentoValido,
                    // but as a safeguard:
                    return false;
                }
                if (!getCasa(currentLinha, currentColuna).estaVazia()) {
                    return false; // Path is blocked
                }
                currentLinha += stepLinha;
                currentColuna += stepColuna;
            }
        }
        return true; // All checks passed
    }

    public boolean isCapturaValida(Casa origem, Casa casaPecaCapturada, Casa destino) {
        // Basic Checks
        if (origem == null || casaPecaCapturada == null || destino == null) {
            return false;
        }
        if (origem.estaVazia() || casaPecaCapturada.estaVazia()) {
            return false;
        }
        if (!destino.estaVazia()) {
            return false;
        }

        // Piece Checks
        Peca pecaMovendo = origem.getPeca();
        Peca pecaASerCapturada = casaPecaCapturada.getPeca();

        if (pecaASerCapturada == null) { // Should be caught by casaPecaCapturada.estaVazia()
            return false;
        }
        if (pecaMovendo.getCor() == pecaASerCapturada.getCor()) {
            return false; // Cannot capture own piece
        }

        // Geometric Validation (Standard Single Jump Capture)
        boolean pecaCapturadaDiagonalmenteAdjacente = Math.abs(origem.getLinha() - casaPecaCapturada.getLinha()) == 1 &&
                                                     Math.abs(origem.getColuna() - casaPecaCapturada.getColuna()) == 1;
        if (!pecaCapturadaDiagonalmenteAdjacente) {
            return false;
        }

        boolean destinoSaltoValido = (origem.getLinha() - casaPecaCapturada.getLinha() == casaPecaCapturada.getLinha() - destino.getLinha()) &&
                                   (origem.getColuna() - casaPecaCapturada.getColuna() == casaPecaCapturada.getColuna() - destino.getColuna());
        if (!destinoSaltoValido) {
            return false;
        }
        // This also implies:
        // Math.abs(casaPecaCapturada.getLinha() - destino.getLinha()) == 1
        // Math.abs(casaPecaCapturada.getColuna() - destino.getColuna()) == 1
        // Math.abs(origem.getLinha() - destino.getLinha()) == 2
        // Math.abs(origem.getColuna() - destino.getColuna()) == 2

        // Piece-Specific Capture Rules (Direction for PecaRegular)
        if (pecaMovendo instanceof PecaRegular) {
            if (pecaMovendo.getCor() == Peca.Cor.BRANCA) {
                // White pieces (BRANCA) must capture by moving "up" the board (decreasing row index)
                if (destino.getLinha() >= origem.getLinha()) {
                    return false;
                }
            } else { // PRETA
                // Black pieces (PRETA) must capture by moving "down" the board (increasing row index)
                if (destino.getLinha() <= origem.getLinha()) {
                    return false;
                }
            }
        }
        // Damas can capture in any diagonal direction, so no specific check needed here for Dama.

        return true; // All checks pass
    }

    public List<Casa> getPossiveisCapturas(Casa origem) {
        List<Casa> capturas = new ArrayList<>();
        if (origem == null || origem.estaVazia()) {
            return capturas;
        }

        // Peca peca = origem.getPeca(); // peca is not directly used for validation logic here, isCapturaValida handles it.

        int[] deltaLinhaOpcoes = {-2, -2, 2, 2};
        int[] deltaColunaOpcoes = {-2, 2, -2, 2};

        for (int i = 0; i < deltaLinhaOpcoes.length; i++) {
            int linhaDestino = origem.getLinha() + deltaLinhaOpcoes[i];
            int colunaDestino = origem.getColuna() + deltaColunaOpcoes[i];

            if (linhaDestino >= 0 && linhaDestino < TAMANHO && colunaDestino >= 0 && colunaDestino < TAMANHO) {
                Casa destino = getCasa(linhaDestino, colunaDestino);

                int linhaIntermediaria = origem.getLinha() + deltaLinhaOpcoes[i] / 2;
                int colunaIntermediaria = origem.getColuna() + deltaColunaOpcoes[i] / 2;
                Casa casaPecaCapturada = getCasa(linhaIntermediaria, colunaIntermediaria);

                if (isCapturaValida(origem, casaPecaCapturada, destino)) {
                    capturas.add(destino);
                }
            }
        }
        return capturas;
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
