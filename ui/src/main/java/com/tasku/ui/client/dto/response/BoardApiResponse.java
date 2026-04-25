package com.tasku.ui.client.dto.response;

import com.tasku.ui.client.dto.EstadoTablero;

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