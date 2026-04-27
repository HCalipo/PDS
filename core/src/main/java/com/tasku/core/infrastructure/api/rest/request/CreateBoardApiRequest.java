package com.tasku.core.infrastructure.api.rest.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record CreateBoardApiRequest(
        @NotBlank @Email String ownerEmail,
        @NotBlank String name,
        @NotBlank @Pattern(regexp = "^#[0-9a-fA-F]{6}$") String color,
        @NotBlank String description,
        List<@Valid InitialListApiRequest> initialLists
) {
}
