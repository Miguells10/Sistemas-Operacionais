package org.example.sistemasoperacionais.arrayList_thread_safe;

import java.util.ArrayList;
import java.util.List;

public class ThreadSafeArrayList<E> {
    private final List<E> listaInterna =  new ArrayList<>();



    public synchronized void add(E elemento) {
        listaInterna.add(elemento);
    }

    public synchronized E remove(int index){
        return listaInterna.remove(index);
    }

    public synchronized void clear(){
        listaInterna.clear();
    }

    public synchronized E get(int index) {
        return listaInterna.get(index);
    }

    public synchronized int size() {
        return listaInterna.size();
    }

    public synchronized boolean contains(Object elemento) {
        return listaInterna.contains(elemento);
    }

    public synchronized String toString() {
        return listaInterna.toString();
    }
}
