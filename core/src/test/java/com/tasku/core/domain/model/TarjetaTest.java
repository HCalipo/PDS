package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TarjetaTest {

    private Tarjeta createTaskCard() {
        return TarjetaTarea.createNew(new ListaTableroId(), "Titulo", "Descripcion",
                Set.of(new EtiquetaTarjeta("bug", "#FF0000")));
    }

    @Test
    void shouldCreateTaskCard() {
        Tarjeta t = createTaskCard();
        assertNotNull(t.id());
        assertNotNull(t.listId());
        assertEquals(TipoTarjeta.TAREA, t.type());
        assertEquals("Titulo", t.title());
        assertEquals("Descripcion", t.description());
        assertFalse(t.archived());
        assertEquals(1, t.labels().size());
    }

    @Test
    void shouldMoveToList() {
        ListaTableroId destId = new ListaTableroId();
        Tarjeta t = createTaskCard();
        t.moveToList(destId);
        assertEquals(destId, t.listIdValue());
    }

    @Test
    void shouldArchive() {
        Tarjeta t = createTaskCard();
        t.archive();
        assertTrue(t.archived());
    }

    @Test
    void shouldThrowWhenRenamingToNull() {
        Tarjeta t = createTaskCard();
        assertThrows(DomainValidationException.class, () -> t.rename(null));
    }
    
    @Test
    void shouldThrowWhenAddingNullLabel() {
        Tarjeta t = createTaskCard();
        assertThrows(NullPointerException.class, () -> t.addLabel(null));
    }

    @Test
    void shouldThrowWhenRemovingNullLabel() {
        Tarjeta t = createTaskCard();
        assertThrows(NullPointerException.class, () -> t.removeLabel(null));
    }
}
