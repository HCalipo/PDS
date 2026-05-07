package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainValidationException;
import org.junit.jupiter.api.Test;


import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TableroUrlTest {

    @Test
    void createNew_generaUrlValida() {
        TableroUrl url = TableroUrl.createNew();
        assertThat(url.value()).startsWith("tasku://tablero/");
        assertThat(url.value()).hasSize("tasku://tablero/".length() + 36);
    }

    @Test
    void constructor_validaFormatoUrl() {
        String validUrl = "tasku://tablero/" + UUID.randomUUID();
        TableroUrl url = new TableroUrl(validUrl);
        assertThat(url.value()).isEqualTo(validUrl);

        assertThatThrownBy(() -> new TableroUrl(null))
                .isInstanceOf(DomainValidationException.class).hasMessageContaining("nula ni vacia");
        assertThatThrownBy(() -> new TableroUrl("invalid-url"))
                .isInstanceOf(DomainValidationException.class).hasMessageContaining("UUID valido");
    }
}
