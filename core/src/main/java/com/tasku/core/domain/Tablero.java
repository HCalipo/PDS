package com.tasku.core.domain;

import java.util.List;
import java.util.Set;

public class Tablero {
    private URL url;
    private boolean estaBloqueado;
    private Email dueno;
    private Set<Email> colaboradores;
    private List<Tarjeta> tareas;
    private ListaCompletadas listaCompletadas;
    private List<HistorialMovimientos> historial;

    // Getters, setters, constructor, equals, hashCode
}
