package com.tasku.core.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class EtiquetaTarjetaEmbeddable {
    @Column(name = "nombre", nullable = false, length = 120)
    private String name;

    @Column(name = "color_hex", nullable = false, length = 16)
    private String colorHex;

    public EtiquetaTarjetaEmbeddable() {
    }

    public EtiquetaTarjetaEmbeddable(String name, String colorHex) {
        this.name = name;
        this.colorHex = colorHex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EtiquetaTarjetaEmbeddable that)) {
            return false;
        }
        return Objects.equals(name, that.name) && Objects.equals(colorHex, that.colorHex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, colorHex);
    }
}
