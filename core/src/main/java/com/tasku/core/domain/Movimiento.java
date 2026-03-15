package com.tasku.core.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Movimiento {
    private MovimientoId id;
    private LocalDateTime fechaHora;
    private String accionDetalle;
    private Email autor;


    // Constructores 

    public Movimiento(MovimientoId id, LocalDateTime fechaHora, String accionDetalle, Email autor) {
        this.id = Objects.requireNonNull(id, "El id del movimiento no puede ser nulo");
        this.fechaHora = Objects.requireNonNull(fechaHora, "La fecha del movimiento no puede ser nula");
        this.accionDetalle = validarDetalle(accionDetalle);
        this.autor = Objects.requireNonNull(autor, "El autor no puede ser nulo");
    }

    public Movimiento(String accionDetalle, Email autor) {
        this(new MovimientoId(), LocalDateTime.now(), accionDetalle, autor);
    }

    // Getters

    public MovimientoId getId() {
        return id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getAccionDetalle() {
        return accionDetalle;
    }

    public Email getAutor() {
        return autor;
    }

    // Función para validar el detalle de la acción
    
    private static String validarDetalle(String accionDetalle) {
        if (accionDetalle == null || accionDetalle.isBlank()) {
            throw new IllegalArgumentException("El detalle de la acción no puede ser nulo ni vacío");
        }
        return accionDetalle.trim();
    }

    // Redefinición de equals y hashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Movimiento movimiento)) {
            return false;
        }
        return Objects.equals(id, movimiento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
