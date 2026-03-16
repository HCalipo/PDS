package com.tasku.core.domain;

import java.util.UUID;

public record MovimientoId(UUID id) {

    public MovimientoId {
        if (id == null) {
            throw new IllegalArgumentException("El ID del movimiento no puede ser nulo");
        }
    }

    public MovimientoId() {
        this(UUID.randomUUID());
    }
}
