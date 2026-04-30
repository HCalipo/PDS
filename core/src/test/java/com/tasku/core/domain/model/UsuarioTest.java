package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;


import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioTest {

    @Test
    void constructor_validaCamposNoNulos() {
        Email email = new Email("test@test.com");
        LocalDateTime now = LocalDateTime.now();
        assertThatThrownBy(() -> new Usuario(null, now))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("email");
        assertThatThrownBy(() -> new Usuario(email, null))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("fecha");
    }

    @Test
    void createNew_conString_creaValido() {
        Usuario usuario = Usuario.createNew("test@test.com");
        assertThat(usuario.email()).isEqualTo("test@test.com");
        assertThat(usuario.registrationDate()).isNotNull();
    }

    @Test
    void createNew_conEmail_creaValido() {
        Email email = new Email("test@test.com");
        Usuario usuario = Usuario.createNew(email);
        assertThat(usuario.emailValue()).isEqualTo(email);
    }

    @Test
    void normalizeEmail_normaliza() {
        String normalized = Usuario.normalizeEmail("  TEST@TEST.COM  ");
        assertThat(normalized).isEqualTo("test@test.com");
    }

    @Test
    void equals_comparaPorEmail() {
        Usuario u1 = Usuario.createNew("test@test.com");
        Usuario u2 = Usuario.createNew(new Email("test@test.com"));
        Usuario u3 = Usuario.createNew("other@test.com");
        assertThat(u1).isEqualTo(u2);
        assertThat(u1).isNotEqualTo(u3);
    }
}
