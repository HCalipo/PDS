package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ListaItemsTest {

    @Test
    void constructor_sinArgs_creaVacia() {
        ListaItems lista = new ListaItems();
        assertThat(lista.getItems()).isEmpty();
    }

    @Test
    void constructor_conLista_creaItems() {
        List<ElementoChecklist> items = List.of(
                new ElementoChecklist("Item 1", false),
                new ElementoChecklist("Item 2", true)
        );
        ListaItems lista = new ListaItems(items);
        assertThat(lista.getItems()).hasSize(2);
    }

    @Test
    void constructor_lanzaExcepcion_itemsNulo() {
        assertThatThrownBy(() -> new ListaItems(null))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("no puede ser nula");
    }

    @Test
    void agregarItem_anadeElemento() {
        ListaItems lista = new ListaItems();
        ElementoChecklist item = new ElementoChecklist("New Item", false);
        lista.agregarItem(item);
        assertThat(lista.getItems()).hasSize(1);
    }

    @Test
    void eliminarItem_borraElemento() {
        ElementoChecklist item = new ElementoChecklist("Item", false);
        ListaItems lista = new ListaItems(List.of(item));
        boolean removed = lista.eliminarItem(item);
        assertThat(removed).isTrue();
        assertThat(lista.getItems()).isEmpty();
    }

    @Test
    void marcarItem_marcaElemento() {
        ListaItems lista = new ListaItems(List.of(new ElementoChecklist("Item", false)));
        lista.marcarItem(0, true);
        assertThat(lista.getItems().get(0).completed()).isTrue();
    }

    @Test
    void marcarItem_lanzaExcepcion_indiceInvalido() {
        ListaItems lista = new ListaItems();
        assertThatThrownBy(() -> lista.marcarItem(0, true))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("fuera de rango");
    }

    @Test
    void getItems_retornaCopiaInmutable() {
        ListaItems lista = new ListaItems();
        assertThatThrownBy(() -> lista.getItems().add(null))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
