package com.tasku.ui.presentation.controllers;

import com.tasku.ui.client.dto.TipoTarjeta;
import com.tasku.ui.client.dto.request.CardLabelApiRequest;
import com.tasku.ui.client.dto.request.ChecklistItemApiRequest;
import com.tasku.ui.client.dto.request.CreateCardApiRequest;
import com.tasku.ui.client.dto.response.BoardApiResponse;
import com.tasku.ui.client.dto.response.BoardListApiResponse;
import com.tasku.ui.client.dto.response.CardApiResponse;
import com.tasku.ui.SceneManager;
import com.tasku.ui.client.http.DesktopApiException;
import com.tasku.ui.client.http.TaskuApiClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
    private VBox tareasPanel;

    @FXML
    private VBox taskListContainer;

    @FXML
    private TextField taskInputField;

    private final TaskuApiClient apiClient = new TaskuApiClient();
    private Consumer<UUID> onCardCreated;
    private final Map<String, String> labelColors = new LinkedHashMap<>();
    private final List<String> tasks = new ArrayList<>();

    @FXML
    private void initialize() {
        labelColors.put("General",  "#0ea5e9");
        labelColors.put("Urgente",  "#dc2626");
        labelColors.put("Bloqueada","#f97316");
        labelColors.put("Bug",      "#9333ea");
        choiceEtiqueta.setItems(FXCollections.observableArrayList(labelColors.keySet()));
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

        tipoCardGroup.selectedToggleProperty().addListener((obs, old, newVal) -> {
            boolean isChecklist = newVal != null && newVal != toggleTipo;
            tareasPanel.setVisible(isChecklist);
            tareasPanel.setManaged(isChecklist);
            Platform.runLater(() -> {
                if (tareasPanel.getParent() != null) {
                    tareasPanel.getParent().requestLayout();
                }
            });
        });

        preloadListsFromContext();
    }

    @FXML
    private void handleCancel() {
        closeDialog();
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
            labels.add(new CardLabelApiRequest(selectedLabel, labelColors.getOrDefault(selectedLabel, "#0ea5e9")));
        }

        List<ChecklistItemApiRequest> checklistItems = new ArrayList<>();
        if (type == TipoTarjeta.CHECKLIST) {
            for (String task : tasks) {
                checklistItems.add(new ChecklistItemApiRequest(task, false));
            }
        }

        String authorEmail = SceneManager.getInstance().getCurrentUserEmail();
        CreateCardApiRequest request = new CreateCardApiRequest(
                listId,
                type,
                title,
                description,
                labels,
                checklistItems,
                authorEmail
        );

        try {
            CardApiResponse response = apiClient.createCard(request);
            SceneManager.getInstance().setCurrentListId(response.listId());
            if (onCardCreated != null) {
                onCardCreated.accept(response.listId());
            }
            closeDialog();
        } catch (DesktopApiException ex) {
            showError("No se pudo crear la tarjeta: " + ex.getMessage());
        }
    }

    @FXML
    private void handleAddTask() {
        String task = taskInputField.getText().trim();
        if (task.isBlank()) return;
        tasks.add(task);
        taskInputField.clear();
        refreshTaskList();
    }

    private void refreshTaskList() {
        taskListContainer.getChildren().clear();
        for (String task : new ArrayList<>(tasks)) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);
            Label lbl = new Label(task);
            lbl.setStyle("-fx-text-fill: #334155; -fx-font-size: 13px;");
            lbl.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(lbl, Priority.ALWAYS);
            Button btnRemove = new Button("×");
            btnRemove.getStyleClass().add("btn-add-small");
            btnRemove.setOnAction(e -> {
                tasks.remove(task);
                refreshTaskList();
            });
            row.getChildren().addAll(lbl, btnRemove);
            taskListContainer.getChildren().add(row);
        }
    }

    @FXML
    private void handleNuevaEtiqueta() {
        CreateTagController tagController = SceneManager.getInstance()
                .openDialogAndGetController("CreateTag", null);
        if (tagController == null || tagController.getResultName() == null) {
            return;
        }
        String name  = tagController.getResultName();
        String color = tagController.getResultColor();
        labelColors.put(name, color);
        if (!choiceEtiqueta.getItems().contains(name)) {
            choiceEtiqueta.getItems().add(name);
        }
        choiceEtiqueta.setValue(name);
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

    private void showError(String message) {
        lblCardResult.setStyle("-fx-text-fill: #d63031;");
        lblCardResult.setText(message);
    }

    private void closeDialog() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }
}

