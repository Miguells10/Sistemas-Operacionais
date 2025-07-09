package org.example.sistemasoperacionais.barbeiroDorminhoco;

import lombok.Data;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

@Data
public class Barbeiro implements Runnable{

    private final int id;
    private final BlockingQueue<Cliente> fila;

    public Barbeiro(int id, BlockingQueue<Cliente> fila) {
        this.id = id;
        this.fila = fila;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Cliente cliente = fila.take(); // espera um cliente
                System.out.println("Barbeiro " + id + " cortando cabelo do Cliente " + cliente.getId());
                Thread.sleep(ThreadLocalRandom.current().nextInt(5000, 10001));
                System.out.println("Barbeiro " + id + " terminou o Cliente " + cliente.getId());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
