package com.tasku.core.infrastructure.api.rest.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record TraceApiResponse(
        UUID id,
        String boardUrl,
        String authorEmail,
        String description,
        LocalDateTime date
) {
}
