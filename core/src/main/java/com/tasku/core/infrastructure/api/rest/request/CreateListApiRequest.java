package com.tasku.core.infrastructure.api.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateListApiRequest(
        @NotBlank String boardUrl,
        @NotBlank String name,
        @Positive int cardLimit
) {
}
