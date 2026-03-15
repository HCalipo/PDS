package com.tasku.core.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Tablero {
    private URL url;
    private boolean estaBloqueado;
    private Usuario dueno;
    private Set<Usuario> colaboradores;
    private List<ListaTareas> listasTareas;
    private List<Tarjeta> tareas;
    private ListaCompletadas listaCompletadas;
    private List<HistorialMovimientos> historial;

    // Constructor

    public Tablero(URL url, Usuario dueno) {
        this.url = Objects.requireNonNull(url, "La URL del tablero no puede ser nula");
        this.dueno = Objects.requireNonNull(dueno, "El dueño del tablero no puede ser nulo");
        this.estaBloqueado = false;
        this.colaboradores = new HashSet<>();
        this.listasTareas = new ArrayList<>();
        this.tareas = new ArrayList<>();
        this.listaCompletadas = new ListaCompletadas(url);
        this.historial = new ArrayList<>();
        this.historial.add(new HistorialMovimientos());
    }

    // Getters

    public URL getUrl() {
        return url;
    }

    public boolean isEstaBloqueado() {
        return estaBloqueado;
    }

    public Usuario getDueno() {
        return dueno;
    }

    public Set<Usuario> getColaboradores() {
        return Set.copyOf(colaboradores);
    }

    public List<ListaTareas> getListasTareas() {
        return List.copyOf(listasTareas);
    }

    public List<Tarjeta> getTareas() {
        return List.copyOf(tareas);
    }

    public ListaCompletadas getListaCompletadas() {
        return listaCompletadas;
    }

    public List<HistorialMovimientos> getHistorial() {
        return List.copyOf(historial);
    }

    // Funciones para bloquear/desbloquear el tablero

    public void bloquear() {
        this.estaBloqueado = true;
    }

    public void desbloquear() {
        this.estaBloqueado = false;
    }

    // Funcion para agregar colaboradores
    public void agregarColaborador(Usuario colaborador) {
        Usuario usuario = Objects.requireNonNull(colaborador, "El colaborador no puede ser nulo");
        if (usuario.equals(dueno)) {
            throw new IllegalArgumentException("El dueño ya pertenece al tablero");
        }
        colaboradores.add(usuario);
    }

    // Función para crear cuantas listas queramos de tareas

    public ListaTareas crearListaTareas() {
        ListaTareas nuevaLista = new ListaTareas(url);
        listasTareas.add(nuevaLista);
        return nuevaLista;
    }

    // Función para crear tarjetas dentro de las listas de tareas, siempre y cuando el tablero no esté bloqueado
    public void agregarTarjeta(Tarjeta tarjeta, Email autor) {
        if (estaBloqueado) {
            throw new IllegalStateException("No se pueden crear tarjetas en un tablero bloqueado");
        }
        Tarjeta tarjetaNueva = Objects.requireNonNull(tarjeta, "La tarjeta no puede ser nula");
        tareas.add(tarjetaNueva);
        historialPrincipal().registrar(new Movimiento("Tarjeta creada: " + tarjetaNueva.getTitulo(), autor));
    }

    // Función para completar una tarjeta, moviéndola de la lista de tareas a la lista de completadas, siempre y cuando el tablero no esté bloqueado
    public void completarTarjeta(Tarjeta tarjeta, Email autor) {
        Tarjeta tarjetaCompletada = Objects.requireNonNull(tarjeta, "La tarjeta no puede ser nula");
        if (!tareas.remove(tarjetaCompletada)) {
            throw new IllegalArgumentException("La tarjeta no pertenece a las tareas activas del tablero");
        }
        listaCompletadas.anadirAcompletadas(tarjetaCompletada);
        historialPrincipal().registrar(new Movimiento("Tarjeta completada: " + tarjetaCompletada.getTitulo(), autor));
    }

    // Funcion para historial de movimientos del tablero, registrando cada acción realizada en el tablero con su respectiva fecha y autor

    private HistorialMovimientos historialPrincipal() {
        if (historial.isEmpty()) {
            historial.add(new HistorialMovimientos());
        }
        return historial.getFirst();
    }

    // Redefinición de equals y hashCode
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tablero tablero)) {
            return false;
        }
        return Objects.equals(url, tablero.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
