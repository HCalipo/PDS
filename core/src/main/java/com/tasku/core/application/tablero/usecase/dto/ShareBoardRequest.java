package com.tasku.core.application.tablero.usecase.dto;

import com.tasku.core.domain.model.RolComparticion;

public record ShareBoardRequest(
        String boardUrl,
        String email,
        RolComparticion role
) {
}


