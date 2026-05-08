package com.tasku.core.infrastructure.api.rest.request;

import com.tasku.core.domain.model.RolComparticion;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record JoinBoardApiRequest(
        @NotBlank @Email String email,
        RolComparticion role
) {
}
