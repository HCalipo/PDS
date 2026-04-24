package com.tasku.core.infrastructure.api.rest.request;

import com.tasku.core.domain.model.TipoTarjeta;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record CreateCardApiRequest(
        @NotNull UUID listId,
        @NotNull TipoTarjeta type,
        @NotBlank String title,
        @NotBlank String description,
        Set<@Valid CardLabelApiRequest> labels,
        List<@Valid ChecklistItemApiRequest> checklistItems
) {
}

