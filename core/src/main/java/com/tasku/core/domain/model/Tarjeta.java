package com.tasku.core.domain.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class Tarjeta {
    private TarjetaId id;
    private String titulo;
    private String descripcion;
    private boolean completada;
    private LocalDateTime fechaCreacion;
    private Set<Etiqueta> etiquetas;

    // Constructor protegido para ser utilizado por las subclases

    protected Tarjeta(TarjetaId id, String titulo, String descripcion) {
        this.id = Objects.requireNonNull(id, "El id de la tarjeta no puede ser nulo");
        this.titulo = validarTexto(titulo, "El título no puede ser nulo ni vacío");
        this.descripcion = validarTexto(descripcion, "La descripción no puede ser nula ni vacía");
        this.completada = false;
        this.fechaCreacion = LocalDateTime.now();
        this.etiquetas = new HashSet<>();
    }

    // Getters 
    public TarjetaId getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isCompletada() {
        return completada;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public Set<Etiqueta> getEtiquetas() {
        return Set.copyOf(etiquetas);
    }

    // Funcion para modificar el contenido de la tarjeta
    void actualizarContenido(String titulo, String descripcion) {
        this.titulo = validarTexto(titulo, "El título no puede ser nulo ni vacío");
        this.descripcion = validarTexto(descripcion, "La descripción no puede ser nula ni vacía");
    }

    // Función para completar la tarjeta y añadirla a la lista de completadas
    
    void completar() {
        this.completada = true;
    }

    // Función para descompletar la tarjeda y sacarla de la lista de completadas
    
    void descompletar() {
        this.completada = false;
    }

    // Función para agregar o quitar etiquetas de la tarjeta
    
    void agregarEtiqueta(Etiqueta etiqueta) {
        etiquetas.add(Objects.requireNonNull(etiqueta, "La etiqueta no puede ser nula"));
    }

    void quitarEtiqueta(Etiqueta etiqueta) {
        etiquetas.remove(Objects.requireNonNull(etiqueta, "La etiqueta no puede ser nula"));
    }

    // Redefinición de equals y hashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tarjeta tarjeta)) {
            return false;
        }
        return Objects.equals(id, tarjeta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    private static String validarTexto(String texto, String mensajeError) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(mensajeError);
        }
        return texto.trim();
    }
}
