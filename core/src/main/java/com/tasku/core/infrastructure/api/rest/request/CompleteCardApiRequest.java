package com.tasku.core.infrastructure.api.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CompleteCardApiRequest(
        @NotNull UUID cardId,
        @NotBlank String authorEmail
) {
}
