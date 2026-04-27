package com.tasku.core.application.tablero.usecase.dto;

import com.tasku.core.domain.model.TarjetaId;

public record AssignCardLabelRequest(
        TarjetaId cardId,
        String labelName,
        String colorHex
) {
}
