package com.tasku.ui.presentation.controllers;

import com.tasku.ui.SceneManager;
import com.tasku.ui.client.dto.request.LoginUserApiRequest;
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

public class InicioSesionController {

    @FXML
    private StackPane btnCerrarApp;

    @FXML
    private Button btnIniciarSesión;

    @FXML
    private Label btnRegistrar;

    @FXML
    private Label lblMensajeError;

    @FXML
    private TextField txtEmail;

    private final TaskuApiClient apiClient = new TaskuApiClient();

    @FXML
    void handleAbrirPanelregistro(MouseEvent event) {
        SceneManager.getInstance().switchTo("Registro");
    }

    @FXML
    void handleCerrarApp(MouseEvent event) {
        Platform.exit();
        System.exit(0); 
    }

    @FXML
    void handleIniciarSesion(ActionEvent event) {
        
         
        String emailStr = normalize(txtEmail.getText());

        if (emailStr.isBlank()) {
            showError("Por favor, introduce tu email.");
            return;
        }
        
        try {
            LoginUserApiRequest request = new LoginUserApiRequest(emailStr);
            apiClient.loginUser(request);

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
