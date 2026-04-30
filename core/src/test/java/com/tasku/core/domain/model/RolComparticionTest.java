package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

class RolComparticionTest {

    @Test
    void viewer_esRolValido() {
        assertThat(RolComparticion.VIEWER).isNotNull();
        assertThat(RolComparticion.VIEWER.name()).isEqualTo("VIEWER");
    }

    @Test
    void editor_esRolValido() {
        assertThat(RolComparticion.EDITOR).isNotNull();
        assertThat(RolComparticion.EDITOR.name()).isEqualTo("EDITOR");
    }

    @Test
    void values_retornaTodosLosRoles() {
        RolComparticion[] values = RolComparticion.values();
        assertThat(values).hasSize(2);
        assertThat(values).contains(RolComparticion.VIEWER, RolComparticion.EDITOR);
    }
}
