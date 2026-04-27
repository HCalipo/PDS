package com.tasku.core.infrastructure.persistence.jpa.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.Set;
import java.util.UUID;

@Entity
@DiscriminatorValue("TAREA")
public class TarjetaTareaJpaEntity extends TarjetaJpaEntity {
    public TarjetaTareaJpaEntity() {
    }

    public TarjetaTareaJpaEntity(UUID id,
                                 ListaTableroJpaEntity list,
                                 String title,
                                 String description,
                                 boolean archived,
                                 Set<EtiquetaTarjetaEmbeddable> labels) {
        super(id, list, title, description, archived, labels);
    }
}
