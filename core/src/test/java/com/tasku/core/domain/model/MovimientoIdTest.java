package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;


import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MovimientoIdTest {

    @Test
    void constructor_validaIdNoNulo() {
        assertThatThrownBy(() -> new MovimientoId(null))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("no puede ser nulo");
    }

    @Test
    void constructor_conUuid_creaValido() {
        UUID uuid = UUID.randomUUID();
        MovimientoId id = new MovimientoId(uuid);
        assertThat(id.id()).isEqualTo(uuid);
    }

    @Test
    void constructor_sinArgs_generaUuid() {
        MovimientoId id1 = new MovimientoId();
        MovimientoId id2 = new MovimientoId();
        assertThat(id1.id()).isNotNull();
        assertThat(id2.id()).isNotNull();
        assertThat(id1.id()).isNotEqualTo(id2.id());
    }
}
