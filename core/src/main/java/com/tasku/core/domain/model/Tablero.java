package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainConflictException;
import com.tasku.core.domain.board.exception.DomainForbiddenException;
import com.tasku.core.domain.board.exception.DomainNotFoundException;
import com.tasku.core.domain.board.exception.DomainValidationException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class Tablero {
    private final TableroUrl url;
    private final String name;
    private final Email ownerEmail;
    private final String color;
    private final String description;
    private final EstadoTablero status;
    private final List<ListaTablero> lists;
    private final Set<TableroCompartido> sharedWith;

    public Tablero(TableroUrl url,
                 String name,
                 Email ownerEmail,
                 String color,
                 String description,
                 EstadoTablero status,
                 List<ListaTablero> lists,
                 Set<TableroCompartido> sharedWith) {
        this.url = Objects.requireNonNull(url, "La url del tablero no puede ser nula");
        this.name = validateText(name, "El nombre del tablero no puede ser nulo ni vacio");
        this.ownerEmail = Objects.requireNonNull(ownerEmail, "El email del duenio no puede ser nulo");
        this.color = validateText(color, "El color del tablero no puede ser nulo ni vacio");
        this.description = validateText(description, "La descripcion del tablero no puede ser nula ni vacia");
        this.status = Objects.requireNonNull(status, "El estado del tablero no puede ser nulo");
        this.lists = new ArrayList<>(Objects.requireNonNullElseGet(lists, ArrayList::new));
        this.sharedWith = new LinkedHashSet<>(Objects.requireNonNullElseGet(sharedWith, LinkedHashSet::new));
    }

    public static Tablero createNew(String ownerEmail,
                                  String name,
                                  String color,
                                  String description,
                                  List<DefinicionListaInicial> initialLists) {
        TableroUrl url = TableroUrl.createNew();
        List<ListaTablero> generatedLists = new ArrayList<>();
        if (initialLists != null) {
            for (DefinicionListaInicial definition : initialLists) {
                generatedLists.add(ListaTablero.createNew(url, definition.name(), definition.cardLimit()));
            }
        }
        return new Tablero(url, name, new Email(ownerEmail), color, description, EstadoTablero.ACTIVE,
                generatedLists,
                Set.of());
    }

    public String url() {
        return url.value();
    }

    public TableroUrl urlValue() {
        return url;
    }

    public String name() {
        return name;
    }

    public String ownerEmail() {
        return ownerEmail.email();
    }

    public Email ownerEmailValue() {
        return ownerEmail;
    }

    public String color() {
        return color;
    }

    public String description() {
        return description;
    }

    public EstadoTablero status() {
        return status;
    }

    public List<ListaTablero> lists() {
        return List.copyOf(lists);
    }

    public Set<TableroCompartido> sharedWith() {
        return Set.copyOf(sharedWith);
    }

    public Tablero withAddedList(String listName, int cardLimit, String colorHex) {
        String normalizedName = validateText(listName, "El nombre de la lista no puede ser nulo ni vacio");
        if (cardLimit <= 0) {
            throw new DomainValidationException("El limite de tarjetas de la lista debe ser mayor que cero");
        }
        if (hasListName(normalizedName, null)) {
            throw new DomainConflictException("Ya existe una lista con ese nombre en el tablero");
        }

        List<ListaTablero> updatedLists = new ArrayList<>(lists);
        updatedLists.add(ListaTablero.createNew(url, normalizedName, cardLimit, colorHex));
        return new Tablero(url, name, ownerEmail, color, description, status, updatedLists, sharedWith);
    }

    public Tablero withAddedList(String listName, int cardLimit) {
        return withAddedList(listName, cardLimit, null);
    }

    public Tablero withRenamedList(ListaTableroId listId, String newName) {
        Objects.requireNonNull(listId, "El id de la lista no puede ser nulo");
        String normalizedName = validateText(newName, "El nombre de la lista no puede ser nulo ni vacio");

        if (hasListName(normalizedName, listId)) {
            throw new DomainConflictException("Ya existe una lista con ese nombre en el tablero");
        }

        List<ListaTablero> updatedLists = new ArrayList<>();
        boolean found = false;
        for (ListaTablero list : lists) {
            if (list.listIdValue().equals(listId)) {
                updatedLists.add(list.withName(normalizedName));
                found = true;
            } else {
                updatedLists.add(list);
            }
        }

        if (!found) {
            throw new DomainNotFoundException("No existe la lista indicada en el tablero");
        }

        return new Tablero(url, name, ownerEmail, color, description, status, updatedLists, sharedWith);
    }

    public Tablero withRenamedList(UUID listId, String newName) {
        return withRenamedList(new ListaTableroId(listId), newName);
    }

    public Tablero withRemovedList(ListaTableroId listId) {
        Objects.requireNonNull(listId, "El id de la lista no puede ser nulo");
        List<ListaTablero> updatedLists = new ArrayList<>(lists);
        boolean removed = updatedLists.removeIf(l -> l.listIdValue().equals(listId));
        if (!removed) {
            throw new DomainNotFoundException("No existe la lista indicada en el tablero");
        }
        return new Tablero(url, name, ownerEmail, color, description, status, updatedLists, sharedWith);
    }

    public Tablero withStatus(EstadoTablero newStatus) {
        EstadoTablero validatedStatus = Objects.requireNonNull(newStatus, "El estado del tablero no puede ser nulo");
        if (validatedStatus == status) {
            return this;
        }
        return new Tablero(url, name, ownerEmail, color, description, validatedStatus, lists, sharedWith);
    }

    public boolean isBlocked() {
        return status == EstadoTablero.BLOCKED;
    }

    public Tablero withAddedShare(String email, RolComparticion role) {
        Email normalizedEmail = new Email(email);
        for (TableroCompartido share : sharedWith) {
            if (share.email().equalsIgnoreCase(normalizedEmail.email())) {
                throw new DomainConflictException("El tablero ya esta compartido con ese email");
            }
        }
        Set<TableroCompartido> updatedShares = new LinkedHashSet<>(sharedWith);
        updatedShares.add(new TableroCompartido(null, url, normalizedEmail, role));
        return new Tablero(url, name, ownerEmail, color, description, status, lists, updatedShares);
    }

    public ListaTablero findListOrFail(ListaTableroId listId) {
        Objects.requireNonNull(listId, "El id de la lista no puede ser nulo");
        for (ListaTablero list : lists) {
            if (list.listIdValue().equals(listId)) {
                return list;
            }
        }
        throw new DomainValidationException("La lista indicada no pertenece al tablero");
    }

    public ListaTablero findListOrFail(UUID listId) {
        return findListOrFail(new ListaTableroId(listId));
    }

    public boolean hasAccess(String email) {
        if (ownerEmail.email().equalsIgnoreCase(email)) return true;
        for (TableroCompartido share : sharedWith) {
            if (share.email().equalsIgnoreCase(email)) return true;
        }
        return false;
    }

    public RolComparticion effectiveRoleOf(String email) {
        if (ownerEmail.email().equalsIgnoreCase(email)) {
            return RolComparticion.ADMIN;
        }
        for (TableroCompartido share : sharedWith) {
            if (share.email().equalsIgnoreCase(email)) {
                return share.role();
            }
        }
        throw new DomainForbiddenException("El usuario no tiene acceso a este tablero");
    }

    private boolean hasListName(String candidateName, ListaTableroId ignoredListId) {
        for (ListaTablero list : lists) {
            if (ignoredListId != null && list.listIdValue().equals(ignoredListId)) {
                continue;
            }
            if (list.name().equalsIgnoreCase(candidateName)) {
                return true;
            }
        }
        return false;
    }

    private static String validateText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(message);
        }
        return value.trim();
    }
}


