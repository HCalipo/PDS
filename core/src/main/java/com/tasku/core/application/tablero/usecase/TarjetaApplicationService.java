package com.tasku.core.application.tablero.usecase;

import com.tasku.core.application.port.EventPublisher;
import com.tasku.core.application.tablero.usecase.dto.AssignCardLabelRequest;
import com.tasku.core.application.tablero.usecase.dto.CompleteCardRequest;
import com.tasku.core.application.tablero.usecase.dto.CreateCardRequest;
import com.tasku.core.application.tablero.usecase.dto.DeleteCardRequest;
import com.tasku.core.application.tablero.usecase.dto.MoveCardRequest;
import com.tasku.core.application.tablero.usecase.dto.RenameCardRequest;
import com.tasku.core.application.tablero.usecase.dto.ToggleChecklistItemRequest;
import com.tasku.core.application.tablero.usecase.event.TarjetaCreadaEvent;
import com.tasku.core.application.tablero.usecase.event.TarjetaMovidaEvent;
import com.tasku.core.application.util.UseCaseValidator;
import com.tasku.core.domain.board.exception.DomainConflictException;
import com.tasku.core.domain.board.exception.DomainForbiddenException;
import com.tasku.core.domain.board.exception.DomainNotFoundException;
import com.tasku.core.domain.board.exception.DomainValidationException;
import com.tasku.core.domain.board.port.ListaTableroStore;
import com.tasku.core.domain.board.port.TableroStore;
import com.tasku.core.domain.board.port.TarjetaStore;
import com.tasku.core.domain.model.EtiquetaTarjeta;
import com.tasku.core.domain.model.ListaTablero;
import com.tasku.core.domain.model.ListaTableroId;
import com.tasku.core.domain.model.Tablero;
import com.tasku.core.domain.model.Tarjeta;
import com.tasku.core.domain.model.TarjetaChecklist;
import com.tasku.core.domain.model.TarjetaId;
import com.tasku.core.domain.model.TarjetaTarea;
import com.tasku.core.domain.model.TipoTarjeta;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class TarjetaApplicationService {

    private final ListaTableroStore boardListStore;
    private final TableroStore boardStore;
    private final TarjetaStore cardStore;
    private final EventPublisher eventPublisher;

    public TarjetaApplicationService(ListaTableroStore boardListStore,
                                     TableroStore boardStore,
                                     TarjetaStore cardStore,
                                     EventPublisher eventPublisher) {
        this.boardListStore = boardListStore;
        this.boardStore = boardStore;
        this.cardStore = cardStore;
        this.eventPublisher = eventPublisher;
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

        Tarjeta saved = cardStore.save(card);

        if (request.authorEmail() != null) {
            eventPublisher.publish(new TarjetaCreadaEvent(
                    saved.cardIdValue(),
                    saved.title(),
                    list.listIdValue(),
                    list.name(),
                    list.boardUrlValue(),
                    request.authorEmail(),
                    LocalDateTime.now()
            ));
        }

        return saved;
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

        if (!card.listIdValue().equals(request.destinationListId())) {
            long destinationCount = cardStore.countByListId(request.destinationListId());
            if (destinationCount >= destinationList.cardLimit()) {
                throw new DomainConflictException("La lista destino alcanzo su limite de tarjetas");
            }
            card.moveToList(request.destinationListId());
            Tarjeta updated = cardStore.save(card);

            eventPublisher.publish(new TarjetaMovidaEvent(
                    updated.cardIdValue(),
                    updated.title(),
                    sourceList.listIdValue(),
                    sourceList.name(),
                    destinationList.listIdValue(),
                    destinationList.name(),
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
        UseCaseValidator.requireText(request.labelName(), "El nombre de la etiqueta es obligatorio");
        UseCaseValidator.requireText(request.colorHex(), "El color de la etiqueta es obligatorio");

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

    @Transactional(readOnly = true)
    public List<Tarjeta> getCompletedCardsForBoard(String boardUrl) {
        UseCaseValidator.requireText(boardUrl, "La URL del tablero es obligatoria");
        return cardStore.findCompletedByBoardUrl(boardUrl);
    }

    @Transactional
    public void deleteCard(DeleteCardRequest request) {
        Objects.requireNonNull(request, "La solicitud para eliminar tarjeta no puede ser nula");
        Objects.requireNonNull(request.cardId(), "El id de la tarjeta no puede ser nulo");

        Tarjeta card = cardStore.findById(request.cardId())
                .orElseThrow(() -> new DomainNotFoundException("No existe la tarjeta indicada"));

        ListaTablero list = boardListStore.findById(card.listIdValue())
                .orElseThrow(() -> new DomainNotFoundException("La lista de la tarjeta no existe"));
        Tablero board = boardStore.findByUrl(list.boardUrlValue())
                .orElseThrow(() -> new DomainNotFoundException("El tablero de la tarjeta no existe"));
        ensureBoardAllowsCardMutations(board);

        cardStore.deleteById(request.cardId());
    }

    @Transactional
    public Tarjeta renameCard(RenameCardRequest request) {
        Objects.requireNonNull(request, "La solicitud para renombrar tarjeta no puede ser nula");
        Objects.requireNonNull(request.cardId(), "El id de la tarjeta no puede ser nulo");
        UseCaseValidator.requireText(request.title(), "El titulo de la tarjeta es obligatorio");

        Tarjeta card = cardStore.findById(request.cardId())
                .orElseThrow(() -> new DomainNotFoundException("No existe la tarjeta indicada"));

        ListaTablero list = boardListStore.findById(card.listIdValue())
                .orElseThrow(() -> new DomainNotFoundException("La lista de la tarjeta no existe"));
        Tablero board = boardStore.findByUrl(list.boardUrlValue())
                .orElseThrow(() -> new DomainNotFoundException("El tablero de la tarjeta no existe"));
        ensureBoardAllowsCardMutations(board);

        card.rename(request.title());
        return cardStore.save(card);
    }

    @Transactional
    public Tarjeta toggleChecklistItem(ToggleChecklistItemRequest request) {
        Objects.requireNonNull(request, "La solicitud no puede ser nula");
        Objects.requireNonNull(request.cardId(), "El id de la tarjeta no puede ser nulo");

        Tarjeta card = cardStore.findById(request.cardId())
                .orElseThrow(() -> new DomainNotFoundException("No existe la tarjeta indicada"));
        if (!(card instanceof TarjetaChecklist checklist)) {
            throw new DomainValidationException("La tarjeta no es de tipo checklist");
        }
        checklist.toggleItem(request.itemIndex(), request.completed());
        return cardStore.save(checklist);
    }

    private void validateCreateCardRequest(CreateCardRequest request) {
        Objects.requireNonNull(request, "La solicitud para crear tarjeta no puede ser nula");
        Objects.requireNonNull(request.listId(), "La lista destino de la tarjeta no puede ser nula");
        Objects.requireNonNull(request.type(), "El tipo de tarjeta es obligatorio");
        UseCaseValidator.requireText(request.title(), "El titulo de la tarjeta es obligatorio");
        UseCaseValidator.requireText(request.description(), "La descripcion de la tarjeta es obligatoria");

        if (request.type() == TipoTarjeta.CHECKLIST && request.checklistItems() == null) {
            throw new DomainValidationException("Las tarjetas checklist deben incluir lista de items");
        }
    }

    private static void ensureBoardAllowsCardMutations(Tablero board) {
        if (board.isBlocked()) {
            throw new DomainForbiddenException("El tablero esta bloqueado y no permite modificar tarjetas");
        }
    }
}
