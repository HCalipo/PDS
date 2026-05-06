package com.tasku.core.infrastructure.api.rest;

import com.tasku.core.application.tablero.usecase.TarjetaApplicationService;
import com.tasku.core.application.tablero.usecase.dto.DeleteCardRequest;
import com.tasku.core.application.tablero.usecase.dto.RenameCardRequest;
import com.tasku.core.domain.model.ListaTableroId;
import com.tasku.core.domain.model.Tarjeta;
import com.tasku.core.domain.model.TarjetaId;
import com.tasku.core.infrastructure.api.rest.mapper.ApiRestMapper;
import com.tasku.core.infrastructure.api.rest.mapper.CardRequestMapper;
import com.tasku.core.infrastructure.api.rest.request.AssignCardLabelApiRequest;
import com.tasku.core.infrastructure.api.rest.request.CompleteCardApiRequest;
import com.tasku.core.infrastructure.api.rest.request.CreateCardApiRequest;
import com.tasku.core.infrastructure.api.rest.request.MoveCardApiRequest;
import com.tasku.core.infrastructure.api.rest.request.RenameCardApiRequest;
import com.tasku.core.infrastructure.api.rest.request.ToggleChecklistItemApiRequest;
import com.tasku.core.infrastructure.api.rest.response.CardApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/cards")
public class CardRestController {
    private final TarjetaApplicationService cardService;
    private final ApiRestMapper mapper;
    private final CardRequestMapper requestMapper;

    public CardRestController(TarjetaApplicationService cardService,
                              ApiRestMapper mapper,
                              CardRequestMapper requestMapper) {
        this.cardService = cardService;
        this.mapper = mapper;
        this.requestMapper = requestMapper;
    }

    @PostMapping
    public ResponseEntity<CardApiResponse> createCard(@Valid @RequestBody CreateCardApiRequest request) {
        Tarjeta card = cardService.createCard(requestMapper.toCreateCardRequest(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toCardResponse(card));
    }

    @PatchMapping("/move")
    public CardApiResponse moveCard(@Valid @RequestBody MoveCardApiRequest request) {
        Tarjeta card = cardService.moveCard(requestMapper.toMoveCardRequest(request));
        return mapper.toCardResponse(card);
    }

    @PatchMapping("/complete")
    public CardApiResponse completeCard(@Valid @RequestBody CompleteCardApiRequest request) {
        Tarjeta card = cardService.completeCard(requestMapper.toCompleteCardRequest(request));
        return mapper.toCardResponse(card);
    }

    @PatchMapping("/labels")
    public CardApiResponse assignLabel(@Valid @RequestBody AssignCardLabelApiRequest request) {
        Tarjeta card = cardService.assignLabelToCard(requestMapper.toAssignCardLabelRequest(request));
        return mapper.toCardResponse(card);
    }

    @PatchMapping("/checklist/toggle")
    public CardApiResponse toggleChecklistItem(@Valid @RequestBody ToggleChecklistItemApiRequest request) {
        Tarjeta card = cardService.toggleChecklistItem(requestMapper.toToggleChecklistItemRequest(request));
        return mapper.toCardResponse(card);
    }

    @GetMapping("/{cardId}")
    public CardApiResponse getCardById(@PathVariable @NotNull UUID cardId) {
        return mapper.toCardResponse(cardService.getCardById(new TarjetaId(cardId)));
    }

    @GetMapping
    public List<CardApiResponse> findCardsByList(@RequestParam("listId") @NotNull UUID listId) {
        return cardService.findCardsByListId(new ListaTableroId(listId)).stream()
                .map(mapper::toCardResponse)
                .toList();
    }

    @GetMapping("/completed")
    public List<CardApiResponse> getCompletedCards(@RequestParam("boardUrl") @NotBlank String boardUrl) {
        return cardService.getCompletedCardsForBoard(boardUrl).stream()
                .map(mapper::toCardResponse)
                .toList();
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable @NotNull UUID cardId) {
        cardService.deleteCard(new DeleteCardRequest(new TarjetaId(cardId)));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{cardId}")
    public CardApiResponse renameCard(@PathVariable @NotNull UUID cardId,
                                     @Valid @RequestBody RenameCardApiRequest request) {
        Tarjeta card = cardService.renameCard(new RenameCardRequest(new TarjetaId(cardId), request.title()));
        return mapper.toCardResponse(card);
    }
}

