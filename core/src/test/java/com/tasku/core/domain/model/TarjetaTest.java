package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainValidationException;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TarjetaTest {

    private final ListaTableroId LIST_ID = new ListaTableroId(java.util.UUID.randomUUID());
    private final String TITLE = "Test Card";
    private final String DESC = "Test Description";

    @Test
    void tarjetaTarea_createNew_validaCampos() {
        assertThatThrownBy(() -> TarjetaTarea.createNew((ListaTableroId) null, TITLE, DESC, Set.of()))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("lista");
        assertThatThrownBy(() -> TarjetaTarea.createNew(LIST_ID, null, DESC, Set.of()))
                .isInstanceOf(DomainValidationException.class).hasMessageContaining("titulo");
        assertThatThrownBy(() -> TarjetaTarea.createNew(LIST_ID, TITLE, null, Set.of()))
                .isInstanceOf(DomainValidationException.class).hasMessageContaining("descripcion");
    }

    @Test
    void tarjetaTarea_createNew_creaValida() {
        TarjetaTarea card = TarjetaTarea.createNew(LIST_ID, TITLE, DESC, Set.of());
        assertThat(card.id()).isNotNull();
        assertThat(card.listId()).isEqualTo(LIST_ID.id());
        assertThat(card.type()).isEqualTo(TipoTarjeta.TAREA);
        assertThat(card.title()).isEqualTo(TITLE);
        assertThat(card.archived()).isFalse();
        assertThat(card.labels()).isEmpty();
    }

    @Test
    void tarjetaChecklist_createNew_conItems() {
        List<ElementoChecklist> items = List.of(
                new ElementoChecklist("Item 1", false),
                new ElementoChecklist("Item 2", true)
        );
        TarjetaChecklist card = TarjetaChecklist.createNew(LIST_ID, TITLE, DESC, Set.of(), items);
        assertThat(card.type()).isEqualTo(TipoTarjeta.CHECKLIST);
        assertThat(card.items()).hasSize(2);
        assertThat(card.items().get(0).description()).isEqualTo("Item 1");
        assertThat(card.items().get(0).completed()).isFalse();
    }

    @Test
    void tarjeta_moveToList_cambiaLista() {
        TarjetaTarea card = TarjetaTarea.createNew(LIST_ID, TITLE, DESC, Set.of());
        ListaTableroId newListId = new ListaTableroId(java.util.UUID.randomUUID());
        card.moveToList(newListId);
        assertThat(card.listIdValue()).isEqualTo(newListId);
        assertThatThrownBy(() -> card.moveToList((ListaTableroId) null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void tarjeta_archive_tarjetaArchivada() {
        TarjetaTarea card = TarjetaTarea.createNew(LIST_ID, TITLE, DESC, Set.of());
        assertThat(card.archived()).isFalse();
        card.archive();
        assertThat(card.archived()).isTrue();
    }

    @Test
    void tarjeta_addLabel_agregaEtiqueta() {
        TarjetaTarea card = TarjetaTarea.createNew(LIST_ID, TITLE, DESC, Set.of());
        EtiquetaTarjeta label = new EtiquetaTarjeta("Bug", "red");
        card.addLabel(label);
        assertThat(card.labels()).hasSize(1);
        assertThatThrownBy(() -> card.addLabel(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void tarjeta_removeLabel_eliminaEtiqueta() {
        TarjetaTarea card = TarjetaTarea.createNew(LIST_ID, TITLE, DESC, Set.of());
        EtiquetaTarjeta label = new EtiquetaTarjeta("Bug", "red");
        card.addLabel(label);
        card.removeLabel(label);
        assertThat(card.labels()).isEmpty();
        assertThatThrownBy(() -> card.removeLabel(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void tarjeta_labels_retornaCopiaInmutable() {
        TarjetaTarea card = TarjetaTarea.createNew(LIST_ID, TITLE, DESC, Set.of());
        card.addLabel(new EtiquetaTarjeta("Test", "blue"));
        assertThatThrownBy(() -> card.labels().add(null)).isInstanceOf(UnsupportedOperationException.class);
    }
}
