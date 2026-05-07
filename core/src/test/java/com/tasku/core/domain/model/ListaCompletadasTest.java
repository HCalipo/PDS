package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ListaCompletadasTest {

    @Test
    void constructor_conTableroId_creaValida() {
        TableroId url = new TableroId();
        ListaCompletadas lista = new ListaCompletadas(url);
        assertThat(lista.getUrl()).isEqualTo(url);
        assertThat(lista.getId()).isNotNull();
        assertThat(lista.getTarjetas()).isEmpty();
    }

    @Test
    void constructor_validaCamposNoNulos() {
        TableroId url = new TableroId();
        ListaCompletadasId id = new ListaCompletadasId(url);
        assertThatThrownBy(() -> new ListaCompletadas(null, id, List.of()))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("URL");
        assertThatThrownBy(() -> new ListaCompletadas(url, null, List.of()))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("id");
    }

    @Test
    void anadirAcompletadas_archivaYAnade() {
        TableroId url = new TableroId();
        ListaCompletadas lista = new ListaCompletadas(url);
        Tarjeta tarjeta = TarjetaTarea.createNew(new ListaTableroId(), "Title", "Desc", null);
        lista.anadirAcompletadas(tarjeta);
        assertThat(tarjeta.archived()).isTrue();
        assertThat(lista.getTarjetas()).hasSize(1);
    }

    @Test
    void eliminarDeCompletadas_borraTarjeta() {
        TableroId url = new TableroId();
        ListaCompletadas lista = new ListaCompletadas(url);
        Tarjeta tarjeta = TarjetaTarea.createNew(new ListaTableroId(), "Title", "Desc", null);
        lista.anadirAcompletadas(tarjeta);
        lista.eliminarDeCompletadas(tarjeta);
        assertThat(lista.getTarjetas()).isEmpty();
    }

    @Test
    void eliminarDeCompletadas_lanzaExcepcion_noPertenece() {
        TableroId url = new TableroId();
        ListaCompletadas lista = new ListaCompletadas(url);
        Tarjeta tarjeta = TarjetaTarea.createNew(new ListaTableroId(), "Title", "Desc", null);
        assertThatThrownBy(() -> lista.eliminarDeCompletadas(tarjeta))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("no pertenece");
    }

    @Test
    void equals_comparaPorId() {
        TableroId url = new TableroId();
        ListaCompletadasId sameId = new ListaCompletadasId(url);
        ListaCompletadas l1 = new ListaCompletadas(url, sameId, List.of());
        ListaCompletadas l2 = new ListaCompletadas(url, sameId, List.of());
        assertThat(l1).isEqualTo(l2);
        ListaCompletadas l3 = new ListaCompletadas(url);
        assertThat(l1).isNotEqualTo(l3);
    }

    @Test
    void getTarjetas_retornaCopiaInmutable() {
        TableroId url = new TableroId();
        ListaCompletadas lista = new ListaCompletadas(url);
        assertThatThrownBy(() -> lista.getTarjetas().add(null))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
