package com.tasku.core.infrastructure.api.rest.response;

import java.util.UUID;

public record BoardListApiResponse(
        UUID id,
        String boardUrl,
        String name,
        int cardLimit
) {
}
