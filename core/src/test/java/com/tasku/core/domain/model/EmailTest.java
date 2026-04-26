package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {

    @Test
    void shouldCrearEmailWithEmailValido() {
        Email email = new Email("user@example.com");
        assertEquals("user@example.com", email.email());
    }

    @Test
    void shouldConvertirEmailToLowercase() {
        Email email = new Email("USER@EXAMPLE.COM");    
        assertEquals("user@example.com", email.email());
    }

    @Test
    void shouldTrimEspaciosFromEmail() {
        Email email = new Email("  user@example.com  ");
        assertEquals("user@example.com", email.email());
    }

    @Test
    void shouldThrowExceptionWhenEmailEsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Email(null));
    }

    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Email("   "));
    }

    @Test
    void shouldThrowExceptionWhenEmailEstáVacio() {
        assertThrows(IllegalArgumentException.class, () -> new Email(""));
    }

    @Test
    void shouldThrowExceptionWhenEmailSinDominio() {
        assertThrows(IllegalArgumentException.class, () -> new Email("user@"));
    }

    @Test
    void shouldThrowExceptionWhenEmailSinParteLocal() {
        assertThrows(IllegalArgumentException.class, () -> new Email("@example.com"));
    }


    @Test
    void shouldNoSerIgualWithEmailDistintos() {
        Email email1 = new Email("user1@example.com");
        Email email2 = new Email("user2@example.com");
        assertNotEquals(email1, email2);
    }
}
