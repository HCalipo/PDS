package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TableroCompartidoTest {

    private final TableroUrl boardUrl = TableroUrl.createNew();

    @Test
    void shouldThrowWhenBoardUrlIsNull() {
        assertThrows(NullPointerException.class,
                () -> new TableroCompartido(1L, (TableroUrl) null, new Email("u@t.com"), RolComparticion.VIEWER));
    }

    @Test
    void shouldThrowWhenRoleIsNull() {
        assertThrows(NullPointerException.class,
                () -> new TableroCompartido(1L, boardUrl, new Email("u@t.com"), null));
    }

    @Test
    void shouldReturnEmailValue() {
        TableroCompartido tc = new TableroCompartido(1L, boardUrl, new Email("u@t.com"), RolComparticion.VIEWER);
        assertEquals("u@t.com", tc.email());
    }

    @Test
    void shouldThrowWhenEmailIsNull() {
        assertThrows(NullPointerException.class,
                () -> new TableroCompartido(1L, boardUrl, null, RolComparticion.VIEWER));
    }

    @Test
    void shouldReturnBoardUrl() {
        TableroCompartido tc = new TableroCompartido(1L, boardUrl, new Email("u@t.com"), RolComparticion.VIEWER);
        assertEquals(boardUrl.value(), tc.boardUrl());
    }

    @Test
    void shouldReturnBoardUrlValueObject() {
        TableroCompartido tc = new TableroCompartido(1L, boardUrl, new Email("u@t.com"), RolComparticion.VIEWER);
        assertEquals(boardUrl, tc.boardUrlValue());
    }

    @Test
    void shouldReturnEmailValueObject() {
        Email email = new Email("u@t.com");
        TableroCompartido tc = new TableroCompartido(1L, boardUrl, email, RolComparticion.VIEWER);
        assertEquals(email, tc.emailValue());
    }

    @Test
    void shouldReturnRole() {
        TableroCompartido tc = new TableroCompartido(1L, boardUrl, new Email("u@t.com"), RolComparticion.EDITOR);
        assertEquals(RolComparticion.EDITOR, tc.role());
    }

    @Test
    void shouldReturnId() {
        TableroCompartido tc = new TableroCompartido(42L, boardUrl, new Email("u@t.com"), RolComparticion.VIEWER);
        assertEquals(42L, tc.id());
    }
}
