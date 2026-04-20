package com.tasku.core.application.board.dto;

import com.tasku.core.domain.model.board.DefinicionListaInicial;

import java.util.List;

public record CreateBoardRequest(
        String ownerEmail,
        String name,
        String color,
        String description,
        List<DefinicionListaInicial> initialLists
) {
}

