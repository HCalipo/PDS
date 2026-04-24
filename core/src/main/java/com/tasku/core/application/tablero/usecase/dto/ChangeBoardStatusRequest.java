package com.tasku.core.application.tablero.usecase.dto;

import com.tasku.core.domain.model.EstadoTablero;

public record ChangeBoardStatusRequest(
        String boardUrl,
        EstadoTablero status
) {
}

