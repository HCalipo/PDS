package com.tasku.core.infrastructure.api.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CardLabelApiRequest(
        @NotBlank String name,
        @NotBlank @Pattern(regexp = "^#[0-9a-fA-F]{6}$") String colorHex
) {
}
