package com.tasku.core.infrastructure.api.rest.request;

import jakarta.validation.constraints.NotBlank;

public record ChecklistItemApiRequest(
        @NotBlank String description,
        boolean completed
) {
}
