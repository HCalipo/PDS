package com.tasku.core.domain.model;

import java.util.Objects;

public class Usuario {
    private Email correo;
    private String nombre;

    // Constructor

    public Usuario(Email correo, String nombre) {
        this.correo = Objects.requireNonNull(correo, "El correo no puede ser nulo");
        this.nombre = validarNombre(nombre);
    }

    // Getters
    
    public Email getCorreo() {
        return correo;
    }

    public String getNombre() {
        return nombre;
    }

    // Validación del nombre de usuario
        private static String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo ni vacío");
        }
        return nombre.trim();
    }
    
    // Redefinición de equals y hashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Usuario usuario)) {
            return false;
        }
        return Objects.equals(correo, usuario.correo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(correo);
    }


}
