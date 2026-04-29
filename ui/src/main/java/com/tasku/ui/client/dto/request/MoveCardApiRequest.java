package com.tasku.ui.client.dto.request;

import java.util.UUID;

public record MoveCardApiRequest(
        UUID cardId,
        UUID destinationListId,
        String authorEmail
) {
}
