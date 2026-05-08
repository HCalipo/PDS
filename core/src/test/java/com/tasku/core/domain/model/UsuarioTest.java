package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void shouldCreateWithEmailAndDate() {
        Email email = new Email("user@tasku.dev");
        LocalDateTime now = LocalDateTime.now();
        Usuario user = new Usuario(email, now);
        assertEquals("user@tasku.dev", user.email());
        assertEquals(now, user.registrationDate());
    }

    @Test
    void shouldCreateNewWithStringEmail() {
        Usuario user = Usuario.createNew("User@Tasku.dev");
        assertEquals("user@tasku.dev", user.email());
        assertNotNull(user.registrationDate());
    }

    @Test
    void shouldEqualSameEmail() {
        Usuario a = new Usuario(new Email("user@tasku.dev"), LocalDateTime.now());
        Usuario b = new Usuario(new Email("User@Tasku.dev"), LocalDateTime.now().minusDays(1));
        assertEquals(a, b);
    }

    @Test
    void shouldThrowWhenEmailIsNull() {
        assertThrows(NullPointerException.class,
                () -> new Usuario(null, LocalDateTime.now()));
    }

    @Test
    void shouldThrowWhenRegistrationDateIsNull() {
        assertThrows(NullPointerException.class,
                () -> new Usuario(new Email("user@tasku.dev"), null));
    }

    @Test
    void shouldNotEqualDifferentEmail() {
        Usuario a = new Usuario(new Email("user@tasku.dev"), LocalDateTime.now());
        Usuario b = new Usuario(new Email("other@tasku.dev"), LocalDateTime.now());
        assertNotEquals(a, b);
    }

    @Test
    void shouldNormalizeEmailStatic() {
        assertEquals("user@tasku.dev", Usuario.normalizeEmail("User@TASKU.dev"));
    }

    @Test
    void shouldReturnEmailValueObject() {
        Email email = new Email("user@tasku.dev");
        Usuario user = new Usuario(email, LocalDateTime.now());
        assertEquals(email, user.emailValue());
    }
}
