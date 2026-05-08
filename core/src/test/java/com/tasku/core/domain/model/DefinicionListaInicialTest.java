package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;

import com.tasku.core.domain.board.exception.DomainValidationException;

import static org.junit.jupiter.api.Assertions.*;

class DefinicionListaInicialTest {

    @Test
    void shouldCreateWithValidValues() {
        DefinicionListaInicial d = new DefinicionListaInicial("TODO", 5);
        assertEquals("TODO", d.name());
        assertEquals(5, d.cardLimit());
    }

    @Test
    void shouldThrowWhenNameIsNull() {
        assertThrows(DomainValidationException.class,
                () -> new DefinicionListaInicial(null, 5));
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        assertThrows(DomainValidationException.class,
                () -> new DefinicionListaInicial("   ", 5));
    }

    @Test
    void shouldThrowWhenCardLimitIsZeroOrNegative() {
        assertThrows(DomainValidationException.class,
                () -> new DefinicionListaInicial("TODO", 0));
        assertThrows(DomainValidationException.class,
                () -> new DefinicionListaInicial("DOING", -5));
    }

    @Test
    void shouldTrimWhitespaceFromName() {
        DefinicionListaInicial d = new DefinicionListaInicial("  TODO  ", 5);
        assertEquals("TODO", d.name());
    }
}
