package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RolComparticionTest {

    @Test
    void shouldHaveViewerEditorAdmin() {
        assertEquals(3, RolComparticion.values().length);
    }

    @Test
    void viewerCannotEdit() {
        assertFalse(RolComparticion.VIEWER.canEdit());
    }

    @Test
    void adminCanEdit() {
        assertTrue(RolComparticion.ADMIN.canEdit());
    }

    @Test
    void editorCanEdit() {
        assertTrue(RolComparticion.EDITOR.canEdit());
    }

    @Test
    void viewerIsNotAdmin() {
        assertFalse(RolComparticion.VIEWER.isAdmin());
    }

    @Test
    void editorIsNotAdmin() {
        assertFalse(RolComparticion.EDITOR.isAdmin());
    }
}
