package com.tasku.ui.client.dto.request;

import com.tasku.ui.client.dto.RolComparticion;

public record JoinBoardApiRequest(
    String email,
    RolComparticion role
) {}
