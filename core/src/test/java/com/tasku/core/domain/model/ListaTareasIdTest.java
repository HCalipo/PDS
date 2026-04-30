package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;


import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ListaTareasIdTest {

    @Test
    void constructor_validaCamposNoNulos() {
        TableroId url = new TableroId();
        assertThatThrownBy(() -> new ListaTareasId(null, UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("URL no puede ser nula");
        assertThatThrownBy(() -> new ListaTareasId(url, null))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("id no puede ser nulo");
    }

    @Test
    void constructor_conTableroId_creaValido() {
        TableroId url = new TableroId();
        ListaTareasId id = new ListaTareasId(url);
        assertThat(id.url()).isEqualTo(url);
        assertThat(id.id()).isNotNull();
    }

    @Test
    void constructor_completo_creaValido() {
        TableroId url = new TableroId();
        UUID uuid = UUID.randomUUID();
        ListaTareasId id = new ListaTareasId(url, uuid);
        assertThat(id.url()).isEqualTo(url);
        assertThat(id.id()).isEqualTo(uuid);
    }
}
