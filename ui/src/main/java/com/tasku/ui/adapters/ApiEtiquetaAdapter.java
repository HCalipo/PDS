package com.tasku.ui.adapters;

import com.tasku.ui.client.http.TaskuApiClient;
import com.tasku.ui.port.EtiquetaPort;

public class ApiEtiquetaAdapter implements EtiquetaPort {
    private final TaskuApiClient apiClient;

    public ApiEtiquetaAdapter(TaskuApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public void crearEtiqueta(String nombre, String color) {
        try {
            apiClient.crearEtiqueta(nombre, color);
        } catch (Exception e) {
            System.err.println("Error al crear etiqueta: " + e.getMessage());
        }
    }
}
