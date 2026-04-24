package com.tasku.core.domain.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class TarjetaChecklist extends Tarjeta {
    private final List<ElementoChecklist> items;

    public TarjetaChecklist(UUID id,
                         UUID listId,
                         String title,
                         String description,
                         boolean archived,
                         Set<EtiquetaTarjeta> labels,
                         List<ElementoChecklist> items) {
        super(id, listId, TipoTarjeta.CHECKLIST, title, description, archived, Objects.requireNonNullElseGet(labels, LinkedHashSet::new));
        this.items = new ArrayList<>(Objects.requireNonNullElseGet(items, ArrayList::new));
    }

    public static TarjetaChecklist createNew(UUID listId,
                                          String title,
                                          String description,
                                          Set<EtiquetaTarjeta> labels,
                                          List<ElementoChecklist> items) {
        return new TarjetaChecklist(UUID.randomUUID(), listId, title, description, false, labels == null ? Set.of() : labels,
                items == null ? List.of() : items);
    }

    public List<ElementoChecklist> items() {
        return List.copyOf(items);
    }
}


