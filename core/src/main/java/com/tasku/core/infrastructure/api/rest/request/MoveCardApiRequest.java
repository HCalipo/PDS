package com.tasku.core.infrastructure.api.rest.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MoveCardApiRequest(
        @NotNull UUID cardId,
        @NotNull UUID destinationListId,
        @NotBlank @Email String authorEmail
) {
}
