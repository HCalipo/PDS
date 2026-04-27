package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainValidationException;

import java.util.UUID;

public record TableroUrl(String value) {
    private static final String PREFIX = "tasku://tablero/";

    public TableroUrl {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException("La url del tablero no puede ser nula ni vacia");
        }

        String normalized = value.trim();
        String token = normalized.startsWith(PREFIX) ? normalized.substring(PREFIX.length()) : normalized;
        UUID uuid;
        try {
            uuid = UUID.fromString(token);
        } catch (IllegalArgumentException ex) {
            throw new DomainValidationException("La url del tablero debe contener un UUID valido");
        }

        value = PREFIX + uuid;
    }

    public static TableroUrl createNew() {
        return new TableroUrl(PREFIX + UUID.randomUUID());
    }

    public UUID uuid() {
        return UUID.fromString(value.substring(PREFIX.length()));
    }

    public static String prefix() {
        return PREFIX;
    }
}
