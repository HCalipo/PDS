package com.tasku.core.application.board.dto;

import java.util.UUID;

public record MoveCardRequest(
        UUID cardId,
        UUID destinationListId,
        String authorEmail
) {
}
