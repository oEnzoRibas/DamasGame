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
            // Removed duplicate declarations of dirLinha and dirColuna here
            Casa casaPecaCapturada = null;

            if (peca instanceof Dama) {
                // For Dama, iterate along the diagonal from origem to destino to find the single captured piece
                int pecasEncontradasCount = 0;
                Peca pecaEncontradaNoCaminho = null; // To store the actual Peca object
                Casa casaPecaEncontradaTemporaria = null; // To store the Casa of the piece

                int currL = origem.getLinha() + dirLinha;
                int currC = origem.getColuna() + dirColuna;

                while (currL != destino.getLinha() || currC != destino.getColuna()) {
                    if (currL < 0 || currL >= TAMANHO || currC < 0 || currC >= TAMANHO) { // Should not happen if destino is valid
                        break;
                    }
                    Casa casaAtualNoCaminho = getCasa(currL, currC);
                    if (!casaAtualNoCaminho.estaVazia()) {
                        pecasEncontradasCount++;
                        pecaEncontradaNoCaminho = casaAtualNoCaminho.getPeca();
                        casaPecaEncontradaTemporaria = casaAtualNoCaminho;
                    }
                    currL += dirLinha;
                    currC += dirColuna;
                }

                if (pecasEncontradasCount == 1 && pecaEncontradaNoCaminho != null &&
                    pecaEncontradaNoCaminho.getCor() != peca.getCor()) {
                    casaPecaCapturada = casaPecaEncontradaTemporaria;
                } else {
                    // Not a valid Dama capture (0 or >1 pieces in path, or the single piece is friendly)
                    // This will then fall through to simple move logic or throw.
                }
            } else { // PecaRegular
                // For PecaRegular, the captured piece must be exactly one step from origin
                // and the destination two steps from origin.
                // This specific delta check (deltaLinhaAbs > 1) implies deltaLinhaAbs == 2 for PecaRegular.
                if (deltaLinhaAbs == 2) { // Standard jump distance for PecaRegular
                    int linhaIntermediaria = origem.getLinha() + dirLinha;
                    int colunaIntermediaria = origem.getColuna() + dirColuna;
                     if (linhaIntermediaria >= 0 && linhaIntermediaria < TAMANHO &&
                         colunaIntermediaria >= 0 && colunaIntermediaria < TAMANHO) {
                        casaPecaCapturada = getCasa(linhaIntermediaria, colunaIntermediaria);
                        // Further checks if casaPecaCapturada is not null, not empty, and enemy are below
                    }
                }
            }

            // Now, common validation for both Dama (if a single enemy was identified) and PecaRegular
            if (casaPecaCapturada != null && !casaPecaCapturada.estaVazia() &&
                casaPecaCapturada.getPeca().getCor() != peca.getCor()) {
                if (isCapturaValida(origem, casaPecaCapturada, destino)) {
                    executarMovimentoComCaptura(origem, destino, casaPecaCapturada);
                    return;
                } else {
                    throw new MovimentoInvalidoException("Tentativa de captura inválida (isCapturaValida falhou).");
                }
            } else if (peca instanceof PecaRegular) {
                // This case handles if PecaRegular tried to jump but intermediate was empty, friendly, or off-board (handled by casaPecaCapturada being null)
                throw new MovimentoInvalidoException("Peça regular: Salto inválido (sem peça inimiga para capturar ou intermediária fora do tabuleiro).");
            }
            // If Dama and no valid single enemy piece was found for capture, fall through to simple move logic.
        }

        // Attempt Simple Move Logic (if not a valid capture or if capture attempt failed in a way that allows simple move)
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
        // Basic Checks
        if (origem == null || casaPecaCapturada == null || destino == null) return false;
        if (origem.estaVazia() || casaPecaCapturada.estaVazia()) return false;
        if (!destino.estaVazia()) return false;

        Peca pecaMovendo = origem.getPeca();
        if (pecaMovendo == null) return false; // Should be caught by origem.estaVazia()

        Peca pecaCapturadaCorpo = casaPecaCapturada.getPeca();
        if (pecaCapturadaCorpo == null) return false; // Should be caught by casaPecaCapturada.estaVazia()

        if (pecaMovendo.getCor() == pecaCapturadaCorpo.getCor()) return false; // Cannot capture own piece

        if (!(pecaMovendo instanceof Dama)) {
            // --- PecaRegular Capture Logic ---
            // casaPecaCapturada must be diagonally adjacent to origem (1 step away)
            if (Math.abs(casaPecaCapturada.getLinha() - origem.getLinha()) != 1 ||
                Math.abs(casaPecaCapturada.getColuna() - origem.getColuna()) != 1) {
                return false;
            }
            // destino must be exactly one diagonal step beyond casaPecaCapturada.
            if (!(Math.abs(destino.getLinha() - casaPecaCapturada.getLinha()) == 1 &&
                  Math.abs(destino.getColuna() - casaPecaCapturada.getColuna()) == 1)) {
                return false;
            }
            // origem, casaPecaCapturada, destino must be on the same straight diagonal line (implicit by steps)
            if (!(Integer.signum(casaPecaCapturada.getLinha() - origem.getLinha()) == Integer.signum(destino.getLinha() - casaPecaCapturada.getLinha()) &&
                  Integer.signum(casaPecaCapturada.getColuna() - origem.getColuna()) == Integer.signum(destino.getColuna() - casaPecaCapturada.getColuna()))) {
                return false;
            }
            return true; // All checks for PecaRegular capture pass
        } else {
            // --- Dama "Flying Capture" Logic ---
            // Collinearity & Diagonal Check
            int deltaOrigemCapLinha = casaPecaCapturada.getLinha() - origem.getLinha();
            int deltaOrigemCapColuna = casaPecaCapturada.getColuna() - origem.getColuna();
            int deltaCapDestLinha = destino.getLinha() - casaPecaCapturada.getLinha();
            int deltaCapDestColuna = destino.getColuna() - casaPecaCapturada.getColuna();

            if (Math.abs(deltaOrigemCapLinha) != Math.abs(deltaOrigemCapColuna) || // origem to captured is not diagonal
                Math.abs(deltaCapDestLinha) != Math.abs(deltaCapDestColuna) ||     // captured to destino is not diagonal
                deltaOrigemCapLinha == 0) { // Not a diagonal move (no change in line or col)
                return false;
            }

            // Must be in the same direction
            if (Integer.signum(deltaOrigemCapLinha) != Integer.signum(deltaCapDestLinha) ||
                Integer.signum(deltaOrigemCapColuna) != Integer.signum(deltaCapDestColuna)) {
                return false;
            }

            // casaPecaCapturada must be strictly between origem and destino
            // This is implicitly covered by the path clearing and direction checks,
            // as path clearing will fail if casaPecaCapturada is not on the path.
            // Explicit check:
            if (!(Math.abs(origem.getLinha() - destino.getLinha()) > Math.abs(origem.getLinha() - casaPecaCapturada.getLinha()) &&
                  Math.abs(origem.getLinha() - destino.getLinha()) > Math.abs(casaPecaCapturada.getLinha() - destino.getLinha()))){
                 //This check ensures casaPecaCapturada is between origem and destino.
                 //And also that casaPecaCapturada is not the same as origem or destino.
                 return false;
            }


            // Path Clearing from origem to casaPecaCapturada
            int stepLinhaPath1 = Integer.signum(deltaOrigemCapLinha);
            int stepColunaPath1 = Integer.signum(deltaOrigemCapColuna);
            int currL = origem.getLinha() + stepLinhaPath1;
            int currC = origem.getColuna() + stepColunaPath1;
            while ((currL != casaPecaCapturada.getLinha()) || (currC != casaPecaCapturada.getColuna())) {
                if (currL < 0 || currL >= TAMANHO || currC < 0 || currC >= TAMANHO) return false; // Off board
                if (!getCasa(currL, currC).estaVazia()) return false; // Path to captured piece blocked
                currL += stepLinhaPath1;
                currC += stepColunaPath1;
            }

            // Path Clearing from casaPecaCapturada to destino
            int stepLinhaPath2 = Integer.signum(deltaCapDestLinha); // Should be same as stepLinhaPath1 due to earlier checks
            int stepColunaPath2 = Integer.signum(deltaCapDestColuna); // Should be same as stepColunaPath1
            currL = casaPecaCapturada.getLinha() + stepLinhaPath2;
            currC = casaPecaCapturada.getColuna() + stepColunaPath2;
            while ((currL != destino.getLinha()) || (currC != destino.getColuna())) {
                if (currL < 0 || currL >= TAMANHO || currC < 0 || currC >= TAMANHO) return false; // Off board
                if (!getCasa(currL, currC).estaVazia()) return false; // Path to landing spot blocked
                currL += stepLinhaPath2;
                currC += stepColunaPath2;
            }
            return true; // All Dama checks pass
        }
    }

    public List<Casa> getPossiveisCapturas(Casa origem) {
        List<Casa> capturas = new ArrayList<>();
        if (origem == null || origem.estaVazia()) {
            return capturas;
        }

        Peca peca = origem.getPeca();
        int[] deltaLinhaDirs = {-1, -1, 1, 1}; // Directions for row changes
        int[] deltaColunaDirs = {-1, 1, -1, 1}; // Directions for col changes

        if (!(peca instanceof Dama)) {
            // --- PecaRegular getPossiveisCapturas logic ---
            for (int i = 0; i < 4; i++) {
                int lCap = origem.getLinha() + deltaLinhaDirs[i];
                int cCap = origem.getColuna() + deltaColunaDirs[i];

                if (lCap >= 0 && lCap < TAMANHO && cCap >= 0 && cCap < TAMANHO) {
                    Casa casaPecaACapturar = getCasa(lCap, cCap);
                    if (casaPecaACapturar != null && !casaPecaACapturar.estaVazia() && casaPecaACapturar.getPeca().getCor() != peca.getCor()) {
                        int lDest = lCap + deltaLinhaDirs[i];
                        int cDest = cCap + deltaColunaDirs[i];
                        if (lDest >= 0 && lDest < TAMANHO && cDest >= 0 && cDest < TAMANHO) {
                            Casa destinoRegular = getCasa(lDest, cDest);
                            if (isCapturaValida(origem, casaPecaACapturar, destinoRegular)) {
                                capturas.add(destinoRegular);
                            }
                        }
                    }
                }
            }
        } else {
            // --- Dama "Flying Capture" getPossiveisCapturas logic ---
            for (int d = 0; d < 4; d++) { // For each of the 4 diagonal directions
                int dirLinha = deltaLinhaDirs[d];
                int dirColuna = deltaColunaDirs[d];

                // Scan along the diagonal for a piece to capture
                for (int i = 1; i < TAMANHO; i++) {
                    int lCap = origem.getLinha() + i * dirLinha;
                    int cCap = origem.getColuna() + i * dirColuna;

                    if (lCap < 0 || lCap >= TAMANHO || cCap < 0 || cCap >= TAMANHO) {
                        break; // Off-board, stop scanning this direction
                    }

                    Casa casaIntermedia = getCasa(lCap, cCap);
                    if (casaIntermedia.estaVazia()) {
                        continue; // Path to potential capture is clear so far
                    }

                    // Piece encountered
                    if (casaIntermedia.getPeca().getCor() == peca.getCor()) {
                        break; // Blocked by a friendly piece, stop scanning this direction
                    } else {
                        // Enemy piece found at casaIntermedia, this is the casaPecaACapturar
                        // Now, scan for landing spots beyond casaIntermedia
                        for (int j = 1; j < TAMANHO; j++) {
                            int lDest = casaIntermedia.getLinha() + j * dirLinha;
                            int cDest = casaIntermedia.getColuna() + j * dirColuna;

                            if (lDest < 0 || lDest >= TAMANHO || cDest < 0 || cDest >= TAMANHO) {
                                break; // Off-board, stop scanning for landing spots in this direction
                            }

                            Casa casaDestino = getCasa(lDest, cDest);
                            if (!casaDestino.estaVazia()) {
                                break; // Landing path blocked, stop scanning for landing spots
                            }

                            // Basic validation for adding to list. Full validation in isCapturaValida.
                            // The path checks are implicitly done by these loops.
                            // We are adding all empty squares after the first enemy.
                            // isCapturaValida will be called by moverPeca for the chosen one.
                            capturas.add(casaDestino);
                        }
                        break; // Dama captures only the first encountered enemy piece in a direction
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
