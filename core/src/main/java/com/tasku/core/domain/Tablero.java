package com.tasku.core.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Tablero {
    private TableroId url;
    private boolean estaBloqueado;
    private Usuario dueno;
    private Set<Usuario> colaboradores;
    private List<ListaTareas> listasTareas;
    private ListaCompletadas listaCompletadas;
    private HistorialMovimientos historial;

    public Tablero(TableroId url, Usuario dueno) {
        this.url = Objects.requireNonNull(url, "La URL del tablero no puede ser nula");
        this.dueno = Objects.requireNonNull(dueno, "El dueño del tablero no puede ser nulo");
        this.estaBloqueado = false;
        this.colaboradores = new HashSet<>();
        this.listasTareas = new ArrayList<>();
        this.listaCompletadas = new ListaCompletadas(url);
        this.historial = new HistorialMovimientos();
    }

    // Getters

    public TableroId getUrl() {
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

    public List<Tarjeta> getTarjetasActivas() {
        List<Tarjeta> resultado = new ArrayList<>();
        for (ListaTareas lista : listasTareas) {
            resultado.addAll(lista.getTarjetas());
        }
        return List.copyOf(resultado);
    }

    public List<Tarjeta> getTarjetasCompletadas() {
        return listaCompletadas.getTarjetas();
    }

    public List<Movimiento> getHistorial() {
        return historial.getMovimientos();
    }

    // Funciones del Tablero

    public void bloquear() {
        this.estaBloqueado = true;
    }

    public void desbloquear() {
        this.estaBloqueado = false;
    }

    public void agregarColaborador(Usuario colaborador) {
        Usuario usuario = Objects.requireNonNull(colaborador, "El colaborador no puede ser nulo");
        if (usuario.equals(dueno)) {
            throw new IllegalArgumentException("El dueño ya pertenece al tablero");
        }
        colaboradores.add(usuario);
    }

    public UUID crearListaTareas() {
        ListaTareas nuevaLista = new ListaTareas(url);
        listasTareas.add(nuevaLista);
        return nuevaLista.getId().id();
    }

    public UUID crearTarjetaTarea(UUID listaId, String titulo, String descripcion, String texto, Usuario autor) {
        validarModificacion(autor);
        ListaTareas listaDestino = buscarLista(listaId);
        TarjetaTarea tarjeta = new TarjetaTarea(titulo, descripcion, texto);
        listaDestino.agregarTarjeta(tarjeta);
        historial.registrar(new Movimiento("Tarjeta creada: " + tarjeta.getTitulo(), autor.getCorreo()));
        return tarjeta.getId().id();
    }

    public UUID crearTarjetaChecklist(UUID listaId, String titulo, String descripcion, Usuario autor) {
        validarModificacion(autor);
        ListaTareas listaDestino = buscarLista(listaId);
        TarjetaChecklist tarjeta = new TarjetaChecklist(titulo, descripcion);
        listaDestino.agregarTarjeta(tarjeta);
        historial.registrar(new Movimiento("Tarjeta creada: " + tarjeta.getTitulo(), autor.getCorreo()));
        return tarjeta.getId().id();
    }

    public void agregarEtiqueta(UUID tarjetaId, String texto, ColorEtiqueta color, Usuario autor) {
        validarModificacion(autor);
        Tarjeta tarjeta = buscarTarjeta(tarjetaId);
        tarjeta.agregarEtiqueta(new Etiqueta(texto, color));
        historial.registrar(new Movimiento("Etiqueta agregada: " + texto, autor.getCorreo()));
    }

    public void quitarEtiqueta(UUID tarjetaId, String texto, ColorEtiqueta color, Usuario autor) {
        validarModificacion(autor);
        Tarjeta tarjeta = buscarTarjeta(tarjetaId);
        tarjeta.quitarEtiqueta(new Etiqueta(texto, color));
        historial.registrar(new Movimiento("Etiqueta removida: " + texto, autor.getCorreo()));
    }

    public void agregarItemChecklist(UUID tarjetaId, String descripcion, Usuario autor) {
        validarModificacion(autor);
        TarjetaChecklist checklist = requireChecklist(buscarTarjeta(tarjetaId));
        checklist.agregarItemChecklist(descripcion);
        historial.registrar(new Movimiento("Item checklist agregado", autor.getCorreo()));
    }

    public void marcarItemChecklist(UUID tarjetaId, int indice, boolean marcado, Usuario autor) {
        validarModificacion(autor);
        TarjetaChecklist checklist = requireChecklist(buscarTarjeta(tarjetaId));
        checklist.marcarItemChecklist(indice, marcado);
        historial.registrar(new Movimiento("Item checklist actualizado", autor.getCorreo()));
    }

    public void completarTarjeta(UUID tarjetaId, Usuario autor) {
        validarModificacion(autor);
        ListaTareas listaOrigen = buscarListaPorTarjeta(tarjetaId);
        Tarjeta tarjeta = buscarTarjeta(tarjetaId);
        if (!listaOrigen.quitarTarjeta(tarjeta)) {
            throw new IllegalArgumentException("La tarjeta no pertenece a la lista indicada");
        }
        listaCompletadas.anadirAcompletadas(tarjeta);
        historial.registrar(new Movimiento("Tarjeta completada: " + tarjeta.getTitulo(), autor.getCorreo()));
    }

    public void moverTarjeta(UUID tarjetaId, UUID listaDestinoId, Usuario autor) {
        validarModificacion(autor);
        if (estaCompletada(tarjetaId)) {
            throw new IllegalStateException("La tarjeta ya esta completada");
        }
        ListaTareas listaOrigen = buscarListaPorTarjeta(tarjetaId);
        ListaTareas listaDestino = buscarLista(listaDestinoId);
        if (listaOrigen.equals(listaDestino)) {
            return;
        }

        Tarjeta tarjeta = buscarTarjeta(tarjetaId);
        if (!listaOrigen.quitarTarjeta(tarjeta)) {
            throw new IllegalArgumentException("La tarjeta no pertenece a la lista indicada");
        }
        listaDestino.agregarTarjeta(tarjeta);
        historial.registrar(new Movimiento("Tarjeta movida: " + tarjeta.getTitulo(), autor.getCorreo()));
    }

    public Tarjeta obtenerTarjeta(UUID tarjetaId) {
        return buscarTarjeta(tarjetaId);
    }

    private void validarModificacion(Usuario usuario) {
        if (estaBloqueado) {
            throw new IllegalStateException("No se pueden modificar tarjetas en un tablero bloqueado");
        }
        if (!dueno.equals(usuario) && !colaboradores.contains(usuario)) {
            throw new IllegalArgumentException("El usuario no tiene permisos para modificar este tablero");
        }
    }

    private ListaTareas buscarLista(UUID listaId) {
        return listasTareas.stream()
                .filter(lista -> lista.getId().id().equals(listaId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("La lista de tareas no existe en este tablero"));
    }

    private Tarjeta buscarTarjeta(UUID tarjetaId) {
        for (ListaTareas lista : listasTareas) {
            for (Tarjeta tarjeta : lista.getTarjetas()) {
                if (tarjeta.getId().id().equals(tarjetaId)) {
                    return tarjeta;
                }
            }
        }
        for (Tarjeta tarjeta : listaCompletadas.getTarjetas()) {
            if (tarjeta.getId().id().equals(tarjetaId)) {
                return tarjeta;
            }
        }
        throw new IllegalArgumentException("La tarjeta no existe en este tablero");
    }

    private ListaTareas buscarListaPorTarjeta(UUID tarjetaId) {
        for (ListaTareas lista : listasTareas) {
            for (Tarjeta tarjeta : lista.getTarjetas()) {
                if (tarjeta.getId().id().equals(tarjetaId)) {
                    return lista;
                }
            }
        }
        throw new IllegalArgumentException("La tarjeta no pertenece a ninguna lista activa");
    }

    private boolean estaCompletada(UUID tarjetaId) {
        for (Tarjeta tarjeta : listaCompletadas.getTarjetas()) {
            if (tarjeta.getId().id().equals(tarjetaId)) {
                return true;
            }
        }
        return false;
    }

    private TarjetaChecklist requireChecklist(Tarjeta tarjeta) {
        if (tarjeta instanceof TarjetaChecklist checklist) {
            return checklist;
        }
        throw new IllegalArgumentException("La tarjeta no es checklist");
    }

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