package com.tasku.ui.presentation.controllers;

import com.tasku.ui.client.dto.TipoTarjeta;
import com.tasku.ui.client.dto.request.CardLabelApiRequest;
import com.tasku.ui.client.dto.request.CreateCardApiRequest;
import com.tasku.ui.client.dto.response.CardApiResponse;
import com.tasku.ui.client.http.DesktopApiException;
import com.tasku.ui.client.http.TaskuApiClient;
import com.tasku.ui.state.DesktopSessionState;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CrearTarjetaController {
    @FXML
    private TextField titleField;

    @FXML
    private TextField listIdField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private ToggleButton toggleTipo;

    @FXML
    private ToggleGroup tipoCardGroup;

    @FXML
    private ChoiceBox<String> choiceEtiqueta;

    @FXML
    private Label lblCardResult;

    @FXML
    private VBox checklistPanel;

    @FXML
    private VBox checklistItemsContainer;

    private final TaskuApiClient apiClient = new TaskuApiClient();

    @FXML
    private void initialize() {
        choiceEtiqueta.setItems(FXCollections.observableArrayList("General", "Urgente", "Bloqueada", "Bug"));
        choiceEtiqueta.getSelectionModel().selectFirst();

        UUID currentListId = DesktopSessionState.getCurrentListId();
        if (currentListId != null) {
            listIdField.setText(currentListId.toString());
        }
    }

    @FXML
    private void handleCancel() {
        clearForm();
        lblCardResult.setText("");
    }

    @FXML
    private void handleCreate() {
        String title = normalize(titleField.getText());
        String description = normalize(descriptionArea.getText());

        if (title.isBlank()) {
            showError("El titulo es obligatorio.");
            return;
        }

        if (description.isBlank()) {
            showError("La descripcion es obligatoria.");
            return;
        }

        UUID listId;
        try {
            listId = UUID.fromString(normalize(listIdField.getText()));
        } catch (IllegalArgumentException ex) {
            showError("Debes indicar un UUID de lista valido.");
            return;
        }

        TipoTarjeta type = toggleTipo.isSelected() ? TipoTarjeta.TAREA : TipoTarjeta.CHECKLIST;
        String selectedLabel = choiceEtiqueta.getValue();

        Set<CardLabelApiRequest> labels = new LinkedHashSet<>();
        if (selectedLabel != null && !selectedLabel.isBlank()) {
            labels.add(new CardLabelApiRequest(selectedLabel, mapColorForLabel(selectedLabel)));
        }

        CreateCardApiRequest request = new CreateCardApiRequest(
                listId,
                type,
                title,
                description,
                labels,
                List.of()
        );

        try {
            CardApiResponse response = apiClient.createCard(request);
            DesktopSessionState.setCurrentListId(response.listId());
            showSuccess("Tarjeta creada correctamente: " + response.id());
            clearForm();
            listIdField.setText(response.listId().toString());
        } catch (DesktopApiException ex) {
            showError("No se pudo crear la tarjeta: " + ex.getMessage());
        }
    }

    @FXML
    private void handleAddChecklistItem() {
        showInfo("El alta de items de checklist se hace por API en un paso posterior.");
    }

    @FXML
    private void handleNuevaEtiqueta() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nueva etiqueta");
        dialog.setHeaderText("Crear etiqueta local para esta tarjeta");
        dialog.setContentText("Nombre:");
        dialog.showAndWait().ifPresent(value -> {
            String normalized = normalize(value);
            if (!normalized.isBlank()) {
                if (!choiceEtiqueta.getItems().contains(normalized)) {
                    choiceEtiqueta.getItems().add(normalized);
                }
                choiceEtiqueta.setValue(normalized);
            }
        });
    }

    private void clearForm() {
        titleField.clear();
        descriptionArea.clear();
        if (!choiceEtiqueta.getItems().isEmpty()) {
            choiceEtiqueta.getSelectionModel().selectFirst();
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String mapColorForLabel(String label) {
        String lower = label.toLowerCase();
        return switch (lower) {
            case "urgente" -> "#dc2626";
            case "bloqueada" -> "#f97316";
            case "bug" -> "#9333ea";
            default -> "#0ea5e9";
        };
    }

    private void showError(String message) {
        lblCardResult.setStyle("-fx-text-fill: #d63031;");
        lblCardResult.setText(message);
    }

    private void showSuccess(String message) {
        lblCardResult.setStyle("-fx-text-fill: #0ba360;");
        lblCardResult.setText(message);
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacion");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

