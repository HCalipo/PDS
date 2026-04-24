package com.tasku.core.application.tablero.usecase.dto;

import java.util.UUID;

public record MoveCardRequest(
        UUID cardId,
        UUID destinationListId,
        String authorEmail
) {
}
