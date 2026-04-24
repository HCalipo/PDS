package com.tasku.core.application.tablero.usecase.dto;

import com.tasku.core.domain.model.DefinicionListaInicial;

import java.util.List;

public record CreateBoardRequest(
        String ownerEmail,
        String name,
        String color,
        String description,
        List<DefinicionListaInicial> initialLists
) {
}


