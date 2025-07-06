package org.example.sistemasoperacionais.arrayList_thread_safe;

import java.util.ArrayList;
import java.util.List;

public class ProgramThreadSafeArrayList { //testar todas as funcoes da ThreadSafeArrayList

    public static void main(String[] args) throws InterruptedException {

        System.out.println("INICIANDO TESTES PARA A ThreadSafeArrayList");

        testaFuncionalidadeBasica();
        testaEstresseDeConcorrencia();

        System.out.println("TODOS OS TESTES FORAM CONCLUÍDOS!");
    }

    public static void testaFuncionalidadeBasica(){ //add, get, size, remove, contains, clear
        System.out.println("TESTE 1: Funcionalidades Básicas");
        ThreadSafeArrayList<String> lista = new ThreadSafeArrayList<>();

        System.out.println("Testando add e size");
        lista.add("Elemento 1");
        lista.add("Elemento 2");
        lista.add("Elemento 3");

        if (lista.size() != 3) {
            System.err.println("O tamanho da lista deveria ser 3, mas é " + lista.size());
            return;
        }
        System.out.println("Itens adicionados e tamanho correto");

        System.out.println("Testando get");
        if (!"Elemento 2".equals(lista.get(1))) {
            System.err.println("get(1) deveria retornar 'Elemento 2'.");
            return;
        }
        System.out.println("get(1) retornou o valor esperado");

        System.out.println("Testando contains()");
        if (!lista.contains("Elemento 1") || lista.contains("Elemento 4")) {
            System.err.println("Lógica de contains() está incorreta");
            return;
        }
        System.out.println("contains() funciona corretamente");

        System.out.println("Testando remove()");
        String itemRemovido = lista.remove(0); // Remove "Elemento 1"
        if (!"Elemento 1".equals(itemRemovido) || lista.size() != 2 || lista.contains("Elemento 1")) {
            System.err.println("Erro no remove()");
            return;
        }
        System.out.println("remove() funcionou. Lista atual: " + lista.toString());

        System.out.println("Testando clear()");
        lista.clear();
        if (lista.size() != 0) {
            System.err.println("clear() não limpou a lista");
            return;
        }
        System.out.println("clear() funcionou. Lista está vazia: " + lista.toString());

        System.out.println("TESTE 1 CONCLUÍDO COM SUCESSO!");
    }
    private static volatile boolean adicaoConcluida = false;

    public static void testaEstresseDeConcorrencia() throws InterruptedException{
        System.out.println("TESTE 2: Estresse de Concorrência");

        ThreadSafeArrayList<Integer> lista = new ThreadSafeArrayList<>();

        List<Thread> threads = new ArrayList<>();

        int numThreads = 10;
        int numElementosPorThread = 2000;
        int totalElementos = numThreads * numElementosPorThread;

        System.out.println( numThreads + " threads adicionandos " + numElementosPorThread + " itens cada.");


        //threads que vão adicionar itens
        for (int i = 0; i < numThreads; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < numElementosPorThread; j++) {
                    lista.add(j);
                }
            });
            threads.add(t);
        }

        Thread threadLeitora = new Thread(() -> {
            int leiturasBemSucedidas = 0;
            while (!adicaoConcluida) {
                if (lista.size() > 0) {
                    // Se a lista não fosse segura, esta linha poderia causar um erro
                    // ao tentar ler um dado enquanto a lista está sendo modificada.
                    lista.get(0);
                    leiturasBemSucedidas++;
                }
            }
            System.out.println("thread leitora finalizada com " + leiturasBemSucedidas + " leituras.");

        });
        System.out.println("Iniciando todas as threads...");
        threadLeitora.start();
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
        adicaoConcluida = true;
        threadLeitora.join();

        System.out.println("Verificação final");
        if (lista.size() == totalElementos) {
            System.out.println("Tamanho final da lista: " + lista.size() + ". Nenhuma escrita foi perdida");
        } else {
            System.err.println("Tamanho final é " + lista.size() + ", mas o esperado era " + totalElementos);
        }

        System.out.println("SUCESSO no Teste de Estresse de Concorrência!");
    }
}
