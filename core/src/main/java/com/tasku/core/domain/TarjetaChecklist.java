package com.tasku.core.domain;

import java.util.Objects;

public class TarjetaChecklist extends Tarjeta {
    private ListaItems listaItems;

    // Constructores 

    public TarjetaChecklist(TarjetaId id, String titulo, String descripcion, ListaItems listaItems) {
        super(id, titulo, descripcion);
        this.listaItems = Objects.requireNonNull(listaItems, "La lista de items no puede ser nula");
    }

    public TarjetaChecklist(String titulo, String descripcion) {
        this(new TarjetaId(), titulo, descripcion, new ListaItems());
    }

    // Getter
    public ListaItems getListaItems() {
        return listaItems;
    }

    // Función para agregar items a la lista de checklist
    void agregarItemChecklist(String descripcion) {
        listaItems.agregarItem(new ElementoChecklist(descripcion, false));
    }

    // Función para marcar o desmarcar items de la lista de checklist
    void marcarItemChecklist(int indice, boolean marcado) {
        listaItems.marcarItem(indice, marcado);
    }
}
