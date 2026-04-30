package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainValidationException;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ListaTableroTest {

    @Test
    void createNew_conTableroUrl_creaValida() {
        TableroUrl url = TableroUrl.createNew();
        ListaTablero lista = ListaTablero.createNew(url, "TODO", 10);
        assertThat(lista.name()).isEqualTo("TODO");
        assertThat(lista.cardLimit()).isEqualTo(10);
        assertThat(lista.boardUrl()).isNotNull();
    }

    @Test
    void createNew_conStringUrl_creaValida() {
        String url = "tasku://tablero/" + java.util.UUID.randomUUID();
        ListaTablero lista = ListaTablero.createNew(url, "DOING", 20);
        assertThat(lista.name()).isEqualTo("DOING");
        assertThat(lista.cardLimit()).isEqualTo(20);
    }

    @Test
    void constructor_validaCamposNoNulos() {
        ListaTableroId id = new ListaTableroId();
        TableroUrl url = TableroUrl.createNew();
        assertThatThrownBy(() -> new ListaTablero(null, url, "name", 10))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("id");
        assertThatThrownBy(() -> new ListaTablero(id, null, "name", 10))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("url");
    }

    @Test
    void constructor_lanzaExcepcion_cardLimitInvalido() {
        ListaTableroId id = new ListaTableroId();
        TableroUrl url = TableroUrl.createNew();
        assertThatThrownBy(() -> new ListaTablero(id, url, "name", 0))
                .isInstanceOf(DomainValidationException.class).hasMessageContaining("mayor que cero");
    }

    @Test
    void withName_renombraLista() {
        TableroUrl url = TableroUrl.createNew();
        ListaTablero lista = ListaTablero.createNew(url, "OLD", 10);
        ListaTablero renamed = lista.withName("NEW");
        assertThat(renamed.name()).isEqualTo("NEW");
    }

    @Test
    void id_retornaUuidCorrecto() {
        ListaTablero lista = ListaTablero.createNew(TableroUrl.createNew(), "TEST", 10);
        assertThat(lista.id()).isNotNull();
    }
}
