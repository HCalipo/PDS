package com.tasku.core.infrastructure.persistence.jpa.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@DiscriminatorValue("CHECKLIST")
public class TarjetaChecklistJpaEntity extends TarjetaJpaEntity {
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "checklist_items", joinColumns = @JoinColumn(name = "tarjeta_id"))
    private List<ElementoChecklistEmbeddable> items = new ArrayList<>();

    public TarjetaChecklistJpaEntity() {
    }

    public TarjetaChecklistJpaEntity(UUID id,
                                     ListaTableroJpaEntity list,
                                     String title,
                                     String description,
                                     boolean archived,
                                     Set<EtiquetaTarjetaEmbeddable> labels,
                                     List<ElementoChecklistEmbeddable> items) {
        super(id, list, title, description, archived, labels);
        replaceItems(items);
    }

    public List<ElementoChecklistEmbeddable> getItems() {
        return items;
    }

    protected void setItems(List<ElementoChecklistEmbeddable> items) {
        this.items = items;
    }

    public void replaceItems(List<ElementoChecklistEmbeddable> items) {
        this.items.clear();
        if (items != null) {
            this.items.addAll(items);
        }
    }
}
