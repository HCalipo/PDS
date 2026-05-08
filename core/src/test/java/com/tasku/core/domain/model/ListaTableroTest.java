package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainValidationException;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class ListaTableroTest {

    private final TableroUrl boardUrl = TableroUrl.createNew();

    @Test
    void shouldCreateWithAllParameters() {
        ListaTableroId id = new ListaTableroId();
        ListaTablero list = new ListaTablero(id, boardUrl, "TODO", 10, "#FF5733");
        assertEquals(id, list.listIdValue());
        assertEquals(boardUrl.value(), list.boardUrl());
        assertEquals("TODO", list.name());
        assertEquals(10, list.cardLimit());
        assertEquals("#FF5733", list.colorHex());
    }

    @Test
    void shouldThrowWhenCardLimitIsZero() {
        assertThrows(DomainValidationException.class,
                () -> new ListaTablero(new ListaTableroId(), boardUrl, "TODO", 0, "#000"));
    }

    @Test
    void shouldUseDefaultColorWhenColorHexIsNull() {
        ListaTablero list = new ListaTablero(new ListaTableroId(), boardUrl, "TODO", 10, null);
        assertEquals("#90CAF9", list.colorHex());
    }

}
