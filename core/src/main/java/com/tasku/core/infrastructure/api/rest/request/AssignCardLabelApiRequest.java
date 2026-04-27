package com.tasku.core.infrastructure.api.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record AssignCardLabelApiRequest(
        @NotNull UUID cardId,
        @NotBlank String labelName,
        @NotBlank @Pattern(regexp = "^#[0-9a-fA-F]{6}$") String colorHex
) {
}
