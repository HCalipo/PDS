package com.tasku.core.application.tablero.usecase;

import com.tasku.core.application.tablero.usecase.dto.ChangeBoardStatusRequest;
import com.tasku.core.application.tablero.usecase.dto.CreateBoardRequest;
import com.tasku.core.application.tablero.usecase.dto.CreateListRequest;
import com.tasku.core.application.tablero.usecase.dto.RenameListRequest;
import com.tasku.core.application.tablero.usecase.dto.ShareBoardRequest;
import com.tasku.core.domain.board.exception.DomainConflictException;
import com.tasku.core.domain.board.exception.DomainNotFoundException;
import com.tasku.core.domain.board.exception.DomainValidationException;
import com.tasku.core.domain.model.DefinicionListaInicial;
import com.tasku.core.domain.model.Email;
import com.tasku.core.domain.model.RolComparticion;
import com.tasku.core.domain.model.Tablero;
import com.tasku.core.domain.model.TableroUrl;
import com.tasku.core.domain.model.Usuario;
import com.tasku.core.domain.board.port.TableroStore;
import com.tasku.core.domain.board.port.UsuarioStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class TableroUseCaseService {
    private final UsuarioStore userStore;
    private final TableroStore boardStore;

    public TableroUseCaseService(UsuarioStore userStore, TableroStore boardStore) {
        this.userStore = userStore;
        this.boardStore = boardStore;
    }

    @Transactional
    public Tablero createBoard(CreateBoardRequest request) {
        validateCreateBoardRequest(request);

        if (boardStore.existsByOwnerEmailAndNameIgnoreCase(request.ownerEmail(), request.name())) {
            throw new DomainConflictException("Ya existe un tablero con ese nombre para el mismo duenio");
        }

        ensureOwnerExists(request.ownerEmail());

        List<DefinicionListaInicial> initialLists = request.initialLists();
        if (initialLists == null || initialLists.isEmpty()) {
            initialLists = List.of(
                    new DefinicionListaInicial("TODO", 100),
                    new DefinicionListaInicial("DOING", 100),
                    new DefinicionListaInicial("DONE", 100)
            );
        }

        Tablero board = Tablero.createNew(request.ownerEmail().email(), request.name(), request.color(), request.description(), initialLists);
        return boardStore.save(board);
    }

    @Transactional(readOnly = true)
    public Tablero getBoardByUrl(TableroUrl boardUrl) {
        Objects.requireNonNull(boardUrl, "La url del tablero es obligatoria");
        return boardStore.findByUrl(boardUrl)
                .orElseThrow(() -> new DomainNotFoundException("No existe un tablero con la url indicada"));
    }

    @Transactional(readOnly = true)
    public List<Tablero> findBoardsByOwnerEmail(Email ownerEmail) {
        Objects.requireNonNull(ownerEmail, "El email del duenio es obligatorio");
        return boardStore.findByOwnerEmailIgnoreCase(ownerEmail);
    }

    @Transactional(readOnly = true)
    public List<Tablero> findBoardsSharedWithEmail(Email email) {
        Objects.requireNonNull(email, "El email compartido es obligatorio");
        return boardStore.findBySharedEmailIgnoreCase(email);
    }

    @Transactional
    public Tablero shareBoard(ShareBoardRequest request) {
        Objects.requireNonNull(request, "La solicitud de comparticion no puede ser nula");
        Objects.requireNonNull(request.boardUrl(), "La url del tablero es obligatoria");
        Objects.requireNonNull(request.email(), "El email a compartir es obligatorio");
        RolComparticion role = Objects.requireNonNull(request.role(), "El rol a compartir es obligatorio");

        Tablero board = getBoardByUrl(request.boardUrl());
        Tablero updatedBoard = board.withAddedShare(request.email().email(), role);
        return boardStore.save(updatedBoard);
    }

    @Transactional
    public Tablero createList(CreateListRequest request) {
        Objects.requireNonNull(request, "La solicitud para crear lista no puede ser nula");
        Objects.requireNonNull(request.boardUrl(), "La url del tablero es obligatoria");
        validateText(request.name(), "El nombre de la lista es obligatorio");
        if (request.cardLimit() <= 0) {
            throw new DomainValidationException("El limite de tarjetas de la lista debe ser mayor que cero");
        }

        Tablero board = getBoardByUrl(request.boardUrl());
        Tablero updatedBoard = board.withAddedList(request.name(), request.cardLimit(), request.colorHex());
        return boardStore.save(updatedBoard);
    }

    @Transactional
    public Tablero renameList(RenameListRequest request) {
        Objects.requireNonNull(request, "La solicitud para renombrar lista no puede ser nula");
        Objects.requireNonNull(request.boardUrl(), "La url del tablero es obligatoria");
        Objects.requireNonNull(request.listId(), "El id de la lista es obligatorio");
        validateText(request.name(), "El nombre de la lista es obligatorio");

        Tablero board = getBoardByUrl(request.boardUrl());
        Tablero updatedBoard = board.withRenamedList(request.listId(), request.name());
        return boardStore.save(updatedBoard);
    }

    @Transactional
    public Tablero changeBoardStatus(ChangeBoardStatusRequest request) {
        Objects.requireNonNull(request, "La solicitud para cambiar estado no puede ser nula");
        Objects.requireNonNull(request.boardUrl(), "La url del tablero es obligatoria");
        Objects.requireNonNull(request.status(), "El estado del tablero es obligatorio");

        Tablero board = getBoardByUrl(request.boardUrl());
        Tablero updatedBoard = board.withStatus(request.status());
        return boardStore.save(updatedBoard);
    }

    private void ensureOwnerExists(Email ownerEmail) {
        userStore.findByEmail(ownerEmail)
                .orElseGet(() -> userStore.save(Usuario.createNew(ownerEmail)));
    }

    private void validateCreateBoardRequest(CreateBoardRequest request) {
        Objects.requireNonNull(request, "La solicitud para crear tablero no puede ser nula");
        Objects.requireNonNull(request.ownerEmail(), "El email del duenio es obligatorio");
        validateText(request.name(), "El nombre del tablero es obligatorio");
        validateText(request.color(), "El color del tablero es obligatorio");
        validateText(request.description(), "La descripcion del tablero es obligatoria");
    }

    private static String validateText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(message);
        }
        return value.trim();
    }
}
