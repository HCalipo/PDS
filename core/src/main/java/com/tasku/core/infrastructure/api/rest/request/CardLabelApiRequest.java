package com.tasku.core.infrastructure.api.rest.request;

import jakarta.validation.constraints.NotBlank;

public record CardLabelApiRequest(
        @NotBlank String name,
        @NotBlank String colorHex
) {
}
