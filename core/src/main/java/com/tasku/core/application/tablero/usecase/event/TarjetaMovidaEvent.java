package com.tasku.core.application.tablero.usecase.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record TarjetaMovidaEvent(
        UUID cardId,
        UUID sourceListId,
        UUID destinationListId,
        String boardUrl,
        String authorEmail,
        LocalDateTime movedAt
) {
}
