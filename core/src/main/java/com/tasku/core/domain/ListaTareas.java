package com.tasku.core.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListaTareas {
    private TableroId url;
    private ListaTareasId id;
    private List<Tarjeta> tarjetas;

    // Constructores

    public ListaTareas(TableroId url) {
        this(url, new ListaTareasId(url), new ArrayList<>());
    }

    public ListaTareas(TableroId url, ListaTareasId id, List<Tarjeta> tarjetas) {
        this.url = Objects.requireNonNull(url, "La URL no puede ser nula");
        this.id = Objects.requireNonNull(id, "El id de la lista de tareas no puede ser nulo");
        this.tarjetas = new ArrayList<>(Objects.requireNonNull(tarjetas, "La lista de tarjetas no puede ser nula"));
    }

    // Getters

    public TableroId getUrl() {
        return url;
    }

    public ListaTareasId getId() {
        return id;
    }

    public List<Tarjeta> getTarjetas() {
        return List.copyOf(tarjetas);
    }

    // Funciones para agregar y eliminar tarjetas de la lista de tareas
    
    void agregarTarjeta(Tarjeta tarjeta) {
        tarjetas.add(Objects.requireNonNull(tarjeta, "La tarjeta no puede ser nula"));
    }

    boolean quitarTarjeta(Tarjeta tarjeta) {
        return tarjetas.remove(Objects.requireNonNull(tarjeta, "La tarjeta no puede ser nula"));
    }
}
