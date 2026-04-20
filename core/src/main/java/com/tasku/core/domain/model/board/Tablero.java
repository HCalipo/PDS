package com.tasku.core.domain.model.board;

import com.tasku.core.domain.board.exception.DomainConflictException;
import com.tasku.core.domain.board.exception.DomainValidationException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class Tablero {
    private static final String URL_PREFIX = "tasku://tablero/";

    private final String url;
    private final String name;
    private final String ownerEmail;
    private final String color;
    private final String description;
    private final EstadoTablero status;
    private final List<ListaTablero> lists;
    private final Set<TableroCompartido> sharedWith;

    public Tablero(String url,
                 String name,
                 String ownerEmail,
                 String color,
                 String description,
                 EstadoTablero status,
                 List<ListaTablero> lists,
                 Set<TableroCompartido> sharedWith) {
        this.url = validateBoardUrl(url);
        this.name = validateText(name, "El nombre del tablero no puede ser nulo ni vacio");
        this.ownerEmail = CuentaUsuario.normalizeEmail(ownerEmail);
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
        String url = URL_PREFIX + UUID.randomUUID();
        List<ListaTablero> generatedLists = new ArrayList<>();
        if (initialLists != null) {
            for (DefinicionListaInicial definition : initialLists) {
                generatedLists.add(ListaTablero.createNew(url, definition.name(), definition.cardLimit()));
            }
        }
        return new Tablero(url, name, ownerEmail, color, description, EstadoTablero.ACTIVE,
                generatedLists,
                Set.of());
    }

    public String url() {
        return url;
    }

    public String name() {
        return name;
    }

    public String ownerEmail() {
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

    public Tablero withAddedShare(String email, RolComparticion role) {
        String normalizedEmail = CuentaUsuario.normalizeEmail(email);
        for (TableroCompartido share : sharedWith) {
            if (share.email().equalsIgnoreCase(normalizedEmail)) {
                throw new DomainConflictException("El tablero ya esta compartido con ese email");
            }
        }
        Set<TableroCompartido> updatedShares = new LinkedHashSet<>(sharedWith);
        updatedShares.add(new TableroCompartido(null, url, normalizedEmail, role));
        return new Tablero(url, name, ownerEmail, color, description, status, lists, updatedShares);
    }

    public ListaTablero findListOrFail(UUID listId) {
        Objects.requireNonNull(listId, "El id de la lista no puede ser nulo");
        for (ListaTablero list : lists) {
            if (list.id().equals(listId)) {
                return list;
            }
        }
        throw new DomainValidationException("La lista indicada no pertenece al tablero");
    }

    private static String validateBoardUrl(String value) {
        String url = validateText(value, "La url del tablero no puede ser nula ni vacia");
        if (!url.startsWith(URL_PREFIX)) {
            throw new DomainValidationException("La url del tablero debe usar el prefijo tasku://tablero/");
        }
        return url;
    }

    private static String validateText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(message);
        }
        return value.trim();
    }
}

