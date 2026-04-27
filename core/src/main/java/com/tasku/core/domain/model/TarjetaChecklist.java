package com.tasku.core.domain.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class TarjetaChecklist extends Tarjeta {
    private final List<ElementoChecklist> items;

    public TarjetaChecklist(TarjetaId id,
                         ListaTableroId listId,
                         String title,
                         String description,
                         boolean archived,
                         Set<EtiquetaTarjeta> labels,
                         List<ElementoChecklist> items) {
        super(id, listId, TipoTarjeta.CHECKLIST, title, description, archived, Objects.requireNonNullElseGet(labels, LinkedHashSet::new));
        this.items = new ArrayList<>(Objects.requireNonNullElseGet(items, ArrayList::new));
    }

    public static TarjetaChecklist createNew(ListaTableroId listId,
                                          String title,
                                          String description,
                                          Set<EtiquetaTarjeta> labels,
                                          List<ElementoChecklist> items) {
        return new TarjetaChecklist(new TarjetaId(), listId, title, description, false, labels == null ? Set.of() : labels,
                items == null ? List.of() : items);
    }

    public static TarjetaChecklist createNew(java.util.UUID listId,
                                          String title,
                                          String description,
                                          Set<EtiquetaTarjeta> labels,
                                          List<ElementoChecklist> items) {
        return createNew(new ListaTableroId(listId), title, description, labels, items);
    }

    public List<ElementoChecklist> items() {
        return List.copyOf(items);
    }
}


