package com.tasku.core.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HistorialMovimientos {
    private List<Movimiento> movimientos;

    // Constructores 

    public HistorialMovimientos() {
        this.movimientos = new ArrayList<>();
    }

    public HistorialMovimientos(List<Movimiento> movimientos) {
        this.movimientos = new ArrayList<>(Objects.requireNonNull(movimientos, "La lista de movimientos no puede ser nula"));
    }

    // Getter
    public List<Movimiento> getMovimientos() {
        return List.copyOf(movimientos);
    }

    // Funcion de registrar
    void registrar(Movimiento movimiento) {
        movimientos.add(Objects.requireNonNull(movimiento, "El movimiento no puede ser nulo"));
    }
}
