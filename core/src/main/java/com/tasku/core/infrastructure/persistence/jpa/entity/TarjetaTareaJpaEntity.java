package com.tasku.core.infrastructure.persistence.jpa.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TAREA")
public class TarjetaTareaJpaEntity extends TarjetaJpaEntity {
    public TarjetaTareaJpaEntity() {
    }
}
