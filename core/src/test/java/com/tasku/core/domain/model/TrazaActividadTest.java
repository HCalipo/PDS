package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TrazaActividadTest {

    @Test
    void shouldCreateWithAllParameters() {
        UUID id = UUID.randomUUID();
        TableroUrl boardUrl = TableroUrl.createNew();
        Email email = new Email("user@tasku.dev");
        LocalDateTime now = LocalDateTime.now();
        TrazaActividad trace = new TrazaActividad(id, boardUrl, email, "Tarjeta movida", now);
        assertEquals(id, trace.id());
        assertEquals(boardUrl.value(), trace.boardUrl());
        assertEquals("user@tasku.dev", trace.authorEmail());
        assertEquals("Tarjeta movida", trace.description());
        assertEquals(now, trace.date());
    }

    @Test
    void shouldThrowWhenIdIsNull() {
        assertThrows(NullPointerException.class,
                () -> new TrazaActividad(null, TableroUrl.createNew(), new Email("u@t.com"),
                        "desc", LocalDateTime.now()));
    }

    @Test
    void shouldThrowWhenAuthorEmailIsNull() {
        assertThrows(NullPointerException.class,
                () -> new TrazaActividad(UUID.randomUUID(), TableroUrl.createNew(), null,
                        "desc", LocalDateTime.now()));
    }

    @Test
    void shouldThrowWhenDescriptionIsNull() {
        assertThrows(DomainValidationException.class,
                () -> new TrazaActividad(UUID.randomUUID(), TableroUrl.createNew(), new Email("u@t.com"),
                        null, LocalDateTime.now()));
    }

    @Test
    void shouldThrowWhenDateIsNull() {
        assertThrows(NullPointerException.class,
                () -> new TrazaActividad(UUID.randomUUID(), TableroUrl.createNew(), new Email("u@t.com"),
                        "desc", null));
    }

}
