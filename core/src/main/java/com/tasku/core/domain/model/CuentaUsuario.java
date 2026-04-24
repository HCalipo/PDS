package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainValidationException;

import java.time.LocalDateTime;
import java.util.Objects;

public final class CuentaUsuario {
    private final String email;
    private final LocalDateTime registrationDate;

    public CuentaUsuario(String email, LocalDateTime registrationDate) {
        this.email = normalizeEmail(email);
        this.registrationDate = Objects.requireNonNull(registrationDate, "La fecha de registro no puede ser nula");
    }

    public static CuentaUsuario createNew(String email) {
        return new CuentaUsuario(email, LocalDateTime.now());
    }

    public String email() {
        return email;
    }

    public LocalDateTime registrationDate() {
        return registrationDate;
    }

    public static String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new DomainValidationException("El email no puede ser nulo ni vacio");
        }
        return email.trim().toLowerCase();
    }
}


