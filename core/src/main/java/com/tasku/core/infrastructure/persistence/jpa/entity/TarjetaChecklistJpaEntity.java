package com.tasku.core.infrastructure.persistence.jpa.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CHECKLIST")
public class TarjetaChecklistJpaEntity extends TarjetaJpaEntity {
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "checklist_items", joinColumns = @JoinColumn(name = "tarjeta_id"))
    private List<ElementoChecklistEmbeddable> items = new ArrayList<>();

    public TarjetaChecklistJpaEntity() {
    }

    public List<ElementoChecklistEmbeddable> getItems() {
        return items;
    }

    public void setItems(List<ElementoChecklistEmbeddable> items) {
        this.items = items;
    }
}
