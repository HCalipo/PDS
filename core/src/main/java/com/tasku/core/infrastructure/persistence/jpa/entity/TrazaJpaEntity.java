package com.tasku.core.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "trazas")
public class TrazaJpaEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tablero_url", nullable = false)
    private TableroJpaEntity board;

    @Column(name = "autor_email", nullable = false, length = 320)
    private String authorEmail;

    @Column(name = "descripcion", nullable = false, length = 500)
    private String description;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime date;

    public TrazaJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TableroJpaEntity getBoard() {
        return board;
    }

    public void setBoard(TableroJpaEntity board) {
        this.board = board;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
