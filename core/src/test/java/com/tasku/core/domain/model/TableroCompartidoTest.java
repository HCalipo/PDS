package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TableroCompartidoTest {

    @Test
    void constructor_validaCamposNoNulos() {
        TableroUrl url = TableroUrl.createNew();
        Email email = new Email("test@test.com");
        // Primer constructor: (Long id, TableroUrl boardUrl, Email email, RolComparticion role)
        assertThatThrownBy(() -> new TableroCompartido(1L, null, email, RolComparticion.VIEWER))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("url");
        assertThatThrownBy(() -> new TableroCompartido(1L, url, null, RolComparticion.VIEWER))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("email");
        assertThatThrownBy(() -> new TableroCompartido(1L, url, email, null))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("rol");
        // Segundo constructor: (Long id, String boardUrl, String email, RolComparticion role)
        // TableroUrl constructor lanza DomainValidationException para null
        assertThatThrownBy(() -> new TableroCompartido(1L, (String) null, "test@test.com", RolComparticion.VIEWER))
                .isInstanceOf(com.tasku.core.domain.board.exception.DomainValidationException.class).hasMessageContaining("nula");
    }

    @Test
    void constructor_conStringUrl_creaValida() {
        TableroCompartido compartido = new TableroCompartido(1L, "tasku://tablero/" + java.util.UUID.randomUUID(), "test@test.com", RolComparticion.EDITOR);
        assertThat(compartido.email()).isEqualTo("test@test.com");
        assertThat(compartido.role()).isEqualTo(RolComparticion.EDITOR);
    }

    @Test
    void getters_retornanValoresCorrectos() {
        TableroUrl url = TableroUrl.createNew();
        TableroCompartido compartido = new TableroCompartido(1L, url, new Email("test@test.com"), RolComparticion.VIEWER);
        assertThat(compartido.id()).isEqualTo(1L);
        assertThat(compartido.boardUrlValue()).isEqualTo(url);
        assertThat(compartido.emailValue()).isNotNull();
    }
}
