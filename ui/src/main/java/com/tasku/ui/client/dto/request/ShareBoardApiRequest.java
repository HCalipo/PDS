package com.tasku.ui.client.dto.request;

import com.tasku.ui.client.dto.RolComparticion;

public record ShareBoardApiRequest(
        String boardUrl,
        String email,
        RolComparticion role,
        String actorEmail
) {
}
