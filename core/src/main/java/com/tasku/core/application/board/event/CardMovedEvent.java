package com.tasku.core.application.board.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record CardMovedEvent(
        UUID cardId,
        UUID sourceListId,
        UUID destinationListId,
        String boardUrl,
        String authorEmail,
        LocalDateTime movedAt
) {
}
