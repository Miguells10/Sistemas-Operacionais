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

            System.out.println("------ BENCHMARK: 1 THREAD ------");
            testarDesempenho1Thread(tamanho);

            System.out.println("\n------ BENCHMARK: 16 THREADS ------");
            try {
                testarDesempenho16Threads(tamanho);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Execução interrompida: " + e.getMessage());
            }
        }
    }

    private static void testarDesempenho1Thread(int numOperacoes) {
        testarOperacoesArrayList("ArrayList", new ArrayList<>(), numOperacoes);
        testarOperacoesArrayList("Vector", new Vector<>(), numOperacoes);
        testarOperacoesThreadSafeArrayList("ThreadSafeArrayList", new ThreadSafeArrayList<>(), numOperacoes);
    }

    // Usado para ArrayList e Vector (que implementam List)
    private static void testarOperacoesArrayList(String nome, List<Integer> lista, int numOperacoes) {
        Random rand = new Random();

        // Inserção
        long inicio = System.nanoTime();
        for (int i = 0; i < numOperacoes; i++) {
            lista.add(i);
        }
        long duracao = System.nanoTime() - inicio;
        imprimirResultado(nome, "Inserção", numOperacoes, duracao);

        // Busca
        inicio = System.nanoTime();
        for (int i = 0; i < numOperacoes; i++) {
            if (!lista.isEmpty()) {
                // Removida a variável dummy não utilizada
                lista.get(rand.nextInt(lista.size()));
                lista.get(rand.nextInt(lista.size()));
            }
        }

        duracao = System.nanoTime() - inicio;
        imprimirResultado(nome, "Busca", numOperacoes, duracao);

        // Remoção
        inicio = System.nanoTime();
        for (int i = 0; i < numOperacoes; i++) {
            if (!lista.isEmpty()) {
                lista.remove(lista.size() - 1);
            }
        }
        duracao = System.nanoTime() - inicio;
        imprimirResultado(nome, "Remoção", numOperacoes, duracao);
    }

    private static void testarOperacoesThreadSafeArrayList(String nome, ThreadSafeArrayList<Integer> lista, int numOperacoes) {
        Random rand = new Random();

        // Inserção
        long inicio = System.nanoTime();
        for (int i = 0; i < numOperacoes; i++) {
            lista.add(i);
        }
        long duracao = System.nanoTime() - inicio;
        imprimirResultado(nome, "Inserção", numOperacoes, duracao);

        // Busca
        inicio = System.nanoTime();
        for (int i = 0; i < numOperacoes; i++) {
            if (lista.size() > 0) {
                lista.get(rand.nextInt(lista.size()));
            }
        }
        duracao = System.nanoTime() - inicio;
        imprimirResultado(nome, "Busca", numOperacoes, duracao);

        // Remoção
        inicio = System.nanoTime();
        for (int i = 0; i < numOperacoes; i++) {
            if (lista.size() > 0) {
                lista.remove(lista.size() - 1);
            }
        }
        duracao = System.nanoTime() - inicio;
        imprimirResultado(nome, "Remoção", numOperacoes, duracao);
    }

    private static void testarDesempenho16Threads(int numOperacoes) throws InterruptedException {
        testarConcorrenciaVector("Vector", new Vector<>(), numOperacoes);
        testarConcorrenciaThreadSafeArrayList("ThreadSafeArrayList", new ThreadSafeArrayList<>(), numOperacoes);
    }

    // Concorrência para Vector (que é List)
    private static void testarConcorrenciaVector(String nome, List<Integer> listaCompartilhada, int numOperacoes) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        try {
            AtomicInteger contador = new AtomicInteger();
            Random rand = new Random();

            int operacoesPorThread = numOperacoes / NUM_THREADS;
            long inicio = System.nanoTime();

            for (int i = 0; i < NUM_THREADS; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < operacoesPorThread; j++) {
                        int valor = contador.getAndIncrement();
                        listaCompartilhada.add(valor);

                        synchronized (listaCompartilhada) {
                            if (!listaCompartilhada.isEmpty()) {
                                listaCompartilhada.get(rand.nextInt(listaCompartilhada.size()));
                            }

                            if (!listaCompartilhada.isEmpty()) {
                                listaCompartilhada.remove(0);
                            }
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

    // Concorrência para ThreadSafeArrayList (que não é List)
    private static void testarConcorrenciaThreadSafeArrayList(String nome, ThreadSafeArrayList<Integer> listaCompartilhada, int numOperacoes) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        try {
            AtomicInteger contador = new AtomicInteger();
            Random rand = new Random();

            int operacoesPorThread = numOperacoes / NUM_THREADS;
            long inicio = System.nanoTime();

            for (int i = 0; i < NUM_THREADS; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < operacoesPorThread; j++) {
                        int valor = contador.getAndIncrement();
                        listaCompartilhada.add(valor);

                        if (listaCompartilhada.size() > 0) {
                            listaCompartilhada.get(rand.nextInt(listaCompartilhada.size()));
                        }

                        if (listaCompartilhada.size() > 0) {
                            listaCompartilhada.remove(0);
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
        System.out.printf("[%s] %s: %.2f ops/s\n", nome, operacao, opsPorSegundo);
    }
}