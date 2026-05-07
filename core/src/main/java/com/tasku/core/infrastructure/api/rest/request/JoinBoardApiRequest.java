package com.tasku.core.infrastructure.api.rest.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record JoinBoardApiRequest(
        @NotBlank @Email String email
) {
}
