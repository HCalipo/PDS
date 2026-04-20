package com.tasku.core.domain.model.board;

import com.tasku.core.domain.board.exception.DomainValidationException;

public record DefinicionListaInicial(String name, int cardLimit) {
    public DefinicionListaInicial {
        if (name == null || name.isBlank()) {
            throw new DomainValidationException("El nombre de la lista inicial no puede ser nulo ni vacio");
        }
        if (cardLimit <= 0) {
            throw new DomainValidationException("El limite de la lista inicial debe ser mayor que cero");
        }
        name = name.trim();
    }
}

