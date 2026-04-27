package com.tasku.core.infrastructure.api.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RenameListApiRequest(
        @NotBlank @Pattern(regexp = "^tasku://tablero/[0-9a-fA-F\\-]{36}$") String boardUrl,
        @NotBlank String name
) {
}
