package com.tasku.ui.client.dto.response;

import java.util.UUID;

public record BoardListApiResponse(
        UUID id,
        String boardUrl,
        String name,
        int cardLimit,
        String colorHex
) {
}