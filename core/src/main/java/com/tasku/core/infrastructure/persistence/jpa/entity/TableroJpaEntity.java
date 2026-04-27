package com.tasku.core.infrastructure.persistence.jpa.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tableros")
public class TableroJpaEntity {
    @Id
    @Column(name = "url", nullable = false, length = 128)
    private String url;

    @Column(name = "nombre", nullable = false, length = 120)
    private String name;

    @Column(name = "dueno_email", nullable = false, length = 320)
    private String ownerEmail;

    @Column(name = "color", nullable = false, length = 32)
    private String color;

    @Column(name = "descripcion", nullable = false, length = 500)
    private String description;

    @Column(name = "estado", nullable = false, length = 32)
    private String status;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ListaTableroJpaEntity> lists = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<TableroCompartidoJpaEntity> sharedBoards = new LinkedHashSet<>();

    public TableroJpaEntity() {
    }

    public TableroJpaEntity(String url,
                            String name,
                            String ownerEmail,
                            String color,
                            String description,
                            String status) {
        this.url = url;
        this.name = name;
        this.ownerEmail = ownerEmail;
        this.color = color;
        this.description = description;
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    protected void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    protected void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getColor() {
        return color;
    }

    protected void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    protected void setStatus(String status) {
        this.status = status;
    }

    public List<ListaTableroJpaEntity> getLists() {
        return lists;
    }

    protected void setLists(List<ListaTableroJpaEntity> lists) {
        this.lists = lists;
    }

    public Set<TableroCompartidoJpaEntity> getSharedBoards() {
        return sharedBoards;
    }

    protected void setSharedBoards(Set<TableroCompartidoJpaEntity> sharedBoards) {
        this.sharedBoards = sharedBoards;
    }

    public void replaceLists(List<ListaTableroJpaEntity> lists) {
        this.lists.clear();
        if (lists != null) {
            this.lists.addAll(lists);
        }
    }

    public void replaceSharedBoards(Set<TableroCompartidoJpaEntity> sharedBoards) {
        this.sharedBoards.clear();
        if (sharedBoards != null) {
            this.sharedBoards.addAll(sharedBoards);
        }
    }
}
