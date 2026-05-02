package com.tasku.core.application.tablero.usecase.dto;

import com.tasku.core.domain.model.TableroUrl;

public record CreateListRequest(
        TableroUrl boardUrl,
        String name,
        int cardLimit,
        String colorHex
) {
}