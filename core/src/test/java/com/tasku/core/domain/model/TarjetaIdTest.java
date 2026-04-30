package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainValidationException;
import org.junit.jupiter.api.Test;


import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TarjetaIdTest {

    @Test
    void constructor_validaIdNoNulo() {
        assertThatThrownBy(() -> new TarjetaId(null))
                .isInstanceOf(DomainValidationException.class).hasMessageContaining("no puede ser nulo");
    }

    @Test
    void constructor_conUuid_creaValido() {
        UUID uuid = UUID.randomUUID();
        TarjetaId id = new TarjetaId(uuid);
        assertThat(id.id()).isEqualTo(uuid);
    }

    @Test
    void constructor_sinArgs_generaUuid() {
        TarjetaId id1 = new TarjetaId();
        TarjetaId id2 = new TarjetaId();
        assertThat(id1.id()).isNotNull();
        assertThat(id2.id()).isNotNull();
        assertThat(id1.id()).isNotEqualTo(id2.id());
    }
}
