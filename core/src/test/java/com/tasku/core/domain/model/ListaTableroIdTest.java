package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainValidationException;
import org.junit.jupiter.api.Test;


import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ListaTableroIdTest {

    @Test
    void constructor_validaIdNoNulo() {
        assertThatThrownBy(() -> new ListaTableroId(null))
                .isInstanceOf(DomainValidationException.class).hasMessageContaining("no puede ser nulo");
    }

    @Test
    void constructor_conUuid_creaValido() {
        UUID uuid = UUID.randomUUID();
        ListaTableroId id = new ListaTableroId(uuid);
        assertThat(id.id()).isEqualTo(uuid);
    }

    @Test
    void constructor_sinArgs_generaUuid() {
        ListaTableroId id1 = new ListaTableroId();
        ListaTableroId id2 = new ListaTableroId();
        assertThat(id1.id()).isNotNull();
        assertThat(id2.id()).isNotNull();
        assertThat(id1.id()).isNotEqualTo(id2.id());
    }
}
