package com.tasku.core.domain.model.board;

import com.tasku.core.domain.board.exception.DomainValidationException;

public record EtiquetaTarjeta(String name, String colorHex) {
    public EtiquetaTarjeta {
        if (name == null || name.isBlank()) {
            throw new DomainValidationException("El nombre de la etiqueta no puede ser nulo ni vacio");
        }
        if (colorHex == null || colorHex.isBlank()) {
            throw new DomainValidationException("El color de la etiqueta no puede ser nulo ni vacio");
        }
        name = name.trim();
        colorHex = colorHex.trim();
    }
}

