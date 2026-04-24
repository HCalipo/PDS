package com.tasku.core.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ListaCompletadas {
    private TableroId url;
    private ListaCompletadasId id;
    private List<Tarjeta> tarjetas;

    // Constructores 
    
    public ListaCompletadas(TableroId url) {
        this(url, new ListaCompletadasId(url), new ArrayList<>());
    }

    public ListaCompletadas(TableroId url, ListaCompletadasId id, List<Tarjeta> tarjetas) {
        this.url = Objects.requireNonNull(url, "La URL de la lista de completadas no puede ser nula");
        this.id = Objects.requireNonNull(id, "El id de la lista de completadas no puede ser nulo");
        this.tarjetas = new ArrayList<>(Objects.requireNonNull(tarjetas, "La lista de tarjetas no puede ser nula"));
    }

    // Getters 
    
    public TableroId getUrl() {
        return url;
    }

    public ListaCompletadasId getId() {
        return id;
    }

    public List<Tarjeta> getTarjetas() {
        return List.copyOf(tarjetas);
    }

    // Función para añadir una tarjeta a la lista de completadas
    void anadirAcompletadas(Tarjeta tarjeta) {
        Tarjeta tarjetaACompletar = Objects.requireNonNull(tarjeta, "La tarjeta no puede ser nula");
        tarjetaACompletar.archive();
        tarjetas.add(tarjetaACompletar);
    }

    void eliminarDeCompletadas(Tarjeta tarjeta) {
        Tarjeta tarjetaAEliminar = Objects.requireNonNull(tarjeta, "La tarjeta no puede ser nula");
        if (!tarjetas.remove(tarjetaAEliminar)) {
            throw new IllegalArgumentException("La tarjeta no pertenece a la lista de completadas");
        }
    }
    // Redefinición de equals y hashCode
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ListaCompletadas that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
