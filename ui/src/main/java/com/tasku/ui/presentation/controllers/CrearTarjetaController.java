package com.tasku.ui.presentation.controllers;

import com.tasku.ui.client.dto.TipoTarjeta;
import com.tasku.ui.client.dto.request.CardLabelApiRequest;
import com.tasku.ui.client.dto.request.CreateCardApiRequest;
import com.tasku.ui.client.dto.response.BoardApiResponse;
import com.tasku.ui.client.dto.response.BoardListApiResponse;
import com.tasku.ui.client.dto.response.CardApiResponse;
import com.tasku.ui.SceneManager;
import com.tasku.ui.client.http.DesktopApiException;
import com.tasku.ui.client.http.TaskuApiClient;
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
import javafx.util.StringConverter;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class CrearTarjetaController {
    @FXML
    private TextField titleField;

    @FXML
    private ChoiceBox<BoardListApiResponse> listChoiceBox;

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
    private Consumer<UUID> onCardCreated;

    @FXML
    private void initialize() {
        choiceEtiqueta.setItems(FXCollections.observableArrayList("General", "Urgente", "Bloqueada", "Bug"));
        choiceEtiqueta.getSelectionModel().selectFirst();

        listChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(BoardListApiResponse list) {
                return list == null ? "" : list.name();
            }

            @Override
            public BoardListApiResponse fromString(String string) {
                return null;
            }
        });

        listChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, old, current) -> {
            if (current != null) {
                SceneManager.getInstance().setCurrentListId(current.id());
            }
        });

        preloadListsFromContext();
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

        BoardListApiResponse selectedList = listChoiceBox.getValue();
        if (selectedList == null) {
            showError("Selecciona la lista donde crear la tarjeta.");
            return;
        }

        UUID listId = selectedList.id();

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
            SceneManager.getInstance().setCurrentListId(response.listId());
            showSuccess("Tarjeta creada correctamente: " + response.id());
            clearForm();
            selectListById(response.listId());
            if (onCardCreated != null) {
                onCardCreated.accept(response.listId());
            }
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

    private void preloadListsFromContext() {
        String boardUrl = SceneManager.getInstance().getCurrentBoardUrl();
        if (boardUrl == null || boardUrl.isBlank()) {
            return;
        }
        try {
            BoardApiResponse board = apiClient.getBoardByUrl(boardUrl);
            loadAvailableLists(board.lists(), SceneManager.getInstance().getCurrentListId());
        } catch (DesktopApiException ex) {
            showError("No se pudieron cargar las listas: " + ex.getMessage());
        }
    }

    public void loadAvailableLists(List<BoardListApiResponse> lists, UUID preferredId) {
        if (lists == null) {
            listChoiceBox.setItems(FXCollections.observableArrayList());
            return;
        }
        listChoiceBox.setItems(FXCollections.observableArrayList(lists));
        if (!lists.isEmpty()) {
            if (preferredId != null) {
                for (BoardListApiResponse list : lists) {
                    if (preferredId.equals(list.id())) {
                        listChoiceBox.getSelectionModel().select(list);
                        return;
                    }
                }
            }
            listChoiceBox.getSelectionModel().selectFirst();
        }
    }

    public void setOnCardCreated(Consumer<UUID> onCardCreated) {
        this.onCardCreated = onCardCreated;
    }

    private void selectListById(UUID listId) {
        if (listId == null) {
            return;
        }
        for (BoardListApiResponse list : listChoiceBox.getItems()) {
            if (listId.equals(list.id())) {
                listChoiceBox.getSelectionModel().select(list);
                return;
            }
        }
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

