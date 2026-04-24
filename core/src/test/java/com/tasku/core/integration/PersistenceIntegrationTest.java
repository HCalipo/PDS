package com.tasku.core.integration;

import com.tasku.core.application.tablero.usecase.TrazaActividadUseCaseService;
import com.tasku.core.application.tablero.usecase.TableroUseCaseService;
import com.tasku.core.application.tablero.usecase.dto.CreateBoardRequest;
import com.tasku.core.application.tablero.usecase.dto.CreateCardRequest;
import com.tasku.core.application.tablero.usecase.dto.MoveCardRequest;
import com.tasku.core.application.tablero.usecase.dto.RegisterTraceRequest;
import com.tasku.core.domain.board.exception.DomainConflictException;
import com.tasku.core.domain.model.Tablero;
import com.tasku.core.domain.model.ListaTablero;
import com.tasku.core.domain.model.Tarjeta;
import com.tasku.core.domain.model.EtiquetaTarjeta;
import com.tasku.core.domain.model.TipoTarjeta;
import com.tasku.core.domain.model.TarjetaChecklist;
import com.tasku.core.domain.model.ElementoChecklist;
import com.tasku.core.domain.model.DefinicionListaInicial;
import com.tasku.core.infrastructure.bootstrap.CoreApplication;
import com.tasku.core.infrastructure.persistence.jpa.repository.SpringDataListaTableroRepository;
import com.tasku.core.infrastructure.persistence.jpa.repository.SpringDataTableroRepository;
import com.tasku.core.infrastructure.persistence.jpa.repository.SpringDataTarjetaRepository;
import com.tasku.core.infrastructure.persistence.jpa.repository.SpringDataTrazaRepository;
import com.tasku.core.infrastructure.persistence.jpa.repository.SpringDataUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = CoreApplication.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:file:./target/testdb/tasku-integration;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.jpa.hibernate.ddl-auto=update",
        "tasku.traces.retention-days=30",
        "tasku.traces.compaction-cron=0 0 3 * * *"
})
class PersistenceIntegrationTest {

    @Autowired
    private TableroUseCaseService boardService;

    @Autowired
    private TrazaActividadUseCaseService traceService;

    @Autowired
    private SpringDataTrazaRepository traceRepository;

    @Autowired
    private SpringDataTarjetaRepository cardRepository;

    @Autowired
    private SpringDataListaTableroRepository listRepository;

    @Autowired
    private SpringDataTableroRepository boardRepository;

    @Autowired
    private SpringDataUsuarioRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        traceRepository.deleteAll();
        cardRepository.deleteAll();
        listRepository.deleteAll();
        boardRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void integration_createBoard_and_retrieveBoard() {
        CreateBoardRequest request = new CreateBoardRequest(
                "Owner@Tasku.dev",
                "Backlog Producto",
                "#0057B8",
                "Tablero principal",
                List.of(
                        new DefinicionListaInicial("TODO", 5),
                        new DefinicionListaInicial("DOING", 5)
                )
        );

        Tablero created = boardService.createBoard(request);
        Tablero loaded = boardService.getBoardByUrl(created.url());

        assertEquals(created.url(), loaded.url());
        assertEquals("owner@tasku.dev", loaded.ownerEmail());
        assertEquals(2, loaded.lists().size());
        assertEquals(1, boardService.findBoardsByOwnerEmail("OWNER@TASKU.DEV").size());
    }

    @Test
    void integration_createCard_and_listByList() {
        Tablero board = createBoardWithTwoLists("cards-owner@tasku.dev", "Tablero Cards", 3, 3);
        ListaTablero targetList = board.lists().get(0);

        boardService.createCard(new CreateCardRequest(
                targetList.id(),
                TipoTarjeta.TAREA,
                "Implementar login",
                "Integrar backend",
                Set.of(new EtiquetaTarjeta("backend", "#0EA5E9")),
                List.of()
        ));

        Tarjeta checklistCard = boardService.createCard(new CreateCardRequest(
                targetList.id(),
                TipoTarjeta.CHECKLIST,
                "Checklist release",
                "Validar despliegue",
                Set.of(new EtiquetaTarjeta("release", "#16A34A")),
                List.of(
                        new ElementoChecklist("Build", true),
                        new ElementoChecklist("Deploy", false)
                )
        ));

        List<Tarjeta> cards = boardService.findCardsByListId(targetList.id());

        assertEquals(2, cards.size());
        assertTrue(cards.stream().anyMatch(card -> card.id().equals(checklistCard.id())));
        Tarjeta loadedChecklist = cards.stream().filter(card -> card.id().equals(checklistCard.id())).findFirst().orElseThrow();
        TarjetaChecklist typedChecklist = assertInstanceOf(TarjetaChecklist.class, loadedChecklist);
        assertEquals(2, typedChecklist.items().size());
    }

