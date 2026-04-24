package com.tasku.core.infrastructure.api.rest.response;

import com.tasku.core.domain.model.RolComparticion;

public record BoardShareApiResponse(
        Long id,
        String boardUrl,
        String email,
        RolComparticion role
) {
}

