package com.tasku.core.application.tablero.usecase.dto;

import java.time.LocalDateTime;

public record RegisterTraceRequest(
        String boardUrl,
        String authorEmail,
        String description,
        LocalDateTime date
) {
}
