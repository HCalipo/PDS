package com.tasku.ui.presentation.controllers;

import com.tasku.ui.client.dto.request.CreateBoardApiRequest;
import com.tasku.ui.client.dto.request.InitialListApiRequest;
import com.tasku.ui.client.dto.response.BoardApiResponse;
import com.tasku.ui.SceneManager;
import com.tasku.ui.client.http.DesktopApiException;
import com.tasku.ui.client.http.TaskuApiClient;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.function.Consumer;

public class AñadirTableroController {

    @FXML
    private VBox templateBlank;

    @FXML
    private VBox template1;

    @FXML
    private VBox template2;

    @FXML
    private TextField boardNameField;

    @FXML
    private Label lblBoardResult;

    private static final String ACTIVE_CLASS = "template-selector-active";
    private final TaskuApiClient apiClient = new TaskuApiClient();
    private Consumer<BoardApiResponse> onBoardCreated;

    @FXML
    private void onTemplateSelected(javafx.scene.input.MouseEvent event) {
        Node selected = event.getPickResult().getIntersectedNode();
        
        VBox templateCard = findParentVBox(selected);
        if (templateCard == null) return;

        clearAllActiveStates();
        templateCard.getStyleClass().add(ACTIVE_CLASS);
    }

    private VBox findParentVBox(Node node) {
        Node current = node;
        while (current != null) {
            if (current instanceof VBox) {
                VBox vbox = (VBox) current;
                if (vbox.getStyleClass().contains("template-selector-card")) {
                    return vbox;
                }
            }
            current = current.getParent();
        }
        return null;
    }

    private void clearAllActiveStates() {
        templateBlank.getStyleClass().remove(ACTIVE_CLASS);
        template1.getStyleClass().remove(ACTIVE_CLASS);
        template2.getStyleClass().remove(ACTIVE_CLASS);
    }

    @FXML
    private void handleCreateBoard() {
        String ownerEmail = SceneManager.getInstance().getCurrentUserEmail();
        if (ownerEmail == null || ownerEmail.isBlank()) {
            showError("Primero debes iniciar sesion.");
            return;
        }

        String boardName = normalize(boardNameField.getText());
        if (boardName.isBlank()) {
            showError("El nombre del tablero es obligatorio.");
            return;
        }

        CreateBoardApiRequest request = new CreateBoardApiRequest(
                ownerEmail,
                boardName,
                selectedBoardColor(),
                "Tablero creado desde la interfaz desktop",
                selectedInitialLists()
        );

        try {
            BoardApiResponse board = apiClient.createBoard(request);
            SceneManager.getInstance().setCurrentBoard(board.url(), board.name());
            if (board.lists() != null && !board.lists().isEmpty()) {
                SceneManager.getInstance().setCurrentListId(board.lists().get(0).id());
            }
            if (onBoardCreated != null) {
                onBoardCreated.accept(board);
            }
            showSuccess("Tablero creado correctamente: " + board.url());
            boardNameField.clear();
            Stage stage = (Stage) boardNameField.getScene().getWindow();
            stage.close();
        } catch (DesktopApiException ex) {
            showError("No se pudo crear el tablero: " + ex.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) boardNameField.getScene().getWindow();
        stage.close();
    }

    public void setOnBoardCreated(Consumer<BoardApiResponse> onBoardCreated) {
        this.onBoardCreated = onBoardCreated;
    }

    private List<InitialListApiRequest> selectedInitialLists() {
        if (template1.getStyleClass().contains(ACTIVE_CLASS)) {
            // Kanban: flujo de trabajo con columna de bloqueo
            return List.of(
                    new InitialListApiRequest("Pendientes", 80),
                    new InitialListApiRequest("En progreso", 40),
                    new InitialListApiRequest("Bloqueado", 80)
            );
        }
        if (template2.getStyleClass().contains(ACTIVE_CLASS)) {
            // Scrum: backlog → sprint activo → revisión QA
            return List.of(
                    new InitialListApiRequest("Backlog", 60),
                    new InitialListApiRequest("Sprint", 50),
                    new InitialListApiRequest("QA", 50)
            );
        }
        // En blanco
        return List.of(new InitialListApiRequest("General", 100));
    }

    private String selectedBoardColor() {
        if (template1.getStyleClass().contains(ACTIVE_CLASS)) {
            return "#0ea5e9";
        }
        if (template2.getStyleClass().contains(ACTIVE_CLASS)) {
            return "#f97316";
        }
        return "#22c55e";
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private void showError(String message) {
        lblBoardResult.setStyle("-fx-text-fill: #d63031;");
        lblBoardResult.setText(message);
    }

    private void showSuccess(String message) {
        lblBoardResult.setStyle("-fx-text-fill: #0ba360;");
        lblBoardResult.setText(message);
    }
}
