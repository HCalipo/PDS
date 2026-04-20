package com.tasku.core.domain.model.board;

import com.tasku.core.domain.board.exception.DomainValidationException;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public abstract class Tarjeta {
    private final UUID id;
    private UUID listId;
    private final TipoTarjeta type;
    private String title;
    private String description;
    private boolean archived;
    private final Set<EtiquetaTarjeta> labels;

    protected Tarjeta(UUID id,
                   UUID listId,
                   TipoTarjeta type,
                   String title,
                   String description,
                   boolean archived,
                   Set<EtiquetaTarjeta> labels) {
        this.id = Objects.requireNonNull(id, "El id de la tarjeta no puede ser nulo");
        this.listId = Objects.requireNonNull(listId, "El id de la lista no puede ser nulo");
        this.type = Objects.requireNonNull(type, "El tipo de tarjeta no puede ser nulo");
        this.title = validateText(title, "El titulo no puede ser nulo ni vacio");
        this.description = validateText(description, "La descripcion no puede ser nula ni vacia");
        this.archived = archived;
        this.labels = new LinkedHashSet<>(Objects.requireNonNull(labels, "Las etiquetas no pueden ser nulas"));
    }

    public UUID id() {
        return id;
    }

    public UUID listId() {
        return listId;
    }

    public TipoTarjeta type() {
        return type;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public boolean archived() {
        return archived;
    }

    public Set<EtiquetaTarjeta> labels() {
        return Set.copyOf(labels);
    }

    public void moveToList(UUID destinationListId) {
        this.listId = Objects.requireNonNull(destinationListId, "La lista destino no puede ser nula");
    }

    public void archive() {
        this.archived = true;
    }

    protected static String validateText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(message);
        }
        return value.trim();
    }
}

