package org.example.sistemasoperacionais.barbeiroDorminhoco;

import java.util.concurrent.*;

public class Salao {
    private final BlockingQueue<Cliente> filaClientes;
    private final ExecutorService barbeiros;

    public Salao(int capacidadeFila, int numBarbeiros) {
        this.filaClientes = new ArrayBlockingQueue<>(capacidadeFila);
        this.barbeiros = Executors.newFixedThreadPool(numBarbeiros);
    }

    public void abrirSalao() {
        for (int i = 0; i < 2; i++) {
            barbeiros.execute(new Barbeiro(i + 1, filaClientes));
        }

        new Thread(() -> {
            int id = 1;
            while (true){
                Cliente cliente = new Cliente(id++);
                boolean clienteEntrou = filaClientes.offer(cliente);

                if (clienteEntrou){
                    System.out.println("Cliente " + cliente.getId() + " entrou na fila");
                }
                else
                    System.out.println("Cliente " + cliente.getId() + " meteu o pé");
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(4000, 6001));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();

    }
}
