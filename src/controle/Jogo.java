package controle;

import modelo.Tabuleiro;
import modelo.Jogador;
import modelo.Peca;
import modelo.Casa;
import util.MovimentoInvalidoException;
import java.util.Scanner;
import java.util.List; // For List<Casa>

public class Jogo {
    private Tabuleiro tabuleiro;
    private Jogador jogador1;
    private Jogador jogador2;
    private Jogador jogadorAtual;
    private Scanner scanner;
    private boolean emSequenciaDeCaptura = false;
    private Casa pecaEmSequencia = null;

    public void iniciar() {
        tabuleiro = new Tabuleiro();
        scanner = new Scanner(System.in);
        jogador1 = new Jogador("Jogador 1 (Brancas)", Peca.Cor.BRANCA);
        jogador2 = new Jogador("Jogador 2 (Pretas)", Peca.Cor.PRETA);
        jogadorAtual = jogador1;
        emSequenciaDeCaptura = false;
        pecaEmSequencia = null;

        while (true) {
            tabuleiro.mostrar();
            System.out.println("Turno de: " + jogadorAtual.getNome());

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

                if (emSequenciaDeCaptura) {
                    if (casaOrigem != pecaEmSequencia) {
                        throw new MovimentoInvalidoException("Deve continuar a sequência com a mesma peça em " + pecaEmSequencia.getLinha() + "," + pecaEmSequencia.getColuna() + ".");
                    }
                }

                if (casaOrigem == null || casaDestino == null) {
                    throw new MovimentoInvalidoException("Coordenadas fora do tabuleiro.");
                }
                if (casaOrigem.estaVazia()) {
                    throw new MovimentoInvalidoException("Casa de origem está vazia.");
                }
                if (casaOrigem.getPeca().getCor() != jogadorAtual.getCorPecas()) {
                    throw new MovimentoInvalidoException("Não é a cor da sua peça.");
                }

                int deltaLinhaAbs = Math.abs(linhaOrigem - linhaDestino);
                int deltaColunaAbs = Math.abs(colunaOrigem - colunaDestino);
                boolean foiCaptura = false;

                if (deltaLinhaAbs == 1 && deltaColunaAbs == 1) { // Movimento simples
                    if (emSequenciaDeCaptura) {
                        throw new MovimentoInvalidoException("Em sequência de captura, deve realizar outra captura.");
                    }
                    if (tabuleiro.isMovimentoValido(casaOrigem, casaDestino)) {
                        tabuleiro.moverPeca(casaOrigem, casaDestino);
                    } else {
                        throw new MovimentoInvalidoException("Movimento simples inválido.");
                    }
                } else if (deltaLinhaAbs == 2 && deltaColunaAbs == 2) { // Potencial captura
                    int linhaIntermediaria = linhaOrigem + (linhaDestino - linhaOrigem) / 2;
                    int colunaIntermediaria = colunaOrigem + (colunaDestino - colunaOrigem) / 2;
                    Casa casaPecaACapturar = tabuleiro.getCasa(linhaIntermediaria, colunaIntermediaria);

                    if (tabuleiro.isCapturaValida(casaOrigem, casaPecaACapturar, casaDestino)) {
                        tabuleiro.moverPeca(casaOrigem, casaDestino);
                        foiCaptura = true;
                    } else {
                        throw new MovimentoInvalidoException("Movimento de captura inválido.");
                    }
                } else {
                    throw new MovimentoInvalidoException("Movimento inválido: distância não corresponde a simples ou captura.");
                }

                if (tabuleiro.verificarVitoria(jogadorAtual.getCorPecas())) {
                    tabuleiro.mostrar();
                    System.out.println("O jogador " + jogadorAtual.getNome() + " venceu!");
                    emSequenciaDeCaptura = false; // Fim de jogo
                    pecaEmSequencia = null;
                    break;
                }

                if (foiCaptura) {
                    Casa pecaQueCapturouUltimaPosicao = casaDestino; // Peça está agora no destino
                    List<Casa> novasCapturasPossiveis = tabuleiro.getPossiveisCapturas(pecaQueCapturouUltimaPosicao);
                    if (!novasCapturasPossiveis.isEmpty()) {
                        System.out.println("Você realizou uma captura e pode capturar novamente. Próxima jogada deve ser com a mesma peça.");
                        emSequenciaDeCaptura = true;
                        pecaEmSequencia = pecaQueCapturouUltimaPosicao;
                        // Não troca o jogador, continua o turno
                        continue;
                    } else {
                        // Não há mais capturas, encerra a sequência
                        emSequenciaDeCaptura = false;
                        pecaEmSequencia = null;
                    }
                } else {
                    // Se não foi captura, ou se foi uma captura que não permite sequência
                    emSequenciaDeCaptura = false;
                    pecaEmSequencia = null;
                }

                // Trocar jogador apenas se não estiver em sequência de captura ou se a sequência terminou
                if (!emSequenciaDeCaptura) {
                    jogadorAtual = (jogadorAtual == jogador1) ? jogador2 : jogador1;
                }

            } catch (MovimentoInvalidoException | NumberFormatException e) {
                System.out.println("Erro: " + e.getMessage());
                System.out.println("Tente novamente.");
                // continue; // O loop já continua naturalmente
            }
        }
        scanner.close();
    }
}
