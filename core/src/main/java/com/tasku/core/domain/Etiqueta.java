package com.tasku.core.domain;

import java.util.Objects;

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
 
  
        
    // Modificar las etiquetas, cambiándole el texto o el color

    public void actualizarTexto(String texto) {
        this.texto = validarTexto(texto);
    }

    public void actualizarColor(ColorEtiqueta color) {
        this.color = Objects.requireNonNull(color, "El color no puede ser nulo");
    }
    
    // Redefinición de hashCode y equals

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Etiqueta etiqueta)) {
            return false;
        }
        return Objects.equals(id, etiqueta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
