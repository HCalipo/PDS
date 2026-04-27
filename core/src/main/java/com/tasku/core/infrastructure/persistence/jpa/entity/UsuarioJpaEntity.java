package com.tasku.core.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class UsuarioJpaEntity {
    @Id
    @Column(name = "email", nullable = false, length = 320)
    private String email;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime registrationDate;

    public UsuarioJpaEntity() {
    }

    public UsuarioJpaEntity(String email, LocalDateTime registrationDate) {
        this.email = email;
        this.registrationDate = registrationDate;
    }

    public String getEmail() {
        return email;
    }

    protected void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    protected void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
}
