package org.example.sistemasoperacionais.database_concurrency;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DatabaseConcurrencyManager {
    private static final int MAX_CONCURRENT_READS = 10;
    private final Semaphore readSemaphore;
    private final ReentrantReadWriteLock rwLock;
    private volatile int activeReaders;

    public DatabaseConcurrencyManager() {
        this.readSemaphore = new Semaphore(MAX_CONCURRENT_READS, true);
        this.rwLock = new ReentrantReadWriteLock();
        this.activeReaders = 0;
    }

    public void startReading() throws InterruptedException {
        // Primeiro tenta obter permissão de leitura do rwLock
        rwLock.readLock().lock();
        try {
            // Depois tenta obter uma das 10 permissões do semáforo
            readSemaphore.acquire();
            synchronized (this) {
                activeReaders++;
                System.out.println(">>> LEITURA: Total de leitores agora é " + activeReaders + " <<<");
            }
        } catch (InterruptedException e) {
            rwLock.readLock().unlock();
            throw e;
        }
    }

    public synchronized void finishReading() {
        activeReaders--;
        System.out.println(">>> LEITURA: Total de leitores agora é " + activeReaders + " <<<");
        readSemaphore.release();
        rwLock.readLock().unlock();
    }

    public void startWriting() throws InterruptedException {
        System.out.println("\n>>> MODO ESCRITA: Tentando iniciar escrita (Leitores ativos: " + activeReaders + ") <<<");
        
        // Bloqueia todas as novas leituras
        rwLock.writeLock().lock();
        System.out.println(">>> MODO ESCRITA: Bloqueando novas leituras <<<");
        
        // Espera até que todas as leituras atuais terminem
        while (activeReaders > 0) {
            System.out.println(">>> MODO ESCRITA: Aguardando " + activeReaders + " leitores terminarem <<<");
            Thread.sleep(100);
        }
        
        System.out.println(">>> MODO ESCRITA: Iniciando operação de escrita <<<");
    }

    public void finishWriting() {
        System.out.println(">>> MODO ESCRITA: Finalizando operação de escrita <<<");
        rwLock.writeLock().unlock();
    }

    public synchronized int getActiveReaders() {
        return activeReaders;
    }
} 