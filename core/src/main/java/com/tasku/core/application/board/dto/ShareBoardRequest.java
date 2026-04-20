package com.tasku.core.application.board.dto;

import com.tasku.core.domain.model.board.RolComparticion;

public record ShareBoardRequest(
        String boardUrl,
        String email,
        RolComparticion role
) {
}

