package com.tasku.core.infrastructure.api.rest.response;

public record ChecklistItemApiResponse(
        String description,
        boolean completed
) {
}
