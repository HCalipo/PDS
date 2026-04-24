package com.tasku.core.domain.model;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class TarjetaTarea extends Tarjeta {
    public TarjetaTarea(UUID id,
                    UUID listId,
                    String title,
                    String description,
                    boolean archived,
                    Set<EtiquetaTarjeta> labels) {
        super(id, listId, TipoTarjeta.TAREA, title, description, archived, Objects.requireNonNullElseGet(labels, LinkedHashSet::new));
    }

    public static TarjetaTarea createNew(UUID listId, String title, String description, Set<EtiquetaTarjeta> labels) {
        return new TarjetaTarea(UUID.randomUUID(), listId, title, description, false, labels == null ? Set.of() : labels);
    }
}


