package com.tasku.ui.client.dto.request;

import com.tasku.ui.client.dto.TipoTarjeta;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record CreateCardApiRequest(
        UUID listId,
        TipoTarjeta type,
        String title,
        String description,
        Set<CardLabelApiRequest> labels,
        List<ChecklistItemApiRequest> checklistItems
) {
}