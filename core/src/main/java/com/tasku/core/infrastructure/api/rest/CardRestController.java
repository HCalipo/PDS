package com.tasku.core.infrastructure.api.rest;

import com.tasku.core.application.tablero.usecase.TableroUseCaseService;
import com.tasku.core.domain.model.ListaTableroId;
import com.tasku.core.domain.model.Tarjeta;
import com.tasku.core.domain.model.TarjetaId;
import com.tasku.core.infrastructure.api.rest.mapper.ApiRestMapper;
import com.tasku.core.infrastructure.api.rest.mapper.CardRequestMapper;
import com.tasku.core.infrastructure.api.rest.request.AssignCardLabelApiRequest;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cards")
public class CardRestController {
    private final TableroUseCaseService boardService;
    private final ApiRestMapper mapper;
    private final CardRequestMapper requestMapper;

    public CardRestController(TableroUseCaseService boardService,
                              ApiRestMapper mapper,
                              CardRequestMapper requestMapper) {
        this.boardService = boardService;
        this.mapper = mapper;
        this.requestMapper = requestMapper;
    }

    @PostMapping
    public ResponseEntity<CardApiResponse> createCard(@Valid @RequestBody CreateCardApiRequest request) {
        Tarjeta card = boardService.createCard(requestMapper.toCreateCardRequest(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toCardResponse(card));
    }

    @PatchMapping("/move")
    public CardApiResponse moveCard(@Valid @RequestBody MoveCardApiRequest request) {
        Tarjeta card = boardService.moveCard(requestMapper.toMoveCardRequest(request));
        return mapper.toCardResponse(card);
    }

    @PatchMapping("/complete")
    public CardApiResponse completeCard(@Valid @RequestBody CompleteCardApiRequest request) {
        Tarjeta card = boardService.completeCard(requestMapper.toCompleteCardRequest(request));
        return mapper.toCardResponse(card);
    }

    @PatchMapping("/labels")
    public CardApiResponse assignLabel(@Valid @RequestBody AssignCardLabelApiRequest request) {
        Tarjeta card = boardService.assignLabelToCard(requestMapper.toAssignCardLabelRequest(request));
        return mapper.toCardResponse(card);
    }

    @GetMapping("/{cardId}")
    public CardApiResponse getCardById(@PathVariable UUID cardId) {
        return mapper.toCardResponse(boardService.getCardById(new TarjetaId(cardId)));
    }

    @GetMapping
    public List<CardApiResponse> findCardsByList(@RequestParam("listId") UUID listId) {
        return boardService.findCardsByListId(new ListaTableroId(listId)).stream()
                .map(mapper::toCardResponse)
                .toList();
    }
}

