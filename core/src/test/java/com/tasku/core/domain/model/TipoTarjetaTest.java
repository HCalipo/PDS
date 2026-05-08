package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class TipoTarjetaTest {
    @Test
    void tarea_esTipoValido() {
        assertThat(TipoTarjeta.TAREA).isNotNull();
        assertThat(TipoTarjeta.TAREA.name()).isEqualTo("TAREA");
    }

    @Test
    void checklist_esTipoValido() {
        assertThat(TipoTarjeta.CHECKLIST).isNotNull();
        assertThat(TipoTarjeta.CHECKLIST.name()).isEqualTo("CHECKLIST");
    }
}
