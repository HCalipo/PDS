package com.tasku.core.infrastructure.api.rest.response;

import com.tasku.core.domain.model.EstadoTablero;

import java.util.List;
import java.util.Set;

public record BoardApiResponse(
        String url,
        String name,
        String ownerEmail,
        String color,
        String description,
        EstadoTablero status,
        List<BoardListApiResponse> lists,
        Set<BoardShareApiResponse> sharedWith
) {
}

