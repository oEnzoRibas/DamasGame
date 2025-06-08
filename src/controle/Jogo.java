package controle;

import modelo.Tabuleiro;
import modelo.Jogador;
import modelo.Peca;
import modelo.Casa;
import modelo.Peca; // Added for Peca.Cor
import modelo.Tabuleiro; // Added for Tabuleiro.TAMANHO
import util.MovimentoInvalidoException;
import java.util.Scanner;
import java.util.List;
import java.util.Map; // Added for Map
import java.util.HashMap; // Added for HashMap
import java.util.ArrayList; // Added for ArrayList in helper

public class Jogo {
    private Tabuleiro tabuleiro;
    private Jogador jogador1;
    private Jogador jogador2;
    private Jogador jogadorAtual;
    private Scanner scanner;
    private boolean emSequenciaDeCaptura = false;
    private Casa pecaEmSequencia = null;

    // Helper method to find all mandatory captures for the current player
    private Map<Casa, List<Casa>> encontrarCapturasObrigatorias(Jogador jogador) {
        Map<Casa, List<Casa>> capturasObrigatorias = new HashMap<>();
        for (int linha = 0; linha < Tabuleiro.TAMANHO; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.TAMANHO; coluna++) {
                Casa casaOrigem = tabuleiro.getCasa(linha, coluna);
                if (casaOrigem != null && !casaOrigem.estaVazia() && casaOrigem.getPeca().getCor() == jogador.getCorPecas()) {
                    List<Casa> possiveisCapturasParaPeca = tabuleiro.getPossiveisCapturas(casaOrigem);
                    if (!possiveisCapturasParaPeca.isEmpty()) {
                        capturasObrigatorias.put(casaOrigem, possiveisCapturasParaPeca);
                    }
                }
            }
        }
        return capturasObrigatorias;
    }

    public void iniciar() {
        tabuleiro = new Tabuleiro();
        scanner = new Scanner(System.in);
        // Make sure Peca.Cor is accessible or use direct values if defined elsewhere.
        jogador1 = new Jogador("Jogador 1 (Brancas)", Peca.Cor.BRANCA);
        jogador2 = new Jogador("Jogador 2 (Pretas)", Peca.Cor.PRETA);
        jogadorAtual = jogador1;
        emSequenciaDeCaptura = false;
        pecaEmSequencia = null;

        Map<Casa, List<Casa>> mandatoryCapturesMap = new HashMap<>();

        while (true) {
            tabuleiro.mostrar();
            System.out.println("Turno de: " + jogadorAtual.getNome());

            if (!emSequenciaDeCaptura) {
                mandatoryCapturesMap = encontrarCapturasObrigatorias(jogadorAtual);
                if (!mandatoryCapturesMap.isEmpty()) {
                    System.out.println("Captura obrigatória! Você deve realizar uma das seguintes capturas:");
                    for (Map.Entry<Casa, List<Casa>> entry : mandatoryCapturesMap.entrySet()) {
                        Casa origem = entry.getKey();
                        for (Casa destino : entry.getValue()) {
                            System.out.println("De " + origem.getLinha() + "," + origem.getColuna() +
                                    " para " + destino.getLinha() + "," + destino.getColuna());
                        }
                    }
                }
            }

            if (emSequenciaDeCaptura) {
                System.out.println("Continue a sequência de capturas com a peça em " + pecaEmSequencia.getLinha() + "," + pecaEmSequencia.getColuna() + ".");
                System.out.println("Digite sua jogada no formato 'linhaOrigem colunaOrigem linhaDestino colunaDestino' (ex: " + pecaEmSequencia.getLinha() + " " + pecaEmSequencia.getColuna() + " L D):");
            } else {
                System.out.println("Digite sua jogada no formato 'linhaOrigem colunaOrigem linhaDestino colunaDestino' (ex: 2 1 3 2):");
            }
            String input = scanner.nextLine();
            String[] parts = input.split(" ");

            if (parts.length != 4) {
                System.out.println("Erro: Entrada inválida. Use o formato: linhaOrigem colunaOrigem linhaDestino colunaDestino");
                continue;
            }

            try {
                int linhaOrigem = Integer.parseInt(parts[0]);
                int colunaOrigem = Integer.parseInt(parts[1]);
                int linhaDestino = Integer.parseInt(parts[2]);
                int colunaDestino = Integer.parseInt(parts[3]);

                Casa casaOrigem = tabuleiro.getCasa(linhaOrigem, colunaOrigem);
                Casa casaDestino = tabuleiro.getCasa(linhaDestino, colunaDestino);

                if (casaOrigem == null || casaDestino == null) {
                    throw new MovimentoInvalidoException("Coordenadas fora do tabuleiro.");
                }
                if (casaOrigem.estaVazia()) {
                    throw new MovimentoInvalidoException("Casa de origem está vazia.");
                }
                if (casaOrigem.getPeca().getCor() != jogadorAtual.getCorPecas()) {
                    throw new MovimentoInvalidoException("Não é a cor da sua peça.");
                }

                if (emSequenciaDeCaptura) {
                    if (casaOrigem != pecaEmSequencia) {
                        throw new MovimentoInvalidoException("Deve continuar a sequência com a mesma peça em " + pecaEmSequencia.getLinha() + "," + pecaEmSequencia.getColuna() + ".");
                    }
                } else if (!mandatoryCapturesMap.isEmpty()) {
                    // Enforce mandatory capture
                    if (!mandatoryCapturesMap.containsKey(casaOrigem)) {
                        throw new MovimentoInvalidoException("Movimento inválido. Você deve realizar uma das capturas obrigatórias listadas com a peça correta.");
                    }
                    List<Casa> destinosPossiveisParaOrigem = mandatoryCapturesMap.get(casaOrigem);
                    if (!destinosPossiveisParaOrigem.contains(casaDestino)) {
                        throw new MovimentoInvalidoException("Movimento inválido. O destino escolhido não é uma captura válida para a peça de origem selecionada.");
                    }
                    // If we are here, the selected move is a mandatory capture.
                    // The moverPeca method will validate it again, which is fine.
                }


                // The core logic of moving a piece, determining if it's a capture or simple move,
                // and then calling tabuleiro.moverPeca should largely remain.
                // However, if mandatory captures exist, a simple move should be disallowed.

                boolean foiCaptura = false; // Will be set by tabuleiro.moverPeca or inferred

                // Check if the move is a capture based on distance
                // This check is now more of a general validation; mandatory capture has specific validation above.
                int deltaLinhaAbs = Math.abs(linhaOrigem - linhaDestino);
                int deltaColunaAbs = Math.abs(colunaOrigem - colunaDestino);

                if (!mandatoryCapturesMap.isEmpty() && !(deltaLinhaAbs > 1 && deltaColunaAbs > 1 && deltaLinhaAbs == deltaColunaAbs) ) {
                    // If mandatory captures exist, only capture moves are allowed.
                    // A simple move (delta == 1) or any non-diagonal/non-jump move is invalid.
                    throw new MovimentoInvalidoException("Movimento inválido. Uma captura é obrigatória.");
                }

                // At this point, if mandatoryCapturesMap was not empty, the player has chosen one of the mandatory captures.
                // If mandatoryCapturesMap was empty, any valid move is allowed.
                // The tabuleiro.moverPeca method handles the actual execution and internal validation (including if it's a capture or simple).

                // We need to know if a capture occurred to handle sequence captures.
                // One way is to check the distance, as moverPeca itself doesn't return this.
                // Or, check if a piece was actually captured. Let's rely on distance for now,
                // as moverPeca will throw if it's an invalid capture.

                tabuleiro.moverPeca(casaOrigem, casaDestino); // This will throw if the move is invalid (e.g., simple move into occupied, bad jump)

                // Determine if a capture happened for sequence logic.
                // This relies on the fact that moverPeca would have thrown an error if it wasn't a valid jump
                // or a valid simple move.
                if (deltaLinhaAbs > 1) { // Captures always involve moving more than 1 row/col
                    foiCaptura = true;
                }


                if (tabuleiro.verificarVitoria(jogadorAtual.getCorPecas())) {
                    tabuleiro.mostrar();
                    System.out.println("O jogador " + jogadorAtual.getNome() + " venceu!");
                    emSequenciaDeCaptura = false;
                    pecaEmSequencia = null;
                    break;
                }

                if (foiCaptura) {
                    Casa pecaQueCapturouUltimaPosicao = casaDestino;
                    // Check for sequence captures only if the current player is still the same
                    // (which they will be if emSequenciaDeCaptura was false before this capture)
                    // OR if they were already in a sequence.
                    List<Casa> novasCapturasPossiveis = tabuleiro.getPossiveisCapturas(pecaQueCapturouUltimaPosicao);
                    if (!novasCapturasPossiveis.isEmpty()) {
                        System.out.println("Você realizou uma captura e pode capturar novamente com a mesma peça.");
                        emSequenciaDeCaptura = true;
                        pecaEmSequencia = pecaQueCapturouUltimaPosicao;
                        // Do not switch player, continue turn
                    } else {
                        emSequenciaDeCaptura = false;
                        pecaEmSequencia = null;
                        jogadorAtual = (jogadorAtual == jogador1) ? jogador2 : jogador1; // Switch player
                    }
                } else { // Not a capture
                    emSequenciaDeCaptura = false;
                    pecaEmSequencia = null;
                    jogadorAtual = (jogadorAtual == jogador1) ? jogador2 : jogador1; // Switch player
                }

                // If emSequenciaDeCaptura is true at this point, player does not switch.
                // Loop continues with the same player.

            } catch (MovimentoInvalidoException | NumberFormatException e) {
                System.out.println("Erro: " + e.getMessage());
                System.out.println("Tente novamente.");
            }
        }
        scanner.close();
    }
}