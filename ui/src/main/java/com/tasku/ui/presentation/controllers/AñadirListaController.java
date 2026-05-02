package com.tasku.ui.presentation.controllers;

import com.tasku.ui.SceneManager;
import com.tasku.ui.client.dto.request.CreateListApiRequest;
import com.tasku.ui.client.dto.response.BoardApiResponse;
import com.tasku.ui.client.http.TaskuApiClient;
import com.tasku.ui.client.http.DesktopApiException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class AñadirListaController {

    @FXML
    private TextField txtNombre;

    private final TaskuApiClient apiClient = new TaskuApiClient();
    private String boardUrl;
    private Consumer<BoardApiResponse> onListCreated;

    public void setBoardUrl(String boardUrl) {
        this.boardUrl = boardUrl;
    }

    public void setOnListCreated(Consumer<BoardApiResponse> onListCreated) {
        this.onListCreated = onListCreated;
    }

    @FXML
    private void handleCreate() {
        String nombre = txtNombre.getText();
        if (nombre == null || nombre.isBlank()) {
            showAlert("El nombre de la lista es obligatorio.", Alert.AlertType.WARNING);
            return;
        }

        if (boardUrl == null || boardUrl.isBlank()) {
            showAlert("No se ha seleccionado un tablero.", Alert.AlertType.WARNING);
            return;
        }

        try {
            BoardApiResponse updatedBoard = apiClient.createList(new CreateListApiRequest(boardUrl, nombre, 100));
            
            if (onListCreated != null) {
                onListCreated.accept(updatedBoard);
            }
            
            txtNombre.clear();
            closeDialog();
        } catch (DesktopApiException ex) {
            showAlert("Error al crear lista: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("TaskU");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
