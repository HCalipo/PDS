package com.tasku.core.infrastructure.api.rest.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateBoardApiRequest(
        @NotBlank String ownerEmail,
        @NotBlank String name,
        @NotBlank String color,
        @NotBlank String description,
        List<@Valid InitialListApiRequest> initialLists
) {
}
