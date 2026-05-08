package com.tasku.core.domain.model;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

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
    }}
