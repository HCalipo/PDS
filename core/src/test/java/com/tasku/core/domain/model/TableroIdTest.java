package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;


import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TableroIdTest {

    @Test
    void constructor_conStringValido() {
        UUID uuid = UUID.randomUUID();
        TableroId id = new TableroId("tasku://tablero/" + uuid);
        assertThat(id.idTablero()).isEqualTo(uuid);
    }

    @Test
    void constructor_lanzaExcepcion_urlNula() {
        assertThatThrownBy(() -> new TableroId((String) null))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("nula");
    }

    @Test
    void constructor_conUuid_creaValido() {
        UUID uuid = UUID.randomUUID();
        TableroId id = new TableroId(uuid);
        assertThat(id.idTablero()).isEqualTo(uuid);
        assertThat(id.url()).startsWith("tasku://tablero/");
    }

    @Test
    void constructor_sinArgs_generaUuid() {
        TableroId id1 = new TableroId();
        TableroId id2 = new TableroId();
        assertThat(id1.idTablero()).isNotNull();
        assertThat(id2.idTablero()).isNotNull();
        assertThat(id1.idTablero()).isNotEqualTo(id2.idTablero());
    }

    @Test
    void prefijo_retornaPrefijoCorrecto() {
        assertThat(TableroId.prefijo()).isEqualTo("tasku://tablero/");
    }
}
