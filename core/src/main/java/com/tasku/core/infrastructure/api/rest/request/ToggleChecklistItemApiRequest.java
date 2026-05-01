package com.tasku.core.infrastructure.api.rest.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ToggleChecklistItemApiRequest(
        @NotNull UUID cardId,
        @Min(0) int itemIndex,
        boolean completed
) {}
