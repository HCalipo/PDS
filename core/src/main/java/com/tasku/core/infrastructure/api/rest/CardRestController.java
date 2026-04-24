package com.tasku.core.infrastructure.api.rest;

import com.tasku.core.application.tablero.usecase.TableroUseCaseService;
import com.tasku.core.application.tablero.usecase.dto.AssignCardLabelRequest;
import com.tasku.core.application.tablero.usecase.dto.CompleteCardRequest;
import com.tasku.core.application.tablero.usecase.dto.CreateCardRequest;
import com.tasku.core.application.tablero.usecase.dto.MoveCardRequest;
import com.tasku.core.domain.model.ElementoChecklist;
import com.tasku.core.domain.model.EtiquetaTarjeta;
import com.tasku.core.domain.model.Tarjeta;
import com.tasku.core.infrastructure.api.rest.mapper.ApiRestMapper;
import com.tasku.core.infrastructure.api.rest.request.AssignCardLabelApiRequest;
import com.tasku.core.infrastructure.api.rest.request.CardLabelApiRequest;
import com.tasku.core.infrastructure.api.rest.request.ChecklistItemApiRequest;
import com.tasku.core.infrastructure.api.rest.request.CompleteCardApiRequest;
import com.tasku.core.infrastructure.api.rest.request.CreateCardApiRequest;
import com.tasku.core.infrastructure.api.rest.request.MoveCardApiRequest;
import com.tasku.core.infrastructure.api.rest.response.CardApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/cards")
public class CardRestController {
    private final TableroUseCaseService boardService;
    private final ApiRestMapper mapper;

    public CardRestController(TableroUseCaseService boardService, ApiRestMapper mapper) {
        this.boardService = boardService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<CardApiResponse> createCard(@Valid @RequestBody CreateCardApiRequest request) {
        Tarjeta card = boardService.createCard(new CreateCardRequest(
                request.listId(),
                request.type(),
                request.title(),
                request.description(),
                toLabelSet(request.labels()),
                toChecklistItems(request.checklistItems())
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toCardResponse(card));
    }

    @PatchMapping("/move")
    public CardApiResponse moveCard(@Valid @RequestBody MoveCardApiRequest request) {
        Tarjeta card = boardService.moveCard(new MoveCardRequest(
                request.cardId(),
                request.destinationListId(),
                request.authorEmail()
        ));
        return mapper.toCardResponse(card);
    }

    @PatchMapping("/complete")
    public CardApiResponse completeCard(@Valid @RequestBody CompleteCardApiRequest request) {
        Tarjeta card = boardService.completeCard(new CompleteCardRequest(
                request.cardId(),
                request.authorEmail()
        ));
        return mapper.toCardResponse(card);
    }

    @PatchMapping("/labels")
    public CardApiResponse assignLabel(@Valid @RequestBody AssignCardLabelApiRequest request) {
        Tarjeta card = boardService.assignLabelToCard(new AssignCardLabelRequest(
                request.cardId(),
                request.labelName(),
                request.colorHex()
        ));
        return mapper.toCardResponse(card);
    }

    @GetMapping("/{cardId}")
    public CardApiResponse getCardById(@PathVariable UUID cardId) {
        return mapper.toCardResponse(boardService.getCardById(cardId));
    }

    @GetMapping
    public List<CardApiResponse> findCardsByList(@RequestParam("listId") UUID listId) {
        return boardService.findCardsByListId(listId).stream()
                .map(mapper::toCardResponse)
                .toList();
    }

    private static Set<EtiquetaTarjeta> toLabelSet(Set<CardLabelApiRequest> labels) {
        if (labels == null || labels.isEmpty()) {
            return Set.of();
        }
        Set<EtiquetaTarjeta> mapped = new LinkedHashSet<>();
        for (CardLabelApiRequest label : labels) {
            mapped.add(new EtiquetaTarjeta(label.name(), label.colorHex()));
        }
        return mapped;
    }

    private static List<ElementoChecklist> toChecklistItems(List<ChecklistItemApiRequest> items) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }
        return items.stream()
                .map(item -> new ElementoChecklist(item.description(), item.completed()))
                .toList();
    }
}

