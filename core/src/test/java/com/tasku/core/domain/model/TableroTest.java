package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainConflictException;
import com.tasku.core.domain.board.exception.DomainNotFoundException;
import com.tasku.core.domain.board.exception.DomainValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TableroTest {

    private Tablero board;

    @BeforeEach
    void setUp() {
        board = Tablero.createNew("owner@tasku.dev", "Proyecto Alpha", "#0057B8",
                "Tablero principal",
                List.of(
                        new DefinicionListaInicial("TODO", 5),
                        new DefinicionListaInicial("DOING", 5)
                ));
    }

    @Test
    void shouldCreateNewBoard() {
        assertNotNull(board.url());
        assertTrue(board.url().startsWith("tasku://tablero/"));
        assertEquals("Proyecto Alpha", board.name());
        assertEquals("owner@tasku.dev", board.ownerEmail());
        assertEquals("#0057B8", board.color());
        assertEquals("Tablero principal", board.description());
        assertEquals(EstadoTablero.ACTIVE, board.status());
        assertEquals(2, board.lists().size());
        assertTrue(board.sharedWith().isEmpty());
    }

    @Test
    void shouldThrowWhenUrlIsNull() {
        assertThrows(NullPointerException.class, () -> new Tablero(
                null, "name", new Email("a@b.com"), "#000", "desc",
                EstadoTablero.ACTIVE, List.of(), Set.of()));
    }

    @Test
    void shouldThrowWhenNameIsNull() {
        assertThrows(DomainValidationException.class, () -> new Tablero(
                TableroUrl.createNew(), null, new Email("a@b.com"), "#000", "desc",
                EstadoTablero.ACTIVE, List.of(), Set.of()));
    }

    @Test
    void shouldThrowWhenOwnerEmailIsNull() {
        assertThrows(NullPointerException.class, () -> new Tablero(
                TableroUrl.createNew(), "name", null, "#000", "desc",
                EstadoTablero.ACTIVE, List.of(), Set.of()));
    }

    @Test
    void shouldAddList() {
        Tablero updated = board.withAddedList("DONE", 10, "#90CAF9");
        assertEquals(3, updated.lists().size());
        assertEquals("DONE", updated.lists().get(2).name());
        assertEquals(board.url(), updated.url());
        assertEquals(board.name(), updated.name());
    }

    @Test
    void shouldRenameList() {
        ListaTableroId listId = board.lists().get(0).listIdValue();
        Tablero updated = board.withRenamedList(listId, "BACKLOG");
        assertEquals("BACKLOG", updated.lists().get(0).name());
        assertEquals("DOING", updated.lists().get(1).name());
    }

    @Test
    void shouldThrowWhenRenamingToExistingName() {
        assertThrows(DomainConflictException.class,
                () -> board.withRenamedList(board.lists().get(0).listIdValue(), "DOING"));
    }

    @Test
    void shouldThrowWhenRenamingNonExistentList() {
        assertThrows(DomainNotFoundException.class,
                () -> board.withRenamedList(new ListaTableroId(), "NEW"));
    }

    @Test
    void shouldRemoveList() {
        ListaTableroId listId = board.lists().get(0).listIdValue();
        Tablero updated = board.withRemovedList(listId);
        assertEquals(1, updated.lists().size());
        assertEquals("DOING", updated.lists().get(0).name());
    }

    @Test
    void shouldShareWithUser() {
        Tablero shared = board.withAddedShare("user@tasku.dev", RolComparticion.EDITOR);
        assertEquals(1, shared.sharedWith().size());
        assertTrue(shared.hasAccess("user@tasku.dev"));
        assertEquals(RolComparticion.EDITOR, shared.effectiveRoleOf("user@tasku.dev"));
    }

    @Test
    void shouldThrowWhenSharingDuplicateEmail() {
        Tablero shared = board.withAddedShare("user@tasku.dev", RolComparticion.VIEWER);
        assertThrows(DomainConflictException.class,
                () -> shared.withAddedShare("User@Tasku.dev", RolComparticion.EDITOR));
    }

    @Test
    void ownerShouldHaveAdminRole() {
        assertEquals(RolComparticion.ADMIN, board.effectiveRoleOf("owner@tasku.dev"));
    }

    @Test
    void shouldChangeStatusToBlocked() {
        Tablero updated = board.withStatus(EstadoTablero.BLOCKED);
        assertEquals(EstadoTablero.BLOCKED, updated.status());
        assertTrue(updated.isBlocked());
    }

    @Test
    void shouldFindListByUuid() {
        UUID listId = board.lists().get(0).listIdValue().id();
        ListaTablero found = board.findListOrFail(listId);
        assertEquals("TODO", found.name());
    }

    @Test
    void shouldThrowWhenFindingNonExistentList() {
        assertThrows(DomainValidationException.class,
                () -> board.findListOrFail(UUID.randomUUID()));
    }

}
