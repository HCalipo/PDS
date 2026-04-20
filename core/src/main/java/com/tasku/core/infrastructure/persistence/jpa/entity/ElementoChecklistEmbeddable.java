package com.tasku.core.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ElementoChecklistEmbeddable {
    @Column(name = "descripcion", nullable = false, length = 300)
    private String description;

    @Column(name = "completado", nullable = false)
    private boolean completed;

    public ElementoChecklistEmbeddable() {
    }

    public ElementoChecklistEmbeddable(String description, boolean completed) {
        this.description = description;
        this.completed = completed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
