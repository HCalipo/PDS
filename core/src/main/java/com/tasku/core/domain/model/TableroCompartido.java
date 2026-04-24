package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainValidationException;

import java.util.Objects;

public final class TableroCompartido {
    private final Long id;
    private final String boardUrl;
    private final String email;
    private final RolComparticion role;

    public TableroCompartido(Long id, String boardUrl, String email, RolComparticion role) {
        this.id = id;
        this.boardUrl = validateText(boardUrl, "La url del tablero compartido no puede ser nula ni vacia");
        this.email = CuentaUsuario.normalizeEmail(email);
        this.role = Objects.requireNonNull(role, "El rol de comparticion no puede ser nulo");
    }

    public Long id() {
        return id;
    }

    public String boardUrl() {
        return boardUrl;
    }

    public String email() {
        return email;
    }

    public RolComparticion role() {
        return role;
    }

    private static String validateText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(message);
        }
        return value.trim();
    }
}


