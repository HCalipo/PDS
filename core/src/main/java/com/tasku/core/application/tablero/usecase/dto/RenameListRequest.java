package com.tasku.core.application.tablero.usecase.dto;

import java.util.UUID;

public record RenameListRequest(
        String boardUrl,
        UUID listId,
        String name
) {
}