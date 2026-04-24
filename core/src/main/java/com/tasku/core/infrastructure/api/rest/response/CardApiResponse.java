package com.tasku.core.infrastructure.api.rest.response;

import com.tasku.core.domain.model.TipoTarjeta;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record CardApiResponse(
        UUID id,
        UUID listId,
        TipoTarjeta type,
        String title,
        String description,
        boolean archived,
        Set<CardLabelApiResponse> labels,
        List<ChecklistItemApiResponse> checklistItems
) {
}

