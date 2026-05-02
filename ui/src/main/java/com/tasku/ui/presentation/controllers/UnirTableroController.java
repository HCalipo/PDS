package com.tasku.ui.presentation.controllers;

import java.util.function.Consumer;

import com.tasku.ui.SceneManager;
import com.tasku.ui.client.dto.request.JoinBoardApiRequest;
import com.tasku.ui.client.http.DesktopApiException;
import com.tasku.ui.client.http.TaskuApiClient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UnirTableroController {

    @FXML
    private TextField txtUrl;
    
    @FXML
    private Label lblMensajeError;

    private final TaskuApiClient apiClient = new TaskuApiClient();
    private Consumer<String> onJoinedAvisar;


    @FXML
    void handleUnirse(ActionEvent event) {

        String input = txtUrl.getText();
        if (input == null || input.isBlank()) {
            showError("Por favor, introduce el enlace o código del tablero.");
            return;
        }

        String boardUrl = input.substring(input.lastIndexOf('/') + 1).trim();
        String currentUserEmail = SceneManager.getInstance().getCurrentUserEmail();

        if (currentUserEmail == null || currentUserEmail.isBlank()) {
            showError("Error de sesión: No se ha detectado tu usuario.");
            return;
        }

        try {
            
            JoinBoardApiRequest request = new JoinBoardApiRequest(currentUserEmail);
            apiClient.joinBoard(boardUrl, request);
            if (onJoinedAvisar != null) {
                onJoinedAvisar.accept(boardUrl);
            }

            txtUrl.clear();
            cerrarVentana(event);

        } catch (DesktopApiException ex) {
            showError(ex.getMessage());
        } catch (Exception ex) {
            showError("Se ha producido un error inesperado de conexión.");
            ex.printStackTrace();
        }
    }
    

    @FXML
    void handleCancel(ActionEvent event) {
        cerrarVentana(event);
    }

    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) txtUrl.getScene().getWindow();
        stage.close();
    }

    

    public void setOnJoinedAvisar(Consumer<String> aviso) {
        this.onJoinedAvisar = aviso;
    }


    private void showError(String message) {
        if (lblMensajeError != null) {
            lblMensajeError.setStyle("-fx-text-fill: #d63031;");
            lblMensajeError.setText(message);
        } else {
            
            System.err.println("Error (Unir Tablero): " + message);
        }
    }
}
