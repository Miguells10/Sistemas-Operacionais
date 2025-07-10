package org.example.sistemasoperacionais.arrayList_thread_safe;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Benchmark {

    private static final int[] TAMANHOS = {10_000, 100_000, 500_000, 1_000_000};
    private static final int NUM_THREADS = 16;

    public static void main(String[] args) {
        for (int tamanho : TAMANHOS) {
            System.out.println("\n===== TAMANHO: " + tamanho + " =====");

            System.out.println("------ TESTE: 1 THREAD ------");
            testarDesempenho1Thread(tamanho);

            System.out.println("\n------ TESTE: 16 THREADS ------");
            try {
                testarDesempenho16Threads(tamanho);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Execução interrompida: " + e.getMessage());
            }
        }
    }

    // Testes com 1 thread: ArrayList vs ThreadSafeArrayList
    private static void testarDesempenho1Thread(int numOperacoes) {
        testarOperacoes("ArrayList", new ArrayList<>(), numOperacoes);
        testarOperacoesThreadSafe("ThreadSafeArrayList", new ThreadSafeArrayList<>(), numOperacoes);
    }

    private static void testarOperacoes(String nome, List<Integer> lista, int numOperacoes) {
        Random rand = new Random();

        // Inserção
        long inicio = System.nanoTime();
        for (int i = 0; i < numOperacoes; i++) {
            lista.add(rand.nextInt());
        }
        long duracaoInsercao = System.nanoTime() - inicio;
        imprimirResultado(nome, "Inserção", numOperacoes, duracaoInsercao);

        // Busca
        inicio = System.nanoTime();
        for (int i = 0; i < numOperacoes; i++) {
            if (!lista.isEmpty()) {
                lista.get(rand.nextInt(lista.size()));
            }
        }
        long duracaoBusca = System.nanoTime() - inicio;
        imprimirResultado(nome, "Busca", numOperacoes, duracaoBusca);

        // Remoção
        inicio = System.nanoTime();
        for (int i = 0; i < numOperacoes; i++) {
            if (!lista.isEmpty()) {
                lista.remove(rand.nextInt(lista.size()));
            }
        }
        long duracaoRemocao = System.nanoTime() - inicio;
        imprimirResultado(nome, "Remoção", numOperacoes, duracaoRemocao);
    }

    private static void testarOperacoesThreadSafe(String nome, ThreadSafeArrayList<Integer> lista, int numOperacoes) {
        Random rand = new Random();

        // Inserção
        long inicio = System.nanoTime();
        for (int i = 0; i < numOperacoes; i++) {
            lista.add(rand.nextInt());
        }
        long duracaoInsercao = System.nanoTime() - inicio;
        imprimirResultado(nome, "Inserção", numOperacoes, duracaoInsercao);

        // Busca
        inicio = System.nanoTime();
        for (int i = 0; i < numOperacoes; i++) {
            if (lista.size() > 0) {
                lista.get(rand.nextInt(lista.size()));
            }
        }
        long duracaoBusca = System.nanoTime() - inicio;
        imprimirResultado(nome, "Busca", numOperacoes, duracaoBusca);

        // Remoção
        inicio = System.nanoTime();
        for (int i = 0; i < numOperacoes; i++) {
            if (lista.size() > 0) {
                lista.remove(rand.nextInt(lista.size()));
            }
        }
        long duracaoRemocao = System.nanoTime() - inicio;
        imprimirResultado(nome, "Remoção", numOperacoes, duracaoRemocao);
    }

    // Testes com 16 threads: Vector e ThreadSafeArrayList
    private static void testarDesempenho16Threads(int numOperacoes) throws InterruptedException {
        testarConcorrencia("Vector", new Vector<>(), numOperacoes);
        testarConcorrenciaThreadSafe("ThreadSafeArrayList", new ThreadSafeArrayList<>(), numOperacoes);
    }

    private static void testarConcorrencia(String nome, List<Integer> listaCompartilhada, int numOperacoes) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        try {
            Random rand = new Random();
            int operacoesPorThread = numOperacoes / NUM_THREADS;

            long inicio = System.nanoTime();

            for (int i = 0; i < NUM_THREADS; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < operacoesPorThread; j++) {
                        int valor = rand.nextInt();
                        listaCompartilhada.add(valor);

                        if (!listaCompartilhada.isEmpty()) {
                            try {
                                listaCompartilhada.get(rand.nextInt(listaCompartilhada.size()));
                            } catch (IndexOutOfBoundsException ignored) {}
                        }

                        if (!listaCompartilhada.isEmpty()) {
                            try {
                                listaCompartilhada.remove(rand.nextInt(listaCompartilhada.size()));
                            } catch (IndexOutOfBoundsException ignored) {}
                        }
                    }
                });
            }

            executor.shutdown();
            if (!executor.awaitTermination(2, TimeUnit.MINUTES)) {
                System.err.println("Tempo limite atingido para execução das threads.");
            }

            long duracao = System.nanoTime() - inicio;
            int totalOperacoes = numOperacoes * 3;
            double opsPorSegundo = totalOperacoes / (duracao / 1_000_000_000.0);

            System.out.printf("[%s] 16 Threads - Total de operações: %d - Tempo: %.2fs - Média: %.2f ops/s\n",
                    nome, totalOperacoes, duracao / 1_000_000_000.0, opsPorSegundo);

        } finally {
            executor.shutdownNow();
        }
    }

    private static void testarConcorrenciaThreadSafe(String nome, ThreadSafeArrayList<Integer> lista, int numOperacoes) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        try {
            Random rand = new Random();
            int operacoesPorThread = numOperacoes / NUM_THREADS;

            long inicio = System.nanoTime();

            for (int i = 0; i < NUM_THREADS; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < operacoesPorThread; j++) {
                        int valor = rand.nextInt();
                        lista.add(valor);

                        if (lista.size() > 0) {
                            try {
                                lista.get(rand.nextInt(lista.size()));
                            } catch (IndexOutOfBoundsException ignored) {}
                        }

                        if (lista.size() > 0) {
                            try {
                                lista.remove(rand.nextInt(lista.size()));
                            } catch (IndexOutOfBoundsException ignored) {}
                        }
                    }
                });
            }

            executor.shutdown();
            if (!executor.awaitTermination(2, TimeUnit.MINUTES)) {
                System.err.println("Tempo limite atingido para execução das threads.");
            }

            long duracao = System.nanoTime() - inicio;
            int totalOperacoes = numOperacoes * 3;
            double opsPorSegundo = totalOperacoes / (duracao / 1_000_000_000.0);

            System.out.printf("[%s] 16 Threads - Total de operações: %d - Tempo: %.2fs - Média: %.2f ops/s\n",
                    nome, totalOperacoes, duracao / 1_000_000_000.0, opsPorSegundo);

        } finally {
            executor.shutdownNow();
        }
    }

    private static void imprimirResultado(String nome, String operacao, int totalOps, long duracaoNano) {
        double duracaoSeg = duracaoNano / 1_000_000_000.0;
        double opsPorSegundo = totalOps / duracaoSeg;
        System.out.printf("[%s] %s: %.2f ops/s (Tempo: %.2fs)\n", nome, operacao, opsPorSegundo, duracaoSeg);
    }
}
