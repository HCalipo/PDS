package com.tasku.core.domain.model;

import java.util.UUID;

public record EtiquetaId(UUID id) {

    public EtiquetaId {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la etiqueta no puede ser nulo");
        }
    }

    public EtiquetaId() {
        this(UUID.randomUUID());
    }
}

