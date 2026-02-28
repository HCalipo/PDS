package com.tasku.core.domain;

import java.time.LocalDateTime;

import java.util.Set;

public abstract class Tarjeta {
    private String id;
    private String titulo;
    private String descripcion;
    private boolean estaCompletada;
    private LocalDateTime fechaCreacion;
    private Set<Etiqueta> etiquetas;

    // Getters, setters, constructor, equals, hashCode
}
