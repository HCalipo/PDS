package com.tasku.core.application.tablero.usecase.dto;

import com.tasku.core.domain.model.EtiquetaTarjeta;
import com.tasku.core.domain.model.TipoTarjeta;
import com.tasku.core.domain.model.ElementoChecklist;

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


