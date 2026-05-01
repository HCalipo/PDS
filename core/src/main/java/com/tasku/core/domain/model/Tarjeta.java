package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainValidationException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public abstract class Tarjeta {
    private final TarjetaId id;
    private ListaTableroId listId;
    private final TipoTarjeta type;
    private String title;
    private String description;
    private boolean archived;
    private final Set<EtiquetaTarjeta> labels;

    protected Tarjeta(TarjetaId id,
                   ListaTableroId listId,
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
        return id.id();
    }

    public TarjetaId cardIdValue() {
        return id;
    }

    public UUID listId() {
        return listId.id();
    }

    public ListaTableroId listIdValue() {
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

    public List<ElementoChecklist> checklistItems() {
        return List.of();
    }

    public void addLabel(EtiquetaTarjeta label) {
        labels.add(Objects.requireNonNull(label, "La etiqueta no puede ser nula"));
    }

    public void removeLabel(EtiquetaTarjeta label) {
        labels.remove(Objects.requireNonNull(label, "La etiqueta no puede ser nula"));
    }

    public void moveToList(ListaTableroId destinationListId) {
        this.listId = Objects.requireNonNull(destinationListId, "La lista destino no puede ser nula");
    }

    public void moveToList(UUID destinationListId) {
        moveToList(new ListaTableroId(destinationListId));
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


