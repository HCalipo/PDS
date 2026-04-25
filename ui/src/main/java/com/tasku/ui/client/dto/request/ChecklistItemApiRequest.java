package com.tasku.ui.client.dto.request;

public record ChecklistItemApiRequest(
        String description,
        boolean completed
) {
}