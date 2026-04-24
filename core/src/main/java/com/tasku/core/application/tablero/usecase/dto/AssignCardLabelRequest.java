package com.tasku.core.application.tablero.usecase.dto;

import java.util.UUID;

public record AssignCardLabelRequest(
        UUID cardId,
        String labelName,
        String colorHex
) {
}
