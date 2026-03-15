package com.tasku.core.domain;

public class TarjetaTarea extends Tarjeta {
    private String texto;
    
    // Constructores

    public TarjetaTarea(TarjetaId id, String titulo, String descripcion, String texto) {
        super(id, titulo, descripcion);
        this.texto = validarTexto(texto);
    }

    public TarjetaTarea(String titulo, String descripcion, String texto) {
        this(new TarjetaId(), titulo, descripcion, texto);
    }

    // Getter

    public String getTexto() {
        return texto;
    }

    // Función para modificar el texto de la tarjeta
    void actualizarTexto(String texto) {
        this.texto = validarTexto(texto);
    }

    // Función para validar el contenido 
    private static String validarTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("El texto de la tarea no puede ser nulo ni vacío");
        }
        return texto.trim();
    }
}
