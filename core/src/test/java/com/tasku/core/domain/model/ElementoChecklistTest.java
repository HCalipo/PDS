package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElementoChecklistTest {

    @Test
    void shouldCreateWithValidValues() {
        ElementoChecklist item = new ElementoChecklist("Build", true);
        assertEquals("Build", item.description());
        assertTrue(item.completed());
    }

    @Test
    void withCompletedShouldReturnNewInstanceWithUpdatedStatus() {
        ElementoChecklist original = new ElementoChecklist("Build", false);
        ElementoChecklist updated = original.withCompleted(true);
        assertFalse(original.completed());
        assertTrue(updated.completed());
        assertEquals(original.description(), updated.description());
        assertNotSame(original, updated);
    }

    @Test
    void shouldThrowWhenDescriptionIsNull() {
        assertThrows(DomainValidationException.class, () -> new ElementoChecklist(null, false));
    }

    @Test
    void shouldThrowWhenDescriptionIsEmpty() {
        assertThrows(DomainValidationException.class, () -> new ElementoChecklist("", false));
    }

}
