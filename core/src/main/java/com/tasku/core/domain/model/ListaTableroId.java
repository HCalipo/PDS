package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainValidationException;

import java.util.UUID;

public record ListaTableroId(UUID id) {
    public ListaTableroId {
        if (id == null) {
            throw new DomainValidationException("El id de la lista no puede ser nulo");
        }
    }

    public ListaTableroId() {
        this(UUID.randomUUID());
    }
}
