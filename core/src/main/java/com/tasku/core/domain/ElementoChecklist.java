package com.tasku.core.domain;

public record ElementoChecklist(String descripcion, boolean estaMarcado) {
    public ElementoChecklist {
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía");
        }
    }
}
