package com.tasku.ui.adapters;

import com.tasku.ui.client.http.TaskuApiClient;
import com.tasku.ui.port.TableroPort;

import java.util.UUID;

public class ApiTableroAdapter implements TableroPort {
    private final TaskuApiClient apiClient;

    public ApiTableroAdapter(TaskuApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public void bloquear(String url) {
        try {
            apiClient.bloquearTablero(url);
        } catch (Exception e) {
            System.err.println("Error al bloquear tablero: " + e.getMessage());
        }
    }

    @Override
    public void desbloquear(String url) {
        try {
            apiClient.desbloquearTablero(url);
        } catch (Exception e) {
            System.err.println("Error al desbloquear tablero: " + e.getMessage());
        }
    }

    @Override
    public UUID crearListaTareas(String url, Object usuario) {
        try {
            Object response = apiClient.crearLista(url, usuario != null ? usuario.toString() : "");
            return response != null ? UUID.randomUUID() : null;
        } catch (Exception e) {
            System.err.println("Error al crear lista: " + e.getMessage());
            return null;
        }
    }
}
