package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoTableroTest {

    @Test
    void active_esEstadoValido() {
        assertThat(EstadoTablero.ACTIVE).isNotNull();
        assertThat(EstadoTablero.ACTIVE.name()).isEqualTo("ACTIVE");
    }

    @Test
    void blocked_esEstadoValido() {
        assertThat(EstadoTablero.BLOCKED).isNotNull();
        assertThat(EstadoTablero.BLOCKED.name()).isEqualTo("BLOCKED");
    }

    @Test
    void values_retornaTodosLosEstados() {
        EstadoTablero[] values = EstadoTablero.values();
        assertThat(values).hasSize(2);
        assertThat(values).contains(EstadoTablero.ACTIVE, EstadoTablero.BLOCKED);
    }
}
