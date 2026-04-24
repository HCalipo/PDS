package com.tasku.core.application.tablero.usecase.dto;

import java.util.UUID;

public record CompleteCardRequest(
        UUID cardId,
        String authorEmail
) {
}
