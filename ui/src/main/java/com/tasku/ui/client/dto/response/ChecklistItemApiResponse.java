package com.tasku.ui.client.dto.response;

public record ChecklistItemApiResponse(
        String description,
        boolean completed
) {
}