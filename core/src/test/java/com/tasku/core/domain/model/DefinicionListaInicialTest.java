package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefinicionListaInicialTest {

    @Test
    void constructor_creaDefinicionValida() {
        DefinicionListaInicial def = new DefinicionListaInicial("TODO", 10);
        assertThat(def.name()).isEqualTo("TODO");
        assertThat(def.cardLimit()).isEqualTo(10);
    }

    @Test
    void name_retornaNombreCorrecto() {
        DefinicionListaInicial def = new DefinicionListaInicial("DOING", 20);
        assertThat(def.name()).isEqualTo("DOING");
    }

    @Test
    void cardLimit_retornaLimiteCorrecto() {
        DefinicionListaInicial def = new DefinicionListaInicial("DONE", 30);
        assertThat(def.cardLimit()).isEqualTo(30);
    }
}
