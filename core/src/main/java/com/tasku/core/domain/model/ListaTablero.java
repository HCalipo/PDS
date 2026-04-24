package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainValidationException;

import java.util.Objects;
import java.util.UUID;

public final class ListaTablero {
    private final UUID id;
    private final String boardUrl;
    private final String name;
    private final int cardLimit;

    public ListaTablero(UUID id, String boardUrl, String name, int cardLimit) {
        this.id = Objects.requireNonNull(id, "El id de la lista no puede ser nulo");
        this.boardUrl = validateText(boardUrl, "La url del tablero no puede ser nula ni vacia");
        this.name = validateText(name, "El nombre de la lista no puede ser nulo ni vacio");
        if (cardLimit <= 0) {
            throw new DomainValidationException("El limite de tarjetas debe ser mayor que cero");
        }
        this.cardLimit = cardLimit;
    }

    public static ListaTablero createNew(String boardUrl, String name, int cardLimit) {
        return new ListaTablero(UUID.randomUUID(), boardUrl, name, cardLimit);
    }

    public UUID id() {
        return id;
    }

    public String boardUrl() {
        return boardUrl;
    }

    public String name() {
        return name;
    }

    public int cardLimit() {
        return cardLimit;
    }

    public ListaTablero withName(String newName) {
        return new ListaTablero(id, boardUrl, newName, cardLimit);
    }

    private static String validateText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(message);
        }
        return value.trim();
    }
}


