package com.tasku.core.application.board;

import com.tasku.core.application.board.dto.CreateBoardRequest;
import com.tasku.core.application.board.dto.CreateCardRequest;
import com.tasku.core.application.board.dto.MoveCardRequest;
import com.tasku.core.application.board.dto.ShareBoardRequest;
import com.tasku.core.application.board.event.CardMovedEvent;
import com.tasku.core.domain.board.exception.DomainConflictException;
import com.tasku.core.domain.board.exception.DomainNotFoundException;
import com.tasku.core.domain.board.exception.DomainValidationException;
import com.tasku.core.domain.model.board.Tablero;
import com.tasku.core.domain.model.board.ListaTablero;
import com.tasku.core.domain.model.board.Tarjeta;
import com.tasku.core.domain.model.board.TipoTarjeta;
import com.tasku.core.domain.model.board.TarjetaChecklist;
import com.tasku.core.domain.model.board.DefinicionListaInicial;
import com.tasku.core.domain.model.board.RolComparticion;
import com.tasku.core.domain.model.board.TarjetaTarea;
import com.tasku.core.domain.model.board.CuentaUsuario;
import com.tasku.core.domain.board.port.ListaTableroStore;
import com.tasku.core.domain.board.port.TableroStore;
import com.tasku.core.domain.board.port.TarjetaStore;
import com.tasku.core.domain.board.port.UsuarioStore;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class BoardApplicationService {
    private final UsuarioStore userStore;
    private final TableroStore boardStore;
    private final ListaTableroStore boardListStore;
    private final TarjetaStore cardStore;
    private final ApplicationEventPublisher eventPublisher;

    public BoardApplicationService(UsuarioStore userStore,
                                   TableroStore boardStore,
                                   ListaTableroStore boardListStore,
                                   TarjetaStore cardStore,
                                   ApplicationEventPublisher eventPublisher) {
        this.userStore = userStore;
        this.boardStore = boardStore;
        this.boardListStore = boardListStore;
        this.cardStore = cardStore;
        this.eventPublisher = eventPublisher;
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

        Tablero board = Tablero.createNew(request.ownerEmail(), request.name(), request.color(), request.description(), initialLists);
        return boardStore.save(board);
    }

    @Transactional(readOnly = true)
    public Tablero getBoardByUrl(String boardUrl) {
        validateText(boardUrl, "La url del tablero es obligatoria");
        return boardStore.findByUrl(boardUrl)
                .orElseThrow(() -> new DomainNotFoundException("No existe un tablero con la url indicada"));
    }

    @Transactional(readOnly = true)
    public List<Tablero> findBoardsByOwnerEmail(String ownerEmail) {
        validateText(ownerEmail, "El email del duenio es obligatorio");
        return boardStore.findByOwnerEmailIgnoreCase(ownerEmail);
    }

    @Transactional(readOnly = true)
    public List<Tablero> findBoardsSharedWithEmail(String email) {
        validateText(email, "El email compartido es obligatorio");
        return boardStore.findBySharedEmailIgnoreCase(email);
    }

    @Transactional
    public Tablero shareBoard(ShareBoardRequest request) {
        Objects.requireNonNull(request, "La solicitud de comparticion no puede ser nula");
        validateText(request.boardUrl(), "La url del tablero es obligatoria");
        validateText(request.email(), "El email a compartir es obligatorio");
        RolComparticion role = Objects.requireNonNull(request.role(), "El rol a compartir es obligatorio");

        Tablero board = getBoardByUrl(request.boardUrl());
        Tablero updatedBoard = board.withAddedShare(request.email(), role);
        return boardStore.save(updatedBoard);
    }

    @Transactional
    public Tarjeta createCard(CreateCardRequest request) {
        validateCreateCardRequest(request);
        ListaTablero list = boardListStore.findById(request.listId())
                .orElseThrow(() -> new DomainNotFoundException("No existe la lista indicada para crear la tarjeta"));

        long currentCards = cardStore.countByListId(request.listId());
        if (currentCards >= list.cardLimit()) {
            throw new DomainConflictException("La lista alcanzo su limite de tarjetas");
        }

        Tarjeta card;
        if (request.type() == TipoTarjeta.CHECKLIST) {
            card = TarjetaChecklist.createNew(
                    request.listId(),
                    request.title(),
                    request.description(),
                    request.labels(),
                    request.checklistItems()
            );
        } else {
            card = TarjetaTarea.createNew(
                    request.listId(),
                    request.title(),
                    request.description(),
                    request.labels()
            );
        }

        return cardStore.save(card);
    }

    @Transactional
    public Tarjeta moveCard(MoveCardRequest request) {
        Objects.requireNonNull(request, "La solicitud para mover tarjeta no puede ser nula");
        validateText(request.authorEmail(), "El autor del movimiento es obligatorio");
        Objects.requireNonNull(request.cardId(), "El id de la tarjeta no puede ser nulo");
        Objects.requireNonNull(request.destinationListId(), "El id de la lista destino no puede ser nulo");

        Tarjeta card = cardStore.findById(request.cardId())
                .orElseThrow(() -> new DomainNotFoundException("No existe la tarjeta indicada"));

        ListaTablero sourceList = boardListStore.findById(card.listId())
                .orElseThrow(() -> new DomainNotFoundException("La lista origen de la tarjeta no existe"));

        ListaTablero destinationList = boardListStore.findById(request.destinationListId())
                .orElseThrow(() -> new DomainNotFoundException("La lista destino no existe"));

        if (!sourceList.boardUrl().equals(destinationList.boardUrl())) {
            throw new DomainValidationException("No se puede mover una tarjeta entre tableros distintos");
        }

        if (!card.listId().equals(request.destinationListId())) {
            long destinationCount = cardStore.countByListId(request.destinationListId());
            if (destinationCount >= destinationList.cardLimit()) {
                throw new DomainConflictException("La lista destino alcanzo su limite de tarjetas");
            }
            card.moveToList(request.destinationListId());
            Tarjeta updated = cardStore.save(card);

            eventPublisher.publishEvent(new CardMovedEvent(
                    updated.id(),
                    sourceList.id(),
                    destinationList.id(),
                    destinationList.boardUrl(),
                    request.authorEmail(),
                    LocalDateTime.now()
            ));
            return updated;
        }

        return card;
    }

    @Transactional(readOnly = true)
    public List<Tarjeta> findCardsByListId(java.util.UUID listId) {
        Objects.requireNonNull(listId, "El id de lista no puede ser nulo");
        return cardStore.findByListId(listId);
    }

    private void ensureOwnerExists(String ownerEmail) {
        userStore.findByEmail(ownerEmail)
                .orElseGet(() -> userStore.save(CuentaUsuario.createNew(ownerEmail)));
    }

    private void validateCreateBoardRequest(CreateBoardRequest request) {
        Objects.requireNonNull(request, "La solicitud para crear tablero no puede ser nula");
        validateText(request.ownerEmail(), "El email del duenio es obligatorio");
        validateText(request.name(), "El nombre del tablero es obligatorio");
        validateText(request.color(), "El color del tablero es obligatorio");
        validateText(request.description(), "La descripcion del tablero es obligatoria");
    }

    private void validateCreateCardRequest(CreateCardRequest request) {
        Objects.requireNonNull(request, "La solicitud para crear tarjeta no puede ser nula");
        Objects.requireNonNull(request.listId(), "La lista destino de la tarjeta no puede ser nula");
        Objects.requireNonNull(request.type(), "El tipo de tarjeta es obligatorio");
        validateText(request.title(), "El titulo de la tarjeta es obligatorio");
        validateText(request.description(), "La descripcion de la tarjeta es obligatoria");

        if (request.type() == TipoTarjeta.CHECKLIST && request.checklistItems() == null) {
            throw new DomainValidationException("Las tarjetas checklist deben incluir lista de items");
        }
    }

    private static String validateText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(message);
        }
        return value.trim();
    }
}

