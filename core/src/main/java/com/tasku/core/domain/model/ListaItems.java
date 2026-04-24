package com.tasku.core.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListaItems {
    private List<ElementoChecklist> items;

    // Constructores

    public ListaItems() {
        this.items = new ArrayList<>();
    }

    public ListaItems(List<ElementoChecklist> items) {
        this.items = new ArrayList<>(Objects.requireNonNull(items, "La lista de items no puede ser nula"));
    }

    // Getter

    public List<ElementoChecklist> getItems() {
        return List.copyOf(items);
    }
    
    // Funciones para agregar y eliminar items en la lista de checklist

    void agregarItem(ElementoChecklist item) {
        items.add(Objects.requireNonNull(item, "El item no puede ser nulo"));
    }

    boolean eliminarItem(ElementoChecklist item) {
        return items.remove(Objects.requireNonNull(item, "El item no puede ser nulo"));
    }

    // Función para marcar o desmarcar items de la lista de checklist
    
    void marcarItem(int indice, boolean estaMarcado) {
        if (indice < 0 || indice >= items.size()) {
            throw new IllegalArgumentException("Índice de item fuera de rango");
        }

        ElementoChecklist actual = items.get(indice);
        items.set(indice, new ElementoChecklist(actual.description(), estaMarcado));
    }
}
