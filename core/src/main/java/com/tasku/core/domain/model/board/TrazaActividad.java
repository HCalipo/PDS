package com.tasku.core.domain.model.board;

import com.tasku.core.domain.board.exception.DomainValidationException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public final class TrazaActividad {
    private final UUID id;
    private final String boardUrl;
    private final String authorEmail;
    private final String description;
    private final LocalDateTime date;

    public TrazaActividad(UUID id, String boardUrl, String authorEmail, String description, LocalDateTime date) {
        this.id = Objects.requireNonNull(id, "El id de la traza no puede ser nulo");
        this.boardUrl = validateText(boardUrl, "La url del tablero no puede ser nula ni vacia");
        this.authorEmail = CuentaUsuario.normalizeEmail(authorEmail);
        this.description = validateText(description, "La descripcion de la traza no puede ser nula ni vacia");
        this.date = Objects.requireNonNull(date, "La fecha de la traza no puede ser nula");
    }

    public static TrazaActividad createNow(String boardUrl, String authorEmail, String description) {
        return new TrazaActividad(UUID.randomUUID(), boardUrl, authorEmail, description, LocalDateTime.now());
    }

    public UUID id() {
        return id;
    }

    public String boardUrl() {
        return boardUrl;
    }

    public String authorEmail() {
        return authorEmail;
    }

    public String description() {
        return description;
    }

    public LocalDateTime date() {
        return date;
    }

    private static String validateText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(message);
        }
        return value.trim();
    }
}

