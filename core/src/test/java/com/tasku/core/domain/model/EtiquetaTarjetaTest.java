package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;

import com.tasku.core.domain.board.exception.DomainValidationException;

import static org.junit.jupiter.api.Assertions.*;

class EtiquetaTarjetaTest {

    @Test
    void shouldCreateWithValidValues() {
        EtiquetaTarjeta e = new EtiquetaTarjeta("backend", "#0EA5E9");
        assertEquals("backend", e.name());
        assertEquals("#0EA5E9", e.colorHex());
    }

    @Test
    void shouldThrowWhenNameIsNull() {
        assertThrows(DomainValidationException.class,
                () -> new EtiquetaTarjeta(null, "#0EA5E9"));
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        assertThrows(DomainValidationException.class,
                () -> new EtiquetaTarjeta("   ", "#0EA5E9"));
    }

    @Test
    void shouldThrowWhenColorIsNull() {
        assertThrows(DomainValidationException.class,
                () -> new EtiquetaTarjeta("backend", null));
    }

    @Test
    void shouldThrowWhenColorIsBlank() {
        assertThrows(DomainValidationException.class,
                () -> new EtiquetaTarjeta("backend", "   "));
    }
}
