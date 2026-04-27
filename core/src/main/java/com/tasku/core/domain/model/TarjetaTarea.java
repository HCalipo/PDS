package com.tasku.core.domain.model;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class TarjetaTarea extends Tarjeta {
    public TarjetaTarea(TarjetaId id,
                    ListaTableroId listId,
                    String title,
                    String description,
                    boolean archived,
                    Set<EtiquetaTarjeta> labels) {
        super(id, listId, TipoTarjeta.TAREA, title, description, archived, Objects.requireNonNullElseGet(labels, LinkedHashSet::new));
    }

    public static TarjetaTarea createNew(ListaTableroId listId, String title, String description, Set<EtiquetaTarjeta> labels) {
        return new TarjetaTarea(new TarjetaId(), listId, title, description, false, labels == null ? Set.of() : labels);
    }

    public static TarjetaTarea createNew(java.util.UUID listId, String title, String description, Set<EtiquetaTarjeta> labels) {
        return createNew(new ListaTableroId(listId), title, description, labels);
    }
}


