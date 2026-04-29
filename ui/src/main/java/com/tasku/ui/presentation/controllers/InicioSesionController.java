package com.tasku.ui.presentation.controllers;

import com.tasku.ui.SceneManager;
import com.tasku.ui.client.http.DesktopApiException;
import com.tasku.ui.client.http.TaskuApiClient;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.regex.Pattern;

public class InicioSesionController {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    @FXML private TextField txtEmail;
    @FXML private TextField txtNombre;
    @FXML private Label lblMensajeError;

    private final TaskuApiClient apiClient = new TaskuApiClient();

    @FXML
    private void iniciarSesion() {
        String email = normalize(txtEmail.getText());

        if (email.isBlank()) {
            showError("Introduce tu email.");
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showError("El email no tiene un formato válido (ej: usuario@dominio.com).");
            return;
        }

        try {
            apiClient.findBoardsByOwner(email);
            SceneManager.getInstance().setCurrentUserEmail(email);
            SceneManager.getInstance().switchTo("Principal");
        } catch (DesktopApiException ex) {
            showError("Error al conectar: " + ex.getMessage());
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private void showError(String message) {
        lblMensajeError.setStyle("-fx-text-fill: #d63031;");
        lblMensajeError.setText(message);
    }
}
