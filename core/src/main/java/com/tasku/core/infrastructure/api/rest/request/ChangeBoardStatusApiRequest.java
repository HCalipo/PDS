package com.tasku.core.infrastructure.api.rest.request;

import com.tasku.core.domain.model.EstadoTablero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChangeBoardStatusApiRequest(
        @NotBlank String boardUrl,
        @NotNull EstadoTablero status
) {
}

