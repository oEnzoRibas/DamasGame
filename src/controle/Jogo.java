package controle;

import modelo.Tabuleiro;
import modelo.Jogador;
import modelo.Peca;
import modelo.Casa;
import util.MovimentoInvalidoException;
import java.util.Scanner;

public class Jogo {
    private Tabuleiro tabuleiro;
    private Jogador jogador1;
    private Jogador jogador2;
    private Jogador jogadorAtual;
    private Scanner scanner;

    public void iniciar() {
        tabuleiro = new Tabuleiro();
        scanner = new Scanner(System.in);
        jogador1 = new Jogador("Jogador 1 (Brancas)", Peca.Cor.BRANCA);
        jogador2 = new Jogador("Jogador 2 (Pretas)", Peca.Cor.PRETA);
        jogadorAtual = jogador1;

        while (true) {
            tabuleiro.mostrar();
            System.out.println("Turno de: " + jogadorAtual.getNome());
            System.out.println("Digite sua jogada no formato 'linhaOrigem colunaOrigem linhaDestino colunaDestino' (ex: 2 1 3 2):");
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

                // Validação básica de movimento (placeholder, será melhorada)
                if (!tabuleiro.isMovimentoValido(casaOrigem, casaDestino)) {
                    throw new MovimentoInvalidoException("Movimento inválido pelas regras do jogo.");
                }

                tabuleiro.moverPeca(casaOrigem, casaDestino);

                if (tabuleiro.verificarVitoria(jogadorAtual.getCorPecas())) {
                    tabuleiro.mostrar();
                    System.out.println("O jogador " + jogadorAtual.getNome() + " venceu!");
                    break;
                }

                jogadorAtual = (jogadorAtual == jogador1) ? jogador2 : jogador1;

            } catch (MovimentoInvalidoException | NumberFormatException e) {
                System.out.println("Erro: " + e.getMessage());
                System.out.println("Tente novamente.");
                // continue; // O loop já continua naturalmente
            }
        }
        scanner.close();
    }
}
