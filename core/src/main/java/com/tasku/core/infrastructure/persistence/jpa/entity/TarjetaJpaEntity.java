package com.tasku.core.infrastructure.persistence.jpa.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tarjetas")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_tarjeta", discriminatorType = DiscriminatorType.STRING)
public abstract class TarjetaJpaEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lista_id", nullable = false)
    private ListaTableroJpaEntity list;

    @Column(name = "titulo", nullable = false, length = 150)
    private String title;

    @Column(name = "descripcion", nullable = false, length = 500)
    private String description;

    @Column(name = "archivada", nullable = false)
    private boolean archived;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tarjeta_etiquetas", joinColumns = @JoinColumn(name = "tarjeta_id"))
    private Set<EtiquetaTarjetaEmbeddable> labels = new LinkedHashSet<>();

    public TarjetaJpaEntity() {
    }

    protected TarjetaJpaEntity(UUID id,
                               ListaTableroJpaEntity list,
                               String title,
                               String description,
                               boolean archived,
                               Set<EtiquetaTarjetaEmbeddable> labels) {
        this.id = id;
        this.list = list;
        this.title = title;
        this.description = description;
        this.archived = archived;
        replaceLabels(labels);
    }

    public UUID getId() {
        return id;
    }

    protected void setId(UUID id) {
        this.id = id;
    }

    public ListaTableroJpaEntity getList() {
        return list;
    }

    protected void setList(ListaTableroJpaEntity list) {
        this.list = list;
    }

    public String getTitle() {
        return title;
    }

    protected void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    public boolean isArchived() {
        return archived;
    }

    protected void setArchived(boolean archived) {
        this.archived = archived;
    }

    public Set<EtiquetaTarjetaEmbeddable> getLabels() {
        return labels;
    }

    protected void setLabels(Set<EtiquetaTarjetaEmbeddable> labels) {
        this.labels = labels;
    }

    public void replaceLabels(Set<EtiquetaTarjetaEmbeddable> labels) {
        this.labels.clear();
        if (labels != null) {
            this.labels.addAll(labels);
        }
    }
}
