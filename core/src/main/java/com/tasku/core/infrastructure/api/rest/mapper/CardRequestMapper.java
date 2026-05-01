package com.tasku.core.infrastructure.api.rest.mapper;

import com.tasku.core.application.tablero.usecase.dto.AssignCardLabelRequest;
import com.tasku.core.application.tablero.usecase.dto.CompleteCardRequest;
import com.tasku.core.application.tablero.usecase.dto.CreateCardRequest;
import com.tasku.core.application.tablero.usecase.dto.MoveCardRequest;
import com.tasku.core.application.tablero.usecase.dto.ToggleChecklistItemRequest;
import com.tasku.core.domain.model.ElementoChecklist;
import com.tasku.core.domain.model.Email;
import com.tasku.core.domain.model.EtiquetaTarjeta;
import com.tasku.core.domain.model.ListaTableroId;
import com.tasku.core.domain.model.TarjetaId;
import com.tasku.core.infrastructure.api.rest.request.AssignCardLabelApiRequest;
import com.tasku.core.infrastructure.api.rest.request.CardLabelApiRequest;
import com.tasku.core.infrastructure.api.rest.request.ChecklistItemApiRequest;
import com.tasku.core.infrastructure.api.rest.request.CompleteCardApiRequest;
import com.tasku.core.infrastructure.api.rest.request.CreateCardApiRequest;
import com.tasku.core.infrastructure.api.rest.request.MoveCardApiRequest;
import com.tasku.core.infrastructure.api.rest.request.ToggleChecklistItemApiRequest;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class CardRequestMapper {

    public CreateCardRequest toCreateCardRequest(CreateCardApiRequest request) {
        Email author = (request.authorEmail() != null && !request.authorEmail().isBlank())
                ? new Email(request.authorEmail())
                : null;
        return new CreateCardRequest(
                new ListaTableroId(request.listId()),
                request.type(),
                request.title(),
                request.description(),
                toLabelSet(request.labels()),
                toChecklistItems(request.checklistItems()),
                author
        );
    }

    public MoveCardRequest toMoveCardRequest(MoveCardApiRequest request) {
        return new MoveCardRequest(
                new TarjetaId(request.cardId()),
                new ListaTableroId(request.destinationListId()),
                new Email(request.authorEmail())
        );
    }

    public CompleteCardRequest toCompleteCardRequest(CompleteCardApiRequest request) {
        return new CompleteCardRequest(
                new TarjetaId(request.cardId()),
                new Email(request.authorEmail())
        );
    }

    public AssignCardLabelRequest toAssignCardLabelRequest(AssignCardLabelApiRequest request) {
        return new AssignCardLabelRequest(
                new TarjetaId(request.cardId()),
                request.labelName(),
                request.colorHex()
        );
    }

    public ToggleChecklistItemRequest toToggleChecklistItemRequest(ToggleChecklistItemApiRequest request) {
        return new ToggleChecklistItemRequest(
                new TarjetaId(request.cardId()),
                request.itemIndex(),
                request.completed()
        );
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
