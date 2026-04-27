package com.tasku.core.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Usuario {
    private final Email email;
    private final LocalDateTime registrationDate;

    public Usuario(Email email, LocalDateTime registrationDate) {
        this.email = Objects.requireNonNull(email, "El email no puede ser nulo");
        this.registrationDate = Objects.requireNonNull(registrationDate, "La fecha de registro no puede ser nula");
    }

    public static Usuario createNew(String email) {
        return new Usuario(new Email(email), LocalDateTime.now());
    }

    public static Usuario createNew(Email email) {
        return new Usuario(email, LocalDateTime.now());
    }

    public String email() {
        return email.email();
    }

    public Email emailValue() {
        return email;
    }

    public LocalDateTime registrationDate() {
        return registrationDate;
    }

    public static String normalizeEmail(String email) {
        return new Email(email).email();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Usuario usuario)) {
            return false;
        }
        return Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
