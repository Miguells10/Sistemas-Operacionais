package org.example.sistemasoperacionais.database_concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TesteConcorrencia {
    public static void main(String[] args) {
        ProdutoService service = new ProdutoService();
        ExecutorService executorService = Executors.newFixedThreadPool(13); // 12 leitores + 1 escritor

        try {
            // Inicia o escritor que cria produtos a cada 5 segundos
            executorService.submit(() -> {
                try {
                    int produtoCount = 1;
                    while (!Thread.currentThread().isInterrupted()) {
                        System.out.println("\n=== ESCRITOR: Criando produto #" + produtoCount + " ===");
                        Produto produto = new Produto("Produto " + produtoCount, produtoCount * 10.0);
                        service.criar(produto);
                        System.out.println("=== ESCRITOR: Produto #" + produtoCount + " criado ===\n");
                        produtoCount++;
                        Thread.sleep(5000); // Espera 5 segundos antes de criar o próximo produto
                    }
                } catch (InterruptedException e) {
                    System.out.println("Escritor interrompido.");
                }
            });

            // Inicia os 12 consumidores que consultam o último produto
            for (int i = 1; i <= 12; i++) {
                final int leitorId = i;
                executorService.submit(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            Produto ultimo = service.buscarUltimoProduto();
                            if (ultimo != null) {
                                System.out.println("Leitor " + leitorId + " leu: " + ultimo.getNome() + 
                                                " (Total leitores: " + service.getNumeroLeitoresAtivos() + ")");
                                Thread.sleep(3000); // Bloqueia por 3 segundos antes da próxima leitura
                            }
                        }
                    } catch (InterruptedException e) {
                        System.out.println("Leitor " + leitorId + " interrompido.");
                    }
                });
            }

            // Deixa o teste rodar por 30 segundos
            Thread.sleep(50000);
            
            // Encerra todas as threads
            System.out.println("\n=== Encerrando teste ===");
            executorService.shutdownNow();
            executorService.awaitTermination(5, TimeUnit.SECONDS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 