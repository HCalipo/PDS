package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;

import com.tasku.core.domain.board.exception.DomainValidationException;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TarjetaTareaTest {

    @Test
    void shouldCreateTaskCard() {
        TarjetaTarea card = TarjetaTarea.createNew(
                new ListaTableroId(), "Implementar login", "Integrar backend",
                Set.of(new EtiquetaTarjeta("backend", "#0EA5E9"))
        );
        assertNotNull(card.id());
        assertEquals(TipoTarjeta.TAREA, card.type());
        assertEquals("Implementar login", card.title());
        assertEquals("Integrar backend", card.description());
        assertNotNull(card.labels());
        assertEquals(1, card.labels().size());
        assertFalse(card.archived());
    }

    @Test
    void shouldCreateWithEmptyLabelsWhenNull() {
        TarjetaTarea card = TarjetaTarea.createNew(
                new ListaTableroId(), "Title", "Desc", null
        );
        assertTrue(card.labels().isEmpty());
    }

    @Test
    void shouldCreateWithUUID() {
        UUID listId = UUID.randomUUID();
        TarjetaTarea card = TarjetaTarea.createNew(
                listId, "Task Title", "Task Description",
                Set.of(new EtiquetaTarjeta("feature", "#10B981"))
        );
        assertEquals(listId, card.listId());
        assertEquals("Task Title", card.title());
        assertEquals(TipoTarjeta.TAREA, card.type());
    }

    @Test
    void shouldThrowWhenTitleIsNull() {
        assertThrows(DomainValidationException.class,
                () -> TarjetaTarea.createNew(new ListaTableroId(), null, "Description", Set.of()));
    }

    @Test
    void shouldThrowWhenTitleIsBlank() {
        assertThrows(DomainValidationException.class,
                () -> TarjetaTarea.createNew(new ListaTableroId(), "   ", "Description", Set.of()));
    }

    @Test
    void shouldThrowWhenDescriptionIsNull() {
        assertThrows(DomainValidationException.class,
                () -> TarjetaTarea.createNew(new ListaTableroId(), "Title", null, Set.of()));
    }

}
