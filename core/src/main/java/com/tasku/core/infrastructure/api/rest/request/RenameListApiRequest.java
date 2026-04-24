package com.tasku.core.infrastructure.api.rest.request;

import jakarta.validation.constraints.NotBlank;

public record RenameListApiRequest(
        @NotBlank String boardUrl,
        @NotBlank String name
) {
}
