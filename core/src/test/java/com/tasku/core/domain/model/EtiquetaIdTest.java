package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EtiquetaIdTest {

    @Test
    void constructor_validaIdNoNulo() {
        assertThatThrownBy(() -> new EtiquetaId(null))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("no puede ser nulo");
    }

    @Test
    void constructor_conUuid_creaValido() {
        UUID uuid = UUID.randomUUID();
        EtiquetaId id = new EtiquetaId(uuid);
        assertThat(id.id()).isEqualTo(uuid);
    }

    @Test
    void constructor_sinArgs_generaUuid() {
        EtiquetaId id1 = new EtiquetaId();
        EtiquetaId id2 = new EtiquetaId();
        assertThat(id1.id()).isNotNull();
        assertThat(id2.id()).isNotNull();
        assertThat(id1.id()).isNotEqualTo(id2.id());
    }
}
