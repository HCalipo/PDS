package com.tasku.core.application.tablero.usecase.dto;

public record CreateListRequest(
        String boardUrl,
        String name,
        int cardLimit
) {
}