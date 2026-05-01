package com.tasku.core.infrastructure.api.rest.response;

import java.time.LocalDateTime;

public record UsuarioApiResponse(String email, LocalDateTime registrationDate) {}
