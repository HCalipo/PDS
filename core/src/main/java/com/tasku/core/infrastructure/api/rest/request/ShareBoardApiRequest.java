package com.tasku.core.infrastructure.api.rest.request;

import com.tasku.core.domain.model.RolComparticion;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ShareBoardApiRequest(
        @NotBlank @Pattern(regexp = "^tasku://tablero/[0-9a-fA-F\\-]{36}$") String boardUrl,
        @NotBlank @Email String email,
        @NotNull RolComparticion role
) {
}

