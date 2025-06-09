package controle;

import modelo.Tabuleiro;
import modelo.Jogador;
import modelo.Peca;
import modelo.Casa;
import modelo.Peca; 
import modelo.Tabuleiro; 
import util.MovimentoInvalidoException;
import java.util.Scanner;
import java.util.List;
import java.util.Map; 
import java.util.HashMap; 
import java.util.ArrayList; 

public class Jogo {
    private Tabuleiro tabuleiro;
    private Jogador jogador1;
    private Jogador jogador2;
    private Jogador jogadorAtual;
    private Scanner scanner;
    private boolean emSequenciaDeCaptura = false;
    private Casa pecaEmSequencia = null;

    /*

    Retorno : Mapa < Casa atual , Lista de Casas que são capturas obrigatórias >
    Descrição : Este método percorre o tabuleiro e verifica se há capturas obrigatórias para o jogador atual.
    */

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
        // Inicia os jogadores
        // Jogador 1 (Brancas) e Jogador 2 (Pretas)
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
                    // Se chegamos aqui, o movimento selecionado é uma captura obrigatória.
                    // O método moverPeca irá validar novamente, o que é aceitável.
                }

                // A lógica principal de movimentar a peça, determinar se é captura ou movimento simples,
                // e então chamar tabuleiro.moverPeca permanece.
                // Porém, se existirem capturas obrigatórias, um movimento simples deve ser proibido.

                boolean foiCaptura = false; 

                // Verifica se o movimento é uma captura com base na distância
                // Esta verificação agora é mais uma validação geral; a captura obrigatória já foi validada acima.
                int deltaLinhaAbs = Math.abs(linhaOrigem - linhaDestino);
                int deltaColunaAbs = Math.abs(colunaOrigem - colunaDestino);

                if (!mandatoryCapturesMap.isEmpty() && !(deltaLinhaAbs > 1 && deltaColunaAbs > 1 && deltaLinhaAbs == deltaColunaAbs) ) {
                     // Se existem capturas obrigatórias, apenas movimentos de captura são permitidos.
                     // Um movimento simples (delta == 1) ou qualquer movimento não diagonal/não salto é inválido.
                     throw new MovimentoInvalidoException("Movimento inválido. Uma captura é obrigatória.");
                }

                // Neste ponto, se mandatoryCapturesMap não estava vazio, o jogador escolheu uma das capturas obrigatórias.
                // Se mandatoryCapturesMap estava vazio, qualquer movimento válido é permitido.
                // O método tabuleiro.moverPeca executa e valida o movimento (incluindo se é captura ou simples).

                // Precisamos saber se ocorreu uma captura para tratar sequências de capturas.
                // Uma forma é checar a distância, já que moverPeca não retorna isso.
                // Ou, verificar se uma peça foi realmente capturada. Vamos confiar na distância por enquanto,
                // pois moverPeca lançará exceção se for uma captura inválida.

                tabuleiro.moverPeca(casaOrigem, casaDestino); 

                // Determina se uma captura ocorreu para a lógica de sequência.
                // Isso depende do fato de que moverPeca teria lançado um erro se não fosse um salto válido
                // ou um movimento simples válido.
                if (deltaLinhaAbs > 1) { 
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
                    // Verifica se a sequência de capturas deve continuar apenas se o jogador atual ainda for o mesmo
                    // (o que será verdade se emSequenciaDeCaptura era false antes desta captura)
                    // OU se ele já estava em uma sequência.
                    List<Casa> novasCapturasPossiveis = tabuleiro.getPossiveisCapturas(pecaQueCapturouUltimaPosicao);
                    if (!novasCapturasPossiveis.isEmpty()) {
                        System.out.println("Você realizou uma captura e pode capturar novamente com a mesma peça.");
                        emSequenciaDeCaptura = true;
                        pecaEmSequencia = pecaQueCapturouUltimaPosicao;
                    } else {
                        emSequenciaDeCaptura = false;
                        pecaEmSequencia = null;
                        jogadorAtual = (jogadorAtual == jogador1) ? jogador2 : jogador1;
                    }
                } else {
                    emSequenciaDeCaptura = false;
                    pecaEmSequencia = null;
                    jogadorAtual = (jogadorAtual == jogador1) ? jogador2 : jogador1;
                }

            // Se emSequenciaDeCaptura for verdadeiro neste ponto, o jogador não troca.
            // O loop continua com o mesmo jogador.

            } catch (MovimentoInvalidoException | NumberFormatException e) {
                System.out.println("Erro: " + e.getMessage());
                System.out.println("Tente novamente.");
            }
        }
        scanner.close();
    }
}
