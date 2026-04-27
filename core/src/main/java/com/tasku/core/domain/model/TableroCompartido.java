package com.tasku.core.domain.model;

import java.util.Objects;

public final class TableroCompartido {
    private final Long id;
    private final TableroUrl boardUrl;
    private final Email email;
    private final RolComparticion role;

    public TableroCompartido(Long id, TableroUrl boardUrl, Email email, RolComparticion role) {
        this.id = id;
        this.boardUrl = Objects.requireNonNull(boardUrl, "La url del tablero compartido no puede ser nula");
        this.email = Objects.requireNonNull(email, "El email compartido no puede ser nulo");
        this.role = Objects.requireNonNull(role, "El rol de comparticion no puede ser nulo");
    }

    public TableroCompartido(Long id, String boardUrl, String email, RolComparticion role) {
        this(id, new TableroUrl(boardUrl), new Email(email), role);
    }

    public Long id() {
        return id;
    }

    public String boardUrl() {
        return boardUrl.value();
    }

    public TableroUrl boardUrlValue() {
        return boardUrl;
    }

    public String email() {
        return email.email();
    }

    public Email emailValue() {
        return email;
    }

    public RolComparticion role() {
        return role;
    }

}


