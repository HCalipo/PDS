package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainValidationException;

public record ElementoChecklist(String description, boolean completed) {
    public ElementoChecklist {
        if (description == null || description.isBlank()) {
            throw new DomainValidationException("La descripcion del item no puede ser nula ni vacia");
        }
        description = description.trim();
    }
}


