package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainValidationException;

import java.util.Objects;
import java.util.UUID;

public final class ListaTablero {
    private static final String DEFAULT_COLOR = "#90CAF9";

    private final ListaTableroId id;
    private final TableroUrl boardUrl;
    private final String name;
    private final int cardLimit;
    private final String colorHex;

    public ListaTablero(ListaTableroId id, TableroUrl boardUrl, String name, int cardLimit, String colorHex) {
        this.id = Objects.requireNonNull(id, "El id de la lista no puede ser nulo");
        this.boardUrl = Objects.requireNonNull(boardUrl, "La url del tablero no puede ser nula");
        this.name = validateText(name, "El nombre de la lista no puede ser nulo ni vacio");
        if (cardLimit <= 0) {
            throw new DomainValidationException("El limite de tarjetas debe ser mayor que cero");
        }
        this.cardLimit = cardLimit;
        this.colorHex = (colorHex == null || colorHex.isBlank()) ? DEFAULT_COLOR : colorHex;
    }

    public ListaTablero(ListaTableroId id, TableroUrl boardUrl, String name, int cardLimit) {
        this(id, boardUrl, name, cardLimit, DEFAULT_COLOR);
    }

    public static ListaTablero createNew(TableroUrl boardUrl, String name, int cardLimit, String colorHex) {
        return new ListaTablero(new ListaTableroId(), boardUrl, name, cardLimit, colorHex);
    }

    public static ListaTablero createNew(TableroUrl boardUrl, String name, int cardLimit) {
        return createNew(boardUrl, name, cardLimit, DEFAULT_COLOR);
    }

    public static ListaTablero createNew(String boardUrl, String name, int cardLimit) {
        return createNew(new TableroUrl(boardUrl), name, cardLimit, DEFAULT_COLOR);
    }

    public UUID id() {
        return id.id();
    }

    public ListaTableroId listIdValue() {
        return id;
    }

    public String boardUrl() {
        return boardUrl.value();
    }

    public TableroUrl boardUrlValue() {
        return boardUrl;
    }

    public String name() {
        return name;
    }

    public int cardLimit() {
        return cardLimit;
    }

    public String colorHex() {
        return colorHex;
    }

    public ListaTablero withName(String newName) {
        return new ListaTablero(id, boardUrl, newName, cardLimit, colorHex);
    }

    private static String validateText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(message);
        }
        return value.trim();
    }
}


