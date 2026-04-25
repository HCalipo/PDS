package com.tasku.ui.client.dto.response;

import com.tasku.ui.client.dto.RolComparticion;

public record BoardShareApiResponse(
        Long id,
        String boardUrl,
        String email,
        RolComparticion role
) {
}