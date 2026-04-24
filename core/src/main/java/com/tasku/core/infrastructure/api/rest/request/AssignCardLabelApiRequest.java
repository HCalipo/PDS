package com.tasku.core.infrastructure.api.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignCardLabelApiRequest(
        @NotNull UUID cardId,
        @NotBlank String labelName,
        @NotBlank String colorHex
) {
}
