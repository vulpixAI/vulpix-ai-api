package com.vulpix.api.utils.listaobj;

import java.util.ArrayList;
import java.util.List;

public class ListaObj<T> {
    private List<T> lista;

    public ListaObj() {
        this.lista = new ArrayList<>();
    }

    public void adicionar(T obj) {
        lista.add(obj);
    }

    public T obter(int indice) {
        if (indice >= 0 && indice < lista.size()) {
            return lista.get(indice);
        } else {
            throw new IndexOutOfBoundsException("Índice inválido.");
        }
    }

    public int tamanho() {
        return lista.size();
    }

    public void exibir() {
        for (T obj : lista) {
            System.out.println(obj.toString());
        }
    }
}

