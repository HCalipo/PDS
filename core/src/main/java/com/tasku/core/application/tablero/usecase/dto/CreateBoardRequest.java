package com.tasku.core.application.tablero.usecase.dto;

import com.tasku.core.domain.model.DefinicionListaInicial;
import com.tasku.core.domain.model.Email;

import java.util.List;

public record CreateBoardRequest(
        Email ownerEmail,
        String name,
        String color,
        String description,
        List<DefinicionListaInicial> initialLists
) {
}


