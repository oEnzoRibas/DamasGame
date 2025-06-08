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
                    // Se for Dama, imprime DB, senão B
                    if (peca instanceof Dama) {
                        System.out.print("DB ");
                    } else {
                        System.out.print(" B ");
                    }
                } else {
                    // Se for Dama, imprime DP, senão P
                    if (peca instanceof Dama) {
                        System.out.print("DP ");
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

        Peca peca = origem.getPeca();
        int deltaLinhaAbs = Math.abs(origem.getLinha() - destino.getLinha());
        int deltaColunaAbs = Math.abs(origem.getColuna() - destino.getColuna());

        // Attempt Capture Logic First
        // A diagonal move with distance > 1 is potentially a capture
        if (deltaLinhaAbs > 1 && deltaLinhaAbs == deltaColunaAbs) {
            int dirLinha = Integer.signum(destino.getLinha() - origem.getLinha());
            int dirColuna = Integer.signum(destino.getColuna() - origem.getColuna());

            // For a capture, there must be exactly one piece being jumped.
            // For PecaRegular, this piece is 1 step away from origin.
            // For Dama, this piece is 1 step away from origin (closest piece jumped).
            // The isCapturaValida method will verify if other pieces are on the path for Dama.

            int linhaPecaIntermediaria = -1;
            int colunaPecaIntermediaria = -1;

            // Find the first piece along the path of the jump.
            // For a Dama, this might not be immediately adjacent if there are empty squares first.
            // However, our isCapturaValida expects casaPecaCapturada to be the one actually jumped.
            // So we iterate from origin to find the *first* non-empty square along the jump path.
            // If that piece is an enemy and the rest of the path is valid, it's a capture.

            Casa casaPecaCapturada = null;
            // Iterate one step at a time from origin towards destino to find the piece to be captured.
            // Note: For a valid capture, the piece to be captured must be exactly one step away
            // from the origin for PecaRegular, or the first piece encountered for Dama along the direct path.
            // The new isCapturaValida already takes care of Dama's path clearing beyond the captured piece.

            // Simplified: the piece to be captured is always one step from origin in the direction of the jump.
            // If that piece is not there or not an enemy, it's not a valid jump start for PecaRegular.
            // For Dama, if that piece is not there, it's a simple move, not a capture.
            // If it IS there and IS an enemy, isCapturaValida will check the rest.

            linhaPecaIntermediaria = origem.getLinha() + dirLinha;
            colunaPecaIntermediaria = origem.getColuna() + dirColuna;

            if (linhaPecaIntermediaria >= 0 && linhaPecaIntermediaria < TAMANHO &&
                    colunaPecaIntermediaria >= 0 && colunaPecaIntermediaria < TAMANHO) {

                casaPecaCapturada = getCasa(linhaPecaIntermediaria, colunaPecaIntermediaria);

                if (casaPecaCapturada != null && !casaPecaCapturada.estaVazia() &&
                        casaPecaCapturada.getPeca().getCor() != peca.getCor()) {
                    // Potential capture of an enemy piece
                    if (isCapturaValida(origem, casaPecaCapturada, destino)) {
                        executarMovimentoComCaptura(origem, destino, casaPecaCapturada);
                        return;
                    } else {
                        // isCapturaValida returned false (e.g. path blocked for Dama, or invalid jump for regular)
                        throw new MovimentoInvalidoException("Tentativa de captura inválida (isCapturaValida falhou).");
                    }
                } else if (peca instanceof PecaRegular) {
                    // PecaRegular tried to jump, but the intermediate square is empty or has a friendly piece.
                    throw new MovimentoInvalidoException("Peça regular não pode saltar sobre casa vazia ou amiga.");
                }
                // If Dama and the intermediate square (1 step away) is empty or friendly,
                // it's not a capture starting at that intermediate square. It might be a long simple move.
                // So we fall through to simple move logic.
            } else if (peca instanceof PecaRegular) {
                // Intermediate square for PecaRegular's jump is off-board.
                throw new MovimentoInvalidoException("Salto inválido para peça regular (intermediária fora do tabuleiro).");
            }
            // If Dama and intermediate square is off-board, it's not a capture; fall through.
        }

        // Attempt Simple Move Logic (if not a capture or if capture attempt failed in a way that allows simple move)
        // isMovimentoValido already handles path clearing for Dama's simple moves
        if (isMovimentoValido(origem, destino)) {
            executarMovimentoSimples(origem, destino);
            return;
        }

        // If neither capture nor simple move was successful
        throw new MovimentoInvalidoException("Movimento inválido (não é captura válida nem movimento simples válido).");
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
        // Basic checks
        if (origem == null || casaPecaCapturada == null || destino == null) {
            return false;
        }
        if (origem.estaVazia() || casaPecaCapturada.estaVazia()) {
            return false;
        }
        if (!destino.estaVazia()) {
            return false;
        }
        if (origem.getPeca().getCor() == casaPecaCapturada.getPeca().getCor()) {
            return false;
        }

        // Geometric checks
        // casaPecaCapturada must be diagonally adjacent to origem (1 step away)
        if (Math.abs(casaPecaCapturada.getLinha() - origem.getLinha()) != 1 ||
                Math.abs(casaPecaCapturada.getColuna() - origem.getColuna()) != 1) {
            return false;
        }

        // origem, casaPecaCapturada, destino must be on the same straight diagonal line
        if (!(Integer.signum(casaPecaCapturada.getLinha() - origem.getLinha()) == Integer.signum(destino.getLinha() - casaPecaCapturada.getLinha()) &&
                Integer.signum(casaPecaCapturada.getColuna() - origem.getColuna()) == Integer.signum(destino.getColuna() - casaPecaCapturada.getColuna()))) {
            return false;
        }

        // destino must be further from origem than casaPecaCapturada is
        if (!(Math.abs(destino.getLinha() - origem.getLinha()) > Math.abs(casaPecaCapturada.getLinha() - origem.getLinha()))) {
            return false;
        }


        Peca peca = origem.getPeca();
        if (peca instanceof Dama) {
            // Path clearing: Iterate from the square diagonally after casaPecaCapturada towards destino.
            // All squares on this path (not including destino itself) must be empty.
            int stepLinha = Integer.signum(destino.getLinha() - origem.getLinha());
            int stepColuna = Integer.signum(destino.getColuna() - origem.getColuna());

            int currentLinha = casaPecaCapturada.getLinha() + stepLinha;
            int currentColuna = casaPecaCapturada.getColuna() + stepColuna;

            while (currentLinha != destino.getLinha() || currentColuna != destino.getColuna()) {
                if (currentLinha < 0 || currentLinha >= TAMANHO || currentColuna < 0 || currentColuna >= TAMANHO) {
                    return false; // Should not happen if destino is on board and diagonal check is correct
                }
                if (!getCasa(currentLinha, currentColuna).estaVazia()) {
                    return false; // Path is blocked for Dama's landing
                }
                currentLinha += stepLinha;
                currentColuna += stepColuna;
            }
            return true; // All checks for Dama capture pass
        } else { // PecaRegular
            // destino must be exactly one diagonal step beyond casaPecaCapturada.
            // This means destino is 2 steps from origem.
            if (Math.abs(destino.getLinha() - origem.getLinha()) == 2 &&
                    Math.abs(destino.getColuna() - origem.getColuna()) == 2) {
                // The other geometric checks already ensure it's in the same line and direction.
                return true; // All checks for PecaRegular capture pass
            }
        }
        return false; // Otherwise
    }

    public List<Casa> getPossiveisCapturas(Casa origem) {
        List<Casa> capturas = new ArrayList<>();
        if (origem == null || origem.estaVazia()) {
            return capturas;
        }

        Peca peca = origem.getPeca();
        int[] dr = {-1, -1, 1, 1}; // delta row for diagonal checks
        int[] dc = {-1, 1, -1, 1}; // delta col for diagonal checks

        for (int i = 0; i < 4; i++) { // Iterate over the 4 diagonal directions
            int linhaPecaIntermediaria = origem.getLinha() + dr[i];
            int colunaPecaIntermediaria = origem.getColuna() + dc[i];

            // Check if casaPecaACapturar is on board
            if (linhaPecaIntermediaria >= 0 && linhaPecaIntermediaria < TAMANHO &&
                    colunaPecaIntermediaria >= 0 && colunaPecaIntermediaria < TAMANHO) {

                Casa casaPecaACapturar = getCasa(linhaPecaIntermediaria, colunaPecaIntermediaria);

                // Check if casaPecaACapturar is not empty and contains an enemy piece
                if (casaPecaACapturar != null && !casaPecaACapturar.estaVazia() &&
                        casaPecaACapturar.getPeca().getCor() != peca.getCor()) {

                    if (peca instanceof Dama) {
                        int stepLinha = dr[i];
                        int stepColuna = dc[i];
                        int currentLinhaDest = linhaPecaIntermediaria + stepLinha;
                        int currentColunaDest = colunaPecaIntermediaria + stepColuna;

                        // Loop while currentLinhaDest / currentColunaDest are on board
                        while (currentLinhaDest >= 0 && currentLinhaDest < TAMANHO &&
                                currentColunaDest >= 0 && currentColunaDest < TAMANHO) {

                            Casa destino = getCasa(currentLinhaDest, currentColunaDest);
                            if (destino.estaVazia()) {
                                if (isCapturaValida(origem, casaPecaACapturar, destino)) {
                                    capturas.add(destino);
                                }
                            } else { // Path blocked for Dama's landing
                                break; // Stop checking further along this diagonal
                            }
                            currentLinhaDest += stepLinha;
                            currentColunaDest += stepColuna;
                        }
                    } else { // PecaRegular
                        int linhaDestinoRegular = linhaPecaIntermediaria + dr[i];
                        int colunaDestinoRegular = colunaPecaIntermediaria + dc[i];

                        if (linhaDestinoRegular >= 0 && linhaDestinoRegular < TAMANHO &&
                                colunaDestinoRegular >= 0 && colunaDestinoRegular < TAMANHO) {

                            Casa destinoRegular = getCasa(linhaDestinoRegular, colunaDestinoRegular);
                            // No need to check if destinoRegular is empty here, isCapturaValida will do it.
                            if (isCapturaValida(origem, casaPecaACapturar, destinoRegular)) {
                                capturas.add(destinoRegular);
                            }
                        }
                    }
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