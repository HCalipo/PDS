package com.tasku.core.application.tablero.usecase;

import com.tasku.core.application.tablero.usecase.dto.AssignCardLabelRequest;
import com.tasku.core.application.tablero.usecase.dto.ChangeBoardStatusRequest;
import com.tasku.core.application.tablero.usecase.dto.CompleteCardRequest;
import com.tasku.core.application.tablero.usecase.dto.CreateBoardRequest;
import com.tasku.core.application.tablero.usecase.dto.CreateCardRequest;
import com.tasku.core.application.tablero.usecase.dto.CreateListRequest;
import com.tasku.core.application.tablero.usecase.dto.MoveCardRequest;
import com.tasku.core.application.tablero.usecase.dto.RenameListRequest;
import com.tasku.core.application.tablero.usecase.dto.ShareBoardRequest;
import com.tasku.core.application.tablero.usecase.event.TarjetaMovidaEvent;
import com.tasku.core.domain.board.exception.DomainConflictException;
import com.tasku.core.domain.board.exception.DomainForbiddenException;
import com.tasku.core.domain.board.exception.DomainNotFoundException;
import com.tasku.core.domain.board.exception.DomainValidationException;
import com.tasku.core.domain.model.EtiquetaTarjeta;
import com.tasku.core.domain.model.Email;
import com.tasku.core.domain.model.ListaTableroId;
import com.tasku.core.domain.model.ListaTablero;
import com.tasku.core.domain.model.Tablero;
import com.tasku.core.domain.model.TableroUrl;
import com.tasku.core.domain.model.Tarjeta;
import com.tasku.core.domain.model.TarjetaId;
import com.tasku.core.domain.model.TipoTarjeta;
import com.tasku.core.domain.model.TarjetaChecklist;
import com.tasku.core.domain.model.DefinicionListaInicial;
import com.tasku.core.domain.model.RolComparticion;
import com.tasku.core.domain.model.TarjetaTarea;
import com.tasku.core.domain.model.Usuario;
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
public class TableroUseCaseService {
    private final UsuarioStore userStore;
    private final TableroStore boardStore;
    private final ListaTableroStore boardListStore;
    private final TarjetaStore cardStore;
    private final ApplicationEventPublisher eventPublisher;

    public TableroUseCaseService(UsuarioStore userStore,
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
        Tablero updatedBoard = board.withAddedList(request.name(), request.cardLimit());
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

    @Transactional
    public Tarjeta createCard(CreateCardRequest request) {
        validateCreateCardRequest(request);
        ListaTablero list = boardListStore.findById(request.listId())
                .orElseThrow(() -> new DomainNotFoundException("No existe la lista indicada para crear la tarjeta"));
        Tablero board = boardStore.findByUrl(list.boardUrlValue())
                .orElseThrow(() -> new DomainNotFoundException("No existe el tablero asociado a la lista"));
        ensureBoardAllowsCardMutations(board);

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
        Objects.requireNonNull(request.authorEmail(), "El autor del movimiento es obligatorio");
        Objects.requireNonNull(request.cardId(), "El id de la tarjeta no puede ser nulo");
        Objects.requireNonNull(request.destinationListId(), "El id de la lista destino no puede ser nulo");

        Tarjeta card = cardStore.findById(request.cardId())
                .orElseThrow(() -> new DomainNotFoundException("No existe la tarjeta indicada"));

        ListaTablero sourceList = boardListStore.findById(card.listIdValue())
                .orElseThrow(() -> new DomainNotFoundException("La lista origen de la tarjeta no existe"));

        ListaTablero destinationList = boardListStore.findById(request.destinationListId())
                .orElseThrow(() -> new DomainNotFoundException("La lista destino no existe"));

        if (!sourceList.boardUrlValue().equals(destinationList.boardUrlValue())) {
            throw new DomainValidationException("No se puede mover una tarjeta entre tableros distintos");
        }

        Tablero board = boardStore.findByUrl(sourceList.boardUrlValue())
                .orElseThrow(() -> new DomainNotFoundException("No existe el tablero asociado a la tarjeta"));
        ensureBoardAllowsCardMutations(board);

        if (!card.listIdValue().equals(request.destinationListId())) {
            long destinationCount = cardStore.countByListId(request.destinationListId());
            if (destinationCount >= destinationList.cardLimit()) {
                throw new DomainConflictException("La lista destino alcanzo su limite de tarjetas");
            }
            card.moveToList(request.destinationListId());
            Tarjeta updated = cardStore.save(card);

            eventPublisher.publishEvent(new TarjetaMovidaEvent(
                    updated.cardIdValue(),
                    sourceList.listIdValue(),
                    destinationList.listIdValue(),
                    destinationList.boardUrlValue(),
                    request.authorEmail(),
                    LocalDateTime.now()
            ));
            return updated;
        }

        return card;
    }

    @Transactional
    public Tarjeta completeCard(CompleteCardRequest request) {
        Objects.requireNonNull(request, "La solicitud para completar tarjeta no puede ser nula");
        Objects.requireNonNull(request.authorEmail(), "El autor es obligatorio");
        Objects.requireNonNull(request.cardId(), "El id de la tarjeta no puede ser nulo");

        Tarjeta card = cardStore.findById(request.cardId())
                .orElseThrow(() -> new DomainNotFoundException("No existe la tarjeta indicada"));
        ListaTablero list = boardListStore.findById(card.listIdValue())
                .orElseThrow(() -> new DomainNotFoundException("La lista de la tarjeta no existe"));
        Tablero board = boardStore.findByUrl(list.boardUrlValue())
                .orElseThrow(() -> new DomainNotFoundException("No existe el tablero asociado a la tarjeta"));
        ensureBoardAllowsCardMutations(board);

        card.archive();
        return cardStore.save(card);
    }

    @Transactional
    public Tarjeta assignLabelToCard(AssignCardLabelRequest request) {
        Objects.requireNonNull(request, "La solicitud para asignar etiqueta no puede ser nula");
        Objects.requireNonNull(request.cardId(), "El id de la tarjeta no puede ser nulo");
        validateText(request.labelName(), "El nombre de la etiqueta es obligatorio");
        validateText(request.colorHex(), "El color de la etiqueta es obligatorio");

        Tarjeta card = cardStore.findById(request.cardId())
            .orElseThrow(() -> new DomainNotFoundException("No existe la tarjeta indicada"));
        ListaTablero list = boardListStore.findById(card.listIdValue())
            .orElseThrow(() -> new DomainNotFoundException("La lista de la tarjeta no existe"));
        Tablero board = boardStore.findByUrl(list.boardUrlValue())
            .orElseThrow(() -> new DomainNotFoundException("No existe el tablero asociado a la tarjeta"));
        ensureBoardAllowsCardMutations(board);

        card.addLabel(new EtiquetaTarjeta(request.labelName(), request.colorHex()));
        return cardStore.save(card);
    }

    @Transactional(readOnly = true)
    public List<Tarjeta> findCardsByListId(ListaTableroId listId) {
        Objects.requireNonNull(listId, "El id de lista no puede ser nulo");
        return cardStore.findByListId(listId);
    }

    @Transactional(readOnly = true)
    public Tarjeta getCardById(TarjetaId cardId) {
        Objects.requireNonNull(cardId, "El id de tarjeta no puede ser nulo");
        return cardStore.findById(cardId)
                .orElseThrow(() -> new DomainNotFoundException("No existe la tarjeta indicada"));
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

    private static void ensureBoardAllowsCardMutations(Tablero board) {
        if (board.isBlocked()) {
            throw new DomainForbiddenException("El tablero esta bloqueado y no permite modificar tarjetas");
        }
    }
}


