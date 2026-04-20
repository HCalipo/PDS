package com.tasku.core.application.board.dto;

import com.tasku.core.domain.model.board.EtiquetaTarjeta;
import com.tasku.core.domain.model.board.TipoTarjeta;
import com.tasku.core.domain.model.board.ElementoChecklist;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record CreateCardRequest(
        UUID listId,
        TipoTarjeta type,
        String title,
        String description,
        Set<EtiquetaTarjeta> labels,
        List<ElementoChecklist> checklistItems
) {
}

