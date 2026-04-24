package com.tasku.core.infrastructure.api.rest.request;

import com.tasku.core.domain.model.RolComparticion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ShareBoardApiRequest(
        @NotBlank String boardUrl,
        @NotBlank String email,
        @NotNull RolComparticion role
) {
}

