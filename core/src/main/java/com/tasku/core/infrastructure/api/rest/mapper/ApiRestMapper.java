package com.tasku.core.infrastructure.api.rest.mapper;

import com.tasku.core.domain.model.EtiquetaTarjeta;
import com.tasku.core.domain.model.ElementoChecklist;
import com.tasku.core.domain.model.ListaTablero;
import com.tasku.core.domain.model.Tablero;
import com.tasku.core.domain.model.TableroCompartido;
import com.tasku.core.domain.model.Tarjeta;
import com.tasku.core.domain.model.TrazaActividad;
import com.tasku.core.infrastructure.api.rest.response.BoardApiResponse;
import com.tasku.core.infrastructure.api.rest.response.BoardListApiResponse;
import com.tasku.core.infrastructure.api.rest.response.BoardShareApiResponse;
import com.tasku.core.infrastructure.api.rest.response.CardApiResponse;
import com.tasku.core.infrastructure.api.rest.response.CardLabelApiResponse;
import com.tasku.core.infrastructure.api.rest.response.ChecklistItemApiResponse;
import com.tasku.core.infrastructure.api.rest.response.TraceApiResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class ApiRestMapper {

    public BoardApiResponse toBoardResponse(Tablero board) {
        List<BoardListApiResponse> lists = board.lists().stream()
                .map(this::toBoardListResponse)
                .toList();

        Set<BoardShareApiResponse> shares = board.sharedWith().stream()
                .map(this::toBoardShareResponse)
                .collect(java.util.stream.Collectors.toSet());

        return new BoardApiResponse(
                board.url(),
                board.name(),
                board.ownerEmail(),
                board.color(),
                board.description(),
                board.status(),
                lists,
                shares
        );
    }

    public CardApiResponse toCardResponse(Tarjeta card) {
        Set<CardLabelApiResponse> labels = card.labels().stream()
                .map(this::toCardLabelResponse)
                .collect(java.util.stream.Collectors.toSet());

        List<ChecklistItemApiResponse> checklistItems = card.checklistItems().stream()
                .map(this::toChecklistItemResponse)
                .toList();

        return new CardApiResponse(
                card.id(),
                card.listId(),
                card.type(),
                card.title(),
                card.description(),
                card.archived(),
                labels,
                checklistItems
        );
    }

    public TraceApiResponse toTraceResponse(TrazaActividad trace) {
        return new TraceApiResponse(
                trace.id(),
                trace.boardUrl(),
                trace.authorEmail(),
                trace.description(),
                trace.date()
        );
    }

    private BoardListApiResponse toBoardListResponse(ListaTablero list) {
        return new BoardListApiResponse(
                list.id(),
                list.boardUrl(),
                list.name(),
                list.cardLimit(),
                list.colorHex()
        );
    }

    private BoardShareApiResponse toBoardShareResponse(TableroCompartido share) {
        return new BoardShareApiResponse(
                share.id(),
                share.boardUrl(),
                share.email(),
                share.role()
        );
    }

    private CardLabelApiResponse toCardLabelResponse(EtiquetaTarjeta label) {
        return new CardLabelApiResponse(label.name(), label.colorHex());
    }

    private ChecklistItemApiResponse toChecklistItemResponse(ElementoChecklist item) {
        return new ChecklistItemApiResponse(item.description(), item.completed());
    }
}

