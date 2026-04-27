package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainValidationException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public final class TrazaActividad {
    private final UUID id;
    private final TableroUrl boardUrl;
    private final Email authorEmail;
    private final String description;
    private final LocalDateTime date;

    public TrazaActividad(UUID id, TableroUrl boardUrl, Email authorEmail, String description, LocalDateTime date) {
        this.id = Objects.requireNonNull(id, "El id de la traza no puede ser nulo");
        this.boardUrl = Objects.requireNonNull(boardUrl, "La url del tablero no puede ser nula");
        this.authorEmail = Objects.requireNonNull(authorEmail, "El email del autor no puede ser nulo");
        this.description = validateText(description, "La descripcion de la traza no puede ser nula ni vacia");
        this.date = Objects.requireNonNull(date, "La fecha de la traza no puede ser nula");
    }

    public TrazaActividad(UUID id, String boardUrl, String authorEmail, String description, LocalDateTime date) {
        this(id, new TableroUrl(boardUrl), new Email(authorEmail), description, date);
    }

    public static TrazaActividad createNow(String boardUrl, String authorEmail, String description) {
        return new TrazaActividad(UUID.randomUUID(), new TableroUrl(boardUrl), new Email(authorEmail), description, LocalDateTime.now());
    }

    public UUID id() {
        return id;
    }

    public String boardUrl() {
        return boardUrl.value();
    }

    public TableroUrl boardUrlValue() {
        return boardUrl;
    }

    public String authorEmail() {
        return authorEmail.email();
    }

    public Email authorEmailValue() {
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


