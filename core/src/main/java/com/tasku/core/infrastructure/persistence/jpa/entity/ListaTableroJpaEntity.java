package com.tasku.core.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "listas")
public class ListaTableroJpaEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tablero_url", nullable = false)
    private TableroJpaEntity board;

    @Column(name = "nombre", nullable = false, length = 120)
    private String name;

    @Column(name = "limite_tarjetas", nullable = false)
    private int cardLimit;

    @Column(name = "color_hex", length = 20)
    private String colorHex;

    public ListaTableroJpaEntity() {
    }

    public ListaTableroJpaEntity(UUID id, TableroJpaEntity board, String name, int cardLimit, String colorHex) {
        this.id = id;
        this.board = board;
        this.name = name;
        this.cardLimit = cardLimit;
        this.colorHex = colorHex;
    }

    public UUID getId() {
        return id;
    }

    protected void setId(UUID id) {
        this.id = id;
    }

    public TableroJpaEntity getBoard() {
        return board;
    }

    protected void setBoard(TableroJpaEntity board) {
        this.board = board;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public int getCardLimit() {
        return cardLimit;
    }

    protected void setCardLimit(int cardLimit) {
        this.cardLimit = cardLimit;
    }

    public String getColorHex() {
        return colorHex;
    }

    protected void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }
}