    @Test
    void integration_moveCard_and_verifyDestinationList() {
        Tablero board = createBoardWithTwoLists("move-owner@tasku.dev", "Tablero Move", 5, 5);
        ListaTablero source = board.lists().get(0);
        ListaTablero destination = board.lists().get(1);

        Tarjeta card = boardService.createCard(new CreateCardRequest(
                source.id(),
                TipoTarjeta.TAREA,
                "Tarjeta mover",
                "Mover entre listas",
                Set.of(),
                List.of()
        ));

        boardService.moveCard(new MoveCardRequest(card.id(), destination.id(), "move-owner@tasku.dev"));

        assertTrue(boardService.findCardsByListId(destination.id()).stream().anyMatch(saved -> saved.id().equals(card.id())));
        assertFalse(boardService.findCardsByListId(source.id()).stream().anyMatch(saved -> saved.id().equals(card.id())));
    }

    @Test
    void integration_moveCard_generatesTrace() {
        Tablero board = createBoardWithTwoLists("trace-owner@tasku.dev", "Tablero Trace", 5, 5);
        ListaTablero source = board.lists().get(0);
        ListaTablero destination = board.lists().get(1);

        Tarjeta card = boardService.createCard(new CreateCardRequest(
                source.id(),
                TipoTarjeta.TAREA,
                "Tarjeta traza",
                "Genera actividad",
                Set.of(),
                List.of()
        ));

        boardService.moveCard(new MoveCardRequest(card.id(), destination.id(), "trace-owner@tasku.dev"));

        var traces = traceService.getBoardTraces(board.url());
        assertEquals(1, traces.size());
        assertTrue(traces.getFirst().description().contains(card.id().toString()));
    }

    @Test
    void integration_traceCompaction_deletesOldTraces() {
        Tablero board = createBoardWithTwoLists("compact-owner@tasku.dev", "Tablero Compact", 3, 3);

        traceService.registerTrace(new RegisterTraceRequest(
                board.url(),
                "compact-owner@tasku.dev",
                "Traza antigua",
                LocalDateTime.now().minusDays(45)
        ));

        traceService.registerTrace(new RegisterTraceRequest(
                board.url(),
                "compact-owner@tasku.dev",
                "Traza reciente",
                LocalDateTime.now().minusDays(5)
        ));

        long removed = traceService.compactOlderThan(LocalDateTime.now().minusDays(30));

        assertEquals(1, removed);
        assertEquals(1, traceService.getBoardTraces(board.url()).size());
        assertEquals("Traza reciente", traceService.getBoardTraces(board.url()).getFirst().description());
    }

    @Test
    void negative_duplicateBoardForSameOwner_isRejected() {
        boardService.createBoard(new CreateBoardRequest(
                "dup-owner@tasku.dev",
                "Roadmap",
                "#111827",
                "Primer tablero",
                List.of(new DefinicionListaInicial("TODO", 3))
        ));

        assertThrows(DomainConflictException.class, () -> boardService.createBoard(new CreateBoardRequest(
                "DUP-OWNER@TASKU.DEV",
                "roadmap",
                "#111827",
                "Duplicado",
                List.of(new DefinicionListaInicial("TODO", 3))
        )));
    }

    @Test
    void negative_createOrMoveCard_whenListLimitReached_isRejected() {
        Tablero board = createBoardWithTwoLists("limit-owner@tasku.dev", "Tablero Limits", 1, 1);
        ListaTablero source = board.lists().get(0);
        ListaTablero destination = board.lists().get(1);

        Tarjeta sourceCard = boardService.createCard(new CreateCardRequest(
                source.id(),
                TipoTarjeta.TAREA,
                "Source full",
                "Ocupa origen",
                Set.of(),
                List.of()
        ));

        boardService.createCard(new CreateCardRequest(
                destination.id(),
                TipoTarjeta.TAREA,
                "Destination full",
                "Ocupa destino",
                Set.of(),
                List.of()
        ));

        assertThrows(DomainConflictException.class, () -> boardService.createCard(new CreateCardRequest(
                source.id(),
                TipoTarjeta.TAREA,
                "Extra",
                "No cabe",
                Set.of(),
                List.of()
        )));

        assertThrows(DomainConflictException.class, () -> boardService.moveCard(
                new MoveCardRequest(sourceCard.id(), destination.id(), "limit-owner@tasku.dev")
        ));
    }

    private Tablero createBoardWithTwoLists(String ownerEmail, String name, int firstLimit, int secondLimit) {
        return boardService.createBoard(new CreateBoardRequest(
                ownerEmail,
                name,
                "#0369A1",
                "Descripcion de prueba",
                List.of(
                        new DefinicionListaInicial("A", firstLimit),
                        new DefinicionListaInicial("B", secondLimit)
                )
        ));
    }
}


