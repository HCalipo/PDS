package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void shouldCreateWithValidEmail() {
        Email email = new Email("test@tasku.dev");
        assertEquals("test@tasku.dev", email.email());
    }

    @Test
    void shouldThrowWhenEmailHasNoSymbol() {
        assertThrows(IllegalArgumentException.class, () -> new Email("invalid-email"));
    }

}
