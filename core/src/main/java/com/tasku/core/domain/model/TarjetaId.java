package com.tasku.core.domain.model;

import java.util.UUID;

public record TarjetaId(UUID id) {

    public TarjetaId {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la tarjeta no puede ser nulo");
        }
    }

    public TarjetaId() {
        this(UUID.randomUUID());
    }
}
