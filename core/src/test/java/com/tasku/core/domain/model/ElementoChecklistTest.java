package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ElementoChecklistTest {

    @Test
    void constructor_creaElementoValido() {
        ElementoChecklist item = new ElementoChecklist("Task 1", false);
        assertThat(item.description()).isEqualTo("Task 1");
        assertThat(item.completed()).isFalse();
    }

    @Test
    void constructor_conCompletadoTrue() {
        ElementoChecklist item = new ElementoChecklist("Task 1", true);
        assertThat(item.completed()).isTrue();
    }
}
