package com.tasku.core.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "tableros_compartidos",
        uniqueConstraints = @UniqueConstraint(name = "uk_tablero_email", columnNames = {"tablero_url", "email"})
)
public class TableroCompartidoJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tablero_url", nullable = false)
    private TableroJpaEntity board;

    @Column(name = "email", nullable = false, length = 320)
    private String email;

    @Column(name = "rol", nullable = false, length = 32)
    private String role;

    public TableroCompartidoJpaEntity() {
    }

    public TableroCompartidoJpaEntity(Long id, TableroJpaEntity board, String email, String role) {
        this.id = id;
        this.board = board;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public TableroJpaEntity getBoard() {
        return board;
    }

    protected void setBoard(TableroJpaEntity board) {
        this.board = board;
    }

    public String getEmail() {
        return email;
    }

    protected void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    protected void setRole(String role) {
        this.role = role;
    }
}
