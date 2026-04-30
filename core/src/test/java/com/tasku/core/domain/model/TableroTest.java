package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainConflictException;
import com.tasku.core.domain.board.exception.DomainNotFoundException;
import com.tasku.core.domain.board.exception.DomainValidationException;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TableroTest {

    private final String OWNER_EMAIL = "owner@test.com";
    private final String BOARD_NAME = "Test Board";
    private final String COLOR = "blue";
    private final String DESCRIPTION = "Test Description";

    @Test
    void constructor_validaCamposNoNulos() {
        TableroUrl url = TableroUrl.createNew();
        Email owner = new Email(OWNER_EMAIL);
        assertThatThrownBy(() -> new Tablero(null, BOARD_NAME, owner, COLOR, DESCRIPTION, EstadoTablero.ACTIVE, List.of(), Set.of()))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("url");
        assertThatThrownBy(() -> new Tablero(url, null, owner, COLOR, DESCRIPTION, EstadoTablero.ACTIVE, List.of(), Set.of()))
                .isInstanceOf(DomainValidationException.class).hasMessageContaining("nombre");
        assertThatThrownBy(() -> new Tablero(url, BOARD_NAME, null, COLOR, DESCRIPTION, EstadoTablero.ACTIVE, List.of(), Set.of()))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("duenio");
    }

    @Test
    void createNew_generaTableroValido() {
        Tablero board = Tablero.createNew(OWNER_EMAIL, BOARD_NAME, COLOR, DESCRIPTION, null);
        assertThat(board.url()).isNotBlank();
        assertThat(board.name()).isEqualTo(BOARD_NAME);
        assertThat(board.ownerEmail()).isEqualTo(OWNER_EMAIL);
        assertThat(board.status()).isEqualTo(EstadoTablero.ACTIVE);
        assertThat(board.lists()).isEmpty();

        List<DefinicionListaInicial> initialLists = List.of(
                new DefinicionListaInicial("TODO", 10),
                new DefinicionListaInicial("DOING", 20)
        );
        Tablero boardWithLists = Tablero.createNew(OWNER_EMAIL, BOARD_NAME, COLOR, DESCRIPTION, initialLists);
        assertThat(boardWithLists.lists()).hasSize(2);
        assertThat(boardWithLists.lists()).extracting(ListaTablero::name).containsExactly("TODO", "DOING");
    }

    @Test
    void withAddedList_agregaLista_cuandoValido() {
        Tablero board = Tablero.createNew(OWNER_EMAIL, BOARD_NAME, COLOR, DESCRIPTION, null);
        Tablero updated = board.withAddedList("New List", 10);
        assertThat(updated.lists()).hasSize(1);
        assertThat(updated.lists().get(0).name()).isEqualTo("New List");
    }

    @Test
    void withAddedList_lanzaConflicto_nombreDuplicado() {
        Tablero board = Tablero.createNew(OWNER_EMAIL, BOARD_NAME, COLOR, DESCRIPTION,
                List.of(new DefinicionListaInicial("Existing", 10)));
        assertThatThrownBy(() -> board.withAddedList("Existing", 10))
                .isInstanceOf(DomainConflictException.class).hasMessageContaining("Ya existe una lista");
    }

    @Test
    void withAddedList_lanzaValidacion_limiteInvalido() {
        Tablero board = Tablero.createNew(OWNER_EMAIL, BOARD_NAME, COLOR, DESCRIPTION, null);
        assertThatThrownBy(() -> board.withAddedList("List", 0))
                .isInstanceOf(DomainValidationException.class).hasMessageContaining("limite de tarjetas");
    }

    @Test
    void withRenamedList_renombraLista_cuandoValido() {
        Tablero board = Tablero.createNew(OWNER_EMAIL, BOARD_NAME, COLOR, DESCRIPTION,
                List.of(new DefinicionListaInicial("Old Name", 10)));
        ListaTableroId listId = board.lists().get(0).listIdValue();
        Tablero updated = board.withRenamedList(listId, "New Name");
        assertThat(updated.lists().get(0).name()).isEqualTo("New Name");
    }

    @Test
    void withRenamedList_lanzaNotFound_listaNoExiste() {
        Tablero board = Tablero.createNew(OWNER_EMAIL, BOARD_NAME, COLOR, DESCRIPTION, null);
        assertThatThrownBy(() -> board.withRenamedList(new ListaTableroId(java.util.UUID.randomUUID()), "New Name"))
                .isInstanceOf(DomainNotFoundException.class).hasMessageContaining("No existe la lista");
    }

    @Test
    void withStatus_cambiaEstado_cuandoDiferente() {
        Tablero board = Tablero.createNew(OWNER_EMAIL, BOARD_NAME, COLOR, DESCRIPTION, null);
        Tablero updated = board.withStatus(EstadoTablero.BLOCKED);
        assertThat(updated.status()).isEqualTo(EstadoTablero.BLOCKED);
        Tablero same = updated.withStatus(EstadoTablero.BLOCKED);
        assertThat(same).isSameAs(updated);
    }

    @Test
    void isBlocked_retornaTrue_paraBloqueado() {
        Tablero blocked = Tablero.createNew(OWNER_EMAIL, BOARD_NAME, COLOR, DESCRIPTION, null)
                .withStatus(EstadoTablero.BLOCKED);
        assertThat(blocked.isBlocked()).isTrue();
        Tablero active = Tablero.createNew(OWNER_EMAIL, BOARD_NAME, COLOR, DESCRIPTION, null);
        assertThat(active.isBlocked()).isFalse();
    }

    @Test
    void withAddedShare_agregaComparticion_cuandoValido() {
        Tablero board = Tablero.createNew(OWNER_EMAIL, BOARD_NAME, COLOR, DESCRIPTION, null);
        Tablero updated = board.withAddedShare("share@test.com", RolComparticion.VIEWER);
        assertThat(updated.sharedWith()).hasSize(1);
        assertThat(updated.sharedWith().iterator().next().email()).isEqualTo("share@test.com");
    }

    @Test
    void withAddedShare_lanzaConflicto_emailDuplicado() {
        Tablero board = Tablero.createNew(OWNER_EMAIL, BOARD_NAME, COLOR, DESCRIPTION, null)
                .withAddedShare("share@test.com", RolComparticion.VIEWER);
        assertThatThrownBy(() -> board.withAddedShare("share@test.com", RolComparticion.EDITOR))
                .isInstanceOf(DomainConflictException.class).hasMessageContaining("ya esta compartido");
    }

    @Test
    void findListOrFail_retornaLista_cuandoExiste() {
        Tablero board = Tablero.createNew(OWNER_EMAIL, BOARD_NAME, COLOR, DESCRIPTION,
                List.of(new DefinicionListaInicial("Test List", 10)));
        ListaTableroId listId = board.lists().get(0).listIdValue();
        ListaTablero found = board.findListOrFail(listId);
        assertThat(found.listIdValue()).isEqualTo(listId);
    }

    @Test
    void findListOrFail_lanzaValidacion_listaNoPertenece() {
        Tablero board = Tablero.createNew(OWNER_EMAIL, BOARD_NAME, COLOR, DESCRIPTION, null);
        assertThatThrownBy(() -> board.findListOrFail(new ListaTableroId(java.util.UUID.randomUUID())))
                .isInstanceOf(DomainValidationException.class).hasMessageContaining("no pertenece al tablero");
    }

    @Test
    void listsYSharedWith_retornanCopiasInmutables() {
        Tablero board = Tablero.createNew(OWNER_EMAIL, BOARD_NAME, COLOR, DESCRIPTION, null)
                .withAddedList("List1", 10)
                .withAddedShare("share@test.com", RolComparticion.VIEWER);
        assertThatThrownBy(() -> board.lists().add(null)).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> board.sharedWith().add(null)).isInstanceOf(UnsupportedOperationException.class);
    }
}
