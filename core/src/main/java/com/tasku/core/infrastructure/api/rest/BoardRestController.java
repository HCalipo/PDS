package com.tasku.core.infrastructure.api.rest;

import com.tasku.core.application.tablero.usecase.TableroUseCaseService;
import com.tasku.core.application.tablero.usecase.dto.ChangeBoardStatusRequest;
import com.tasku.core.application.tablero.usecase.dto.CreateBoardRequest;
import com.tasku.core.application.tablero.usecase.dto.CreateListRequest;
import com.tasku.core.application.tablero.usecase.dto.RenameListRequest;
import com.tasku.core.application.tablero.usecase.dto.ShareBoardRequest;
import com.tasku.core.application.tablero.usecase.dto.JoinBoardApiRequest;
import com.tasku.core.domain.model.DefinicionListaInicial;
import com.tasku.core.domain.model.Email;
import com.tasku.core.domain.model.ListaTableroId;
import com.tasku.core.domain.model.Tablero;
import com.tasku.core.domain.model.TableroUrl;
import com.tasku.core.infrastructure.api.rest.mapper.ApiRestMapper;
import com.tasku.core.infrastructure.api.rest.request.ChangeBoardStatusApiRequest;
import com.tasku.core.infrastructure.api.rest.request.CreateBoardApiRequest;
import com.tasku.core.infrastructure.api.rest.request.CreateListApiRequest;
import com.tasku.core.infrastructure.api.rest.request.InitialListApiRequest;
import com.tasku.core.infrastructure.api.rest.request.RenameListApiRequest;
import com.tasku.core.infrastructure.api.rest.request.ShareBoardApiRequest;
import com.tasku.core.infrastructure.api.rest.response.BoardApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/api/boards")
public class BoardRestController {
    private final TableroUseCaseService tableroUseCaseService;
    private final TableroUseCaseService boardService;
    private final ApiRestMapper mapper;

    public BoardRestController(TableroUseCaseService boardService, ApiRestMapper mapper) {
        this.boardService = boardService;
        this.mapper = mapper;
        this.tableroUseCaseService = boardService;
    }

    @PostMapping
    public ResponseEntity<BoardApiResponse> createBoard(@Valid @RequestBody CreateBoardApiRequest request) {
        List<DefinicionListaInicial> initialLists = null;
        if (request.initialLists() != null) {
            initialLists = request.initialLists().stream()
                    .map(this::toInitialListDefinition)
                    .toList();
        }

        Tablero board = boardService.createBoard(new CreateBoardRequest(
                new Email(request.ownerEmail()),
                request.name(),
                request.color(),
                request.description(),
                initialLists
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toBoardResponse(board));
    }

    @GetMapping("/by-url")
    public BoardApiResponse getBoardByUrl(@RequestParam("url") @NotBlank @Pattern(regexp = "^tasku://tablero/[0-9a-fA-F\\-]{36}$") String boardUrl) {
        return mapper.toBoardResponse(boardService.getBoardByUrl(new TableroUrl(boardUrl)));
    }

    @GetMapping("/owned")
    public List<BoardApiResponse> findBoardsByOwner(@RequestParam("ownerEmail") @NotBlank @jakarta.validation.constraints.Email String ownerEmail) {
        return boardService.findBoardsByOwnerEmail(new Email(ownerEmail)).stream()
                .map(mapper::toBoardResponse)
                .toList();
    }

    @GetMapping("/shared")
    public List<BoardApiResponse> findBoardsSharedWith(@RequestParam("email") @NotBlank @jakarta.validation.constraints.Email String email) {
        return boardService.findBoardsSharedWithEmail(new Email(email)).stream()
                .map(mapper::toBoardResponse)
                .toList();
    }

    @PostMapping("/share")
    public BoardApiResponse shareBoard(@Valid @RequestBody ShareBoardApiRequest request) {
        Tablero board = boardService.shareBoard(new ShareBoardRequest(
                new TableroUrl(request.boardUrl()),
                new Email(request.email()),
                request.role()
        ));
        return mapper.toBoardResponse(board);
    }

    @PostMapping("/lists")
    public ResponseEntity<BoardApiResponse> createList(@Valid @RequestBody CreateListApiRequest request) {
        Tablero board = boardService.createList(new CreateListRequest(
                new TableroUrl(request.boardUrl()),
                request.name(),
                request.cardLimit(),
                request.colorHex()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toBoardResponse(board));
    }

    @PatchMapping("/lists/{listId}")
    public BoardApiResponse renameList(@PathVariable UUID listId, @Valid @RequestBody RenameListApiRequest request) {
        Tablero board = boardService.renameList(new RenameListRequest(
                new TableroUrl(request.boardUrl()),
                new ListaTableroId(listId),
                request.name()
        ));
        return mapper.toBoardResponse(board);
    }

    @PatchMapping("/status")
    public BoardApiResponse changeBoardStatus(@Valid @RequestBody ChangeBoardStatusApiRequest request) {
        Tablero board = boardService.changeBoardStatus(new ChangeBoardStatusRequest(
                new TableroUrl(request.boardUrl()),
                request.status()
        ));
        return mapper.toBoardResponse(board);
    }

    @PostMapping("/{boardUrl}/join")
    public ResponseEntity<Void> joinBoard(
            @PathVariable String boardUrl,
            @RequestBody JoinBoardApiRequest request) {

        tableroUseCaseService.joinBoard(boardUrl, request.email());
        return ResponseEntity.ok().build(); 
    }

    private DefinicionListaInicial toInitialListDefinition(InitialListApiRequest request) {
        return new DefinicionListaInicial(request.name(), request.cardLimit());
    }
}

