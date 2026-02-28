package com.tasku.core.domain;

import java.util.Objects;

public class ColorEtiqueta {
    private final String value;

    public ColorEtiqueta(String value) {
        // Aquí puedes agregar validación de color (hex, nombre, etc)
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColorEtiqueta that = (ColorEtiqueta) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
