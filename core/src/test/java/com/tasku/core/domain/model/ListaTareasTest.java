package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ListaTareasTest {

    @Test
    void constructor_conTableroId_creaValida() {
        TableroId url = new TableroId();
        ListaTareas lista = new ListaTareas(url);
        assertThat(lista.getUrl()).isEqualTo(url);
        assertThat(lista.getId()).isNotNull();
        assertThat(lista.getTarjetas()).isEmpty();
    }

    @Test
    void constructor_validaCamposNoNulos() {
        TableroId url = new TableroId();
        ListaTareasId id = new ListaTareasId(url);
        assertThatThrownBy(() -> new ListaTareas(null, id, List.of()))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("URL no puede ser nula");
        assertThatThrownBy(() -> new ListaTareas(url, null, List.of()))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("id de la lista de tareas no puede ser nulo");
    }

    @Test
    void agregarTarjeta_anadeTarjeta() {
        TableroId url = new TableroId();
        ListaTareas lista = new ListaTareas(url);
        Tarjeta tarjeta = TarjetaTarea.createNew(new ListaTableroId(), "Title", "Desc", null);
        lista.agregarTarjeta(tarjeta);
        assertThat(lista.getTarjetas()).hasSize(1);
    }

    @Test
    void quitarTarjeta_eliminaTarjeta() {
        TableroId url = new TableroId();
        ListaTareas lista = new ListaTareas(url);
        Tarjeta tarjeta = TarjetaTarea.createNew(new ListaTableroId(), "Title", "Desc", null);
        lista.agregarTarjeta(tarjeta);
        boolean removed = lista.quitarTarjeta(tarjeta);
        assertThat(removed).isTrue();
        assertThat(lista.getTarjetas()).isEmpty();
    }

    @Test
    void getTarjetas_retornaCopiaInmutable() {
        TableroId url = new TableroId();
        ListaTareas lista = new ListaTareas(url);
        assertThatThrownBy(() -> lista.getTarjetas().add(null))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
