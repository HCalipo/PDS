package com.tasku.core.domain;

public class Etiqueta {
    private EtiquetaId id;
    private String texto;
    private ColorEtiqueta color;

    // Constructores 

    public Etiqueta(String texto, ColorEtiqueta color) {
        this(new EtiquetaId(), texto, color);
    }

    public Etiqueta(EtiquetaId id, String texto, ColorEtiqueta color) {
        this.id = Objects.requireNonNull(id, "El id de la etiqueta no puede ser nulo");
        this.texto = validarTexto(texto);
        this.color = Objects.requireNonNull(color, "El color no puede ser nulo");
    }

    // Getters 
    public EtiquetaId getId() {
        return id;
    }

    public String getTexto() {
        return texto;
    }

    public ColorEtiqueta getColor() {
        return color;
    }

    // Función para validar el contenido de la etiqueta
    
    private static String validarTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("El texto de la etiqueta no puede ser nulo ni vacío");
        }
        return texto.trim();
    }
 
}