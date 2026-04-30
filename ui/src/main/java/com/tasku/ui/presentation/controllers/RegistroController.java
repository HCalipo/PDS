package com.tasku.ui.presentation.controllers;

import com.tasku.ui.SceneManager;
import com.tasku.ui.client.dto.request.RegisterUserApiRequest;
import com.tasku.ui.client.http.DesktopApiException;
import com.tasku.ui.client.http.TaskuApiClient;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class RegistroController {

    @FXML
    private StackPane btnCerrarApp;

    @FXML
    private Label btnInicioSesion;

    @FXML
    private Button btnRegistrar;

    @FXML
    private Label lblMensajeError;

    @FXML
    private TextField txtEmail;

    private final TaskuApiClient apiClient = new TaskuApiClient();

    @FXML
    void handleAbrirPanelInicioSesion(MouseEvent event) {
        SceneManager.getInstance().switchTo("InicioSesion");
    }

    @FXML
    void handleCerrarApp(MouseEvent event) {
        Platform.exit();
        System.exit(0); 
    }

    @FXML
    void handleRegistroEInicioSesion(ActionEvent event) {
        String emailStr = normalize(txtEmail.getText());


        if (emailStr.isBlank()) {
            showError("Por favor, introduce tu email.");
            return;
        }

        try {
            
            RegisterUserApiRequest request = new RegisterUserApiRequest(emailStr);
            apiClient.registerUser(request);
            //si no da error significa que se ha registrado entonces puede inciar sesión 
            // y da paso a la vista principal con el email que acaba de registrar.
            SceneManager.getInstance().setCurrentUserEmail(emailStr);
            SceneManager.getInstance().startMainApp();

        } catch (DesktopApiException ex) {
            showError(ex.getMessage());
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
