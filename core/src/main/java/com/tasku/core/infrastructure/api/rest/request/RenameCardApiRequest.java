package com.tasku.core.infrastructure.api.rest.request;

import jakarta.validation.constraints.NotBlank;

public record RenameCardApiRequest(
        @NotBlank String title
) {
}
