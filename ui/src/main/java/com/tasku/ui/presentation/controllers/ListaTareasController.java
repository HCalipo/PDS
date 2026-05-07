package com.tasku.ui.presentation.controllers;

import com.tasku.ui.SceneManager;
import com.tasku.ui.client.dto.TipoTarjeta;
import com.tasku.ui.client.dto.request.ToggleChecklistItemApiRequest;
import com.tasku.ui.client.dto.response.CardApiResponse;
import com.tasku.ui.client.dto.response.CardLabelApiResponse;
import com.tasku.ui.client.dto.response.ChecklistItemApiResponse;
import com.tasku.ui.client.http.DesktopApiException;
import com.tasku.ui.client.http.TaskuApiClient;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ListaTareasController {

    public interface CardDropHandler {
        void handle(UUID cardId, UUID sourceListId, UUID targetListId);
    }

    public interface CardCompletedHandler {
        void handle(UUID cardId, UUID sourceListId);
    }

    public interface ColumnReorderHandler {
        void handle(UUID draggedListId, UUID targetListId);
    }

    public interface ListDeletedHandler {
        void handle(UUID listId);
    }

    public interface ListRenamedHandler {
        void handle(UUID listId, String newName);
    }

    private static final DataFormat CARD_ID_FORMAT   = new DataFormat("application/x-tasku-card-id");
    private static final DataFormat LIST_ID_FORMAT   = new DataFormat("application/x-tasku-list-id");
    private static final DataFormat COLUMN_ID_FORMAT = new DataFormat("application/x-tasku-column-list-id");

    @FXML private HBox columnHeader;
    @FXML private Label columnTitle;
    @FXML private Label taskCounter;
    @FXML private VBox tasksContainer;
    @FXML private VBox emptyState;
    @FXML private javafx.scene.control.Button btnRenameList;
    @FXML private javafx.scene.control.Button btnDeleteList;
    @FXML private javafx.scene.control.Button btnCrearTareaVacio;

    private UUID listId;
    private String listName = "";
    private boolean editingEnabled = true;
    private final Map<UUID, CardApiResponse> cardsById = new LinkedHashMap<>();
    private CardDropHandler onCardDropped;
    private CardCompletedHandler onCardCompleted;
    private ColumnReorderHandler onColumnReorder;
    private ListDeletedHandler onListDeleted;
    private ListRenamedHandler onListRenamed;
    private Runnable onCreateCard;
    private String savedHeaderStyle = "";
    private boolean headerDropActive = false;
    private final TaskuApiClient apiClient = new TaskuApiClient();

    public void setTitulo(String nombre) {
        if (columnTitle != null) {
            columnTitle.setText(nombre != null ? nombre.toUpperCase() : "");
        }
        if (nombre != null) {
            this.listName = nombre;
        }
    }

    public void setHeaderColor(String colorHex) {
        if (columnHeader == null || colorHex == null || colorHex.isBlank()) return;

        columnHeader.setStyle(
                "-fx-background-color: " + colorHex + ";" +
                "-fx-background-radius: 12 12 0 0;");

        boolean light = isLightColor(colorHex);
        String textColor   = light ? "#1e293b" : "#ffffff";
        String badgeBg     = light ? "rgba(0,0,0,0.10)" : "rgba(255,255,255,0.22)";
        String badgeBorder = light ? "rgba(0,0,0,0.08)" : "rgba(255,255,255,0.18)";

        columnTitle.setStyle(
                "-fx-text-fill: " + textColor + ";" +
                "-fx-font-weight: 800;" +
                "-fx-effect: dropshadow(three-pass-box, " +
                (light ? "rgba(255,255,255,0.4)" : "rgba(0,0,0,0.25)") +
                ", 0, 0, 0, 1);");

        taskCounter.setStyle(
                "-fx-background-color: " + badgeBg + ";" +
                "-fx-text-fill: " + textColor + ";" +
                "-fx-border-color: " + badgeBorder + ";");
    }

    private boolean isLightColor(String hex) {
        if (hex == null || !hex.startsWith("#") || hex.length() != 7) return true;
        try {
            double r = Integer.parseInt(hex.substring(1, 3), 16) / 255.0;
            double g = Integer.parseInt(hex.substring(3, 5), 16) / 255.0;
            double b = Integer.parseInt(hex.substring(5, 7), 16) / 255.0;
            double[] ch = {r, g, b};
            for (int i = 0; i < 3; i++) {
                ch[i] = ch[i] <= 0.04045 ? ch[i] / 12.92 : Math.pow((ch[i] + 0.055) / 1.055, 2.4);
            }
            double luminance = 0.2126 * ch[0] + 0.7152 * ch[1] + 0.0722 * ch[2];
            return luminance > 0.179;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    public void setListId(UUID listId) {
        this.listId = listId;
        setupDropTargets();
        setupColumnHeaderDrag();
    }

    public UUID getListId() {
        return listId;
    }

    public void setOnCardDropped(CardDropHandler onCardDropped) {
        this.onCardDropped = onCardDropped;
    }

    public void setOnCardCompleted(CardCompletedHandler onCardCompleted) {
        this.onCardCompleted = onCardCompleted;
    }

    public void setOnColumnReorder(ColumnReorderHandler handler) {
        this.onColumnReorder = handler;
    }

    public void setOnCreateCard(Runnable onCreateCard) {
        this.onCreateCard = onCreateCard;
    }

    public void setOnListDeleted(ListDeletedHandler handler) {
        this.onListDeleted = handler;
    }

    public void setOnListRenamed(ListRenamedHandler handler) {
        this.onListRenamed = handler;
    }

    public void setEditingEnabled(boolean canEdit) {
        this.editingEnabled = canEdit;
        if (btnRenameList != null) {
            btnRenameList.setVisible(canEdit);
            btnRenameList.setManaged(canEdit);
        }
        if (btnDeleteList != null) {
            btnDeleteList.setVisible(canEdit);
            btnDeleteList.setManaged(canEdit);
        }
        if (btnCrearTareaVacio != null) {
            btnCrearTareaVacio.setDisable(!canEdit);
        }
        updateCards(java.util.List.copyOf(cardsById.values()));
    }

    @FXML
    private void handleRenameList() {
        TextInputDialog dialog = new TextInputDialog(listName);
        dialog.setTitle("Renombrar lista");
        dialog.setHeaderText(null);
        dialog.setContentText("Nuevo nombre:");
        dialog.showAndWait().ifPresent(newName -> {
            String trimmed = newName.trim();
            if (!trimmed.isBlank() && onListRenamed != null) {
                onListRenamed.handle(listId, trimmed);
            }
        });
    }

    @FXML
    private void handleDeleteList() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar lista");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Eliminar la lista \"" + listName + "\" y todas sus tarjetas?");
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK && onListDeleted != null) {
                onListDeleted.handle(listId);
            }
        });
    }

    @FXML
    private void handleCrearTarea() {
        if (!editingEnabled) return;
        if (listId != null) {
            SceneManager.getInstance().setCurrentListId(listId);
        }
        if (onCreateCard != null) {
            onCreateCard.run();
        }
    }

    public void updateCards(List<CardApiResponse> cards) {
        if (tasksContainer == null) {
            return;
        }

        cardsById.clear();
        tasksContainer.getChildren().clear();

        if (cards != null) {
            for (CardApiResponse card : cards) {
                if (card != null && card.id() != null) {
                    cardsById.put(card.id(), card);
                }
            }
        }

        if (cardsById.isEmpty()) {
            renderEmptyState();
            updateCounter();
            return;
        }

        for (CardApiResponse card : cardsById.values()) {
            Node cardNode = buildCardNode(card);
            setupCardDrag(cardNode, card);
            tasksContainer.getChildren().add(cardNode);
        }
        updateCounter();
    }

    public CardApiResponse removeCard(UUID cardId) {
        if (cardId == null) {
            return null;
        }
        CardApiResponse removed = cardsById.remove(cardId);
        if (removed == null) {
            return null;
        }
        removeCardNode(cardId);
        updateCounter();
        if (cardsById.isEmpty()) {
            renderEmptyState();
        }
        return removed;
    }



    public void addCard(CardApiResponse card) {
        if (card == null || card.id() == null || tasksContainer == null) {
            return;
        }
        cardsById.put(card.id(), card);
        removeEmptyState();
        Node cardNode = buildCardNode(card);
        setupCardDrag(cardNode, card);
        tasksContainer.getChildren().add(cardNode);
        updateCounter();
    }

    private Node buildCardNode(CardApiResponse card) {
        // StackPane como contenedor principal para posicionamiento absoluto
        StackPane cardPane = new StackPane();
        cardPane.getStyleClass().add("task-card");
        cardPane.setUserData(card.id());

        String accentColor = firstLabelColor(card);
        if (!accentColor.isBlank()) {
            cardPane.setStyle("-fx-border-color: " + accentColor + "; -fx-border-width: 2;");
        }

        // Contenedor interno para el contenido (excepto el menú)
        VBox contentBox = new VBox();
        contentBox.setMouseTransparent(false);

        // Título
        Label title = new Label(safe(card.title()));
        title.getStyleClass().add("task-card-title");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(javafx.geometry.Pos.CENTER);
        title.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        title.setWrapText(true);
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        contentBox.getChildren().add(title);

        // Nombre de etiqueta
        if (card.labels() != null && !card.labels().isEmpty()) {
            HBox labelsBox = new HBox();
            labelsBox.setAlignment(javafx.geometry.Pos.CENTER);
            labelsBox.getStyleClass().add("task-card-labels");
            for (CardLabelApiResponse label : card.labels()) {
                String labelName = safe(label.name());
                if (labelName.isBlank()) continue;
                Label chip = new Label(labelName);
                chip.getStyleClass().add("task-card-label");
                String color = safe(label.colorHex());
                if (!color.isBlank()) {
                    chip.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;");
                }
                labelsBox.getChildren().add(chip);
            }
            if (!labelsBox.getChildren().isEmpty()) {
                contentBox.getChildren().add(labelsBox);
            }
        }

        // Checklist items
        if (card.type() == TipoTarjeta.CHECKLIST
                && card.checklistItems() != null
                && !card.checklistItems().isEmpty()) {

            long done = card.checklistItems().stream()
                    .filter(ChecklistItemApiResponse::completed).count();
            int total = card.checklistItems().size();

            Label progress = new Label(done + " / " + total);
            progress.getStyleClass().add("checklist-progress");
            contentBox.getChildren().add(progress);

            ProgressBar progressBar = new ProgressBar(total > 0 ? (double) done / total : 0);
            progressBar.setMaxWidth(Double.MAX_VALUE);
            progressBar.getStyleClass().add("progress-bar-mini");
            contentBox.getChildren().add(progressBar);

            VBox checklistBox = new VBox(4);
            checklistBox.getStyleClass().add("checklist-items");
            List<ChecklistItemApiResponse> items = card.checklistItems();
            for (int i = 0; i < items.size(); i++) {
                final int idx = i;
                CheckBox cb = new CheckBox(safe(items.get(i).description()));
                cb.setSelected(items.get(i).completed());
                cb.setOnAction(e -> {
                    cb.setDisable(true);
                    try {
                        CardApiResponse updated = apiClient.toggleChecklistItem(
                                new ToggleChecklistItemApiRequest(card.id(), idx, cb.isSelected()));
                        refreshCardNode(updated);
                    } catch (DesktopApiException ex) {
                        cb.setSelected(!cb.isSelected());
                        cb.setDisable(false);
                    }
                });
                checklistBox.getChildren().add(cb);
            }
            contentBox.getChildren().add(checklistBox);
        }

        // Descripción
        String descriptionValue = safe(card.description());
        if (!descriptionValue.isBlank()) {
            Label description = new Label(descriptionValue);
            description.setWrapText(true);
            description.getStyleClass().add("task-card-description");
            contentBox.getChildren().add(description);
        }

        // Botón completar
        javafx.scene.control.Button completeBtn = new javafx.scene.control.Button("✓ Completar");
        completeBtn.getStyleClass().add("btn-complete-card");
        completeBtn.setMaxWidth(Double.MAX_VALUE);
        completeBtn.setOnAction(e -> {
            if (listId != null && onCardCompleted != null) {
                completeBtn.setDisable(true);
                onCardCompleted.handle(card.id(), listId);
            }
        });
        contentBox.getChildren().add(completeBtn);

    //menu de 3 puntos 
    StackPane menuButton = new StackPane();
    menuButton.getStyleClass().add("card-menu-button");
    StackPane.setAlignment(menuButton, javafx.geometry.Pos.TOP_RIGHT);
    menuButton.setMaxSize(javafx.scene.layout.Region.USE_PREF_SIZE, javafx.scene.layout.Region.USE_PREF_SIZE);
    menuButton.setPadding(new javafx.geometry.Insets(8, 16, 8, 16));
    StackPane.setMargin(menuButton, new javafx.geometry.Insets(4, 4, 0, 0));
    
    SVGPath btnMenuPuntos = new SVGPath();
    btnMenuPuntos.setContent("M 6 10 C 5.726562 10 5.488281 9.902344 5.292969 9.707031 C 5.097656 9.511719 5 9.273438 5 9 C 5 8.726562 5.097656 8.488281 5.292969 8.292969 C 5.488281 8.097656 5.726562 8 6 8 C 6.273438 8 6.511719 8.097656 6.707031 8.292969 C 6.902344 8.488281 7 8.726562 7 9 C 7 9.273438 6.902344 9.511719 6.707031 9.707031 C 6.511719 9.902344 6.273438 10 6 10 Z M 6 7 C 5.726562 7 5.488281 6.902344 5.292969 6.707031 C 5.097656 6.511719 5 6.273438 5 6 C 5 5.726562 5.097656 5.488281 5.292969 5.292969 C 5.488281 5.097656 5.726562 5 6 5 C 6.273438 5 6.511719 5.097656 6.707031 5.292969 C 6.902344 5.488281 7 5.726562 7 6 C 7 6.273438 6.902344 6.511719 6.707031 6.707031 C 6.511719 6.902344 6.273438 7 6 7 Z M 6 4 C 5.726562 4 5.488281 3.902344 5.292969 3.707031 C 5.097656 3.511719 5 3.273438 5 3 C 5 2.726562 5.097656 2.488281 5.292969 2.292969 C 5.488281 2.097656 5.726562 2 6 2 C 6.273438 2 6.511719 2.097656 6.707031 2.292969 C 6.902344 2.488281 7 2.726562 7 3 C 7 3.273438 6.902344 3.511719 6.707031 3.707031 C 6.511719 3.902344 6.273438 4 6 4 Z M 6 4");
    btnMenuPuntos.setFill(javafx.scene.paint.Color.web("#6b778c"));
    menuButton.getChildren().add(btnMenuPuntos);
    
    
    menuButton.setOnMouseClicked(e -> {
        ContextMenu contextMenu = new ContextMenu();
        if (editingEnabled) {
            MenuItem editItem = new MenuItem("Editar");
            editItem.setOnAction(ev -> handleEditCard(card));
            MenuItem deleteItem = new MenuItem("Eliminar");
            deleteItem.setOnAction(ev -> handleDeleteCard(card));
            contextMenu.getItems().addAll(editItem, deleteItem);
        }
        if (!contextMenu.getItems().isEmpty()) {
            contextMenu.show(menuButton, javafx.geometry.Side.BOTTOM, 0, 0);
        }
    });
    
    // Cursor de mano al pasar por encima
    //menuButton.setOnMouseEntered(e -> menuButton.setCursor(javafx.scene.Cursor.HAND));
    //menuButton.setOnMouseExited(e -> menuButton.setCursor(javafx.scene.Cursor.DEFAULT));

    // Añadir contenido y menú al StackPane principal
    cardPane.getChildren().addAll(contentBox, menuButton);

    return cardPane;
    }

    private void handleEditCard(CardApiResponse card) {
        TextInputDialog dialog = new TextInputDialog(card.title());
        dialog.setTitle("Editar tarjeta");
        dialog.setHeaderText(null);
        dialog.setContentText("Nuevo título:");
        dialog.showAndWait().ifPresent(newTitle -> {
            String trimmed = newTitle.trim();
            if (trimmed.isBlank()) return;
            try {
                CardApiResponse updated = apiClient.renameCard(card.id(), trimmed);
                refreshCardNode(updated);
            } catch (DesktopApiException ex) {
                showAlert("No se pudo editar la tarjeta: " + ex.getMessage());
            }
        });
    }

    private void handleDeleteCard(CardApiResponse card) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar tarjeta");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Eliminar la tarjeta \"" + card.title() + "\"?");
        confirm.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) return;
            try {
                apiClient.deleteCard(card.id());
                removeCard(card.id());
            } catch (DesktopApiException ex) {
                showAlert("No se pudo eliminar la tarjeta: " + ex.getMessage());
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("TaskU");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void refreshCardNode(CardApiResponse updatedCard) {
        cardsById.put(updatedCard.id(), updatedCard);
        for (int i = 0; i < tasksContainer.getChildren().size(); i++) {
            Node child = tasksContainer.getChildren().get(i);
            if (updatedCard.id().equals(child.getUserData())) {
                Node newNode = buildCardNode(updatedCard);
                setupCardDrag(newNode, updatedCard);
                tasksContainer.getChildren().set(i, newNode);
                return;
            }
        }
    }

    private String firstLabelColor(CardApiResponse card) {
        if (card.labels() == null) return "";
        for (CardLabelApiResponse label : card.labels()) {
            String color = safe(label.colorHex());
            if (!color.isBlank()) return color;
        }
        return "";
    }

    private void setupColumnHeaderDrag() {
        if (columnHeader == null || listId == null) return;

        columnHeader.setCursor(javafx.scene.Cursor.OPEN_HAND);

        columnHeader.setOnDragDetected(event -> {
            Dragboard db = columnHeader.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.put(COLUMN_ID_FORMAT, listId.toString());
            db.setContent(content);
            db.setDragView(columnHeader.snapshot(null, null));
            event.consume();
        });

        columnHeader.setOnDragOver(event -> {
            if (event.getGestureSource() != columnHeader && isColumnDrag(event.getDragboard())) {
                event.acceptTransferModes(TransferMode.MOVE);
                addHeaderDropStyle();
            }
            event.consume();
        });

        columnHeader.setOnDragExited(event -> {
            removeHeaderDropStyle();
            event.consume();
        });

        columnHeader.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (isColumnDrag(db)) {
                UUID draggedId = parseUuid((String) db.getContent(COLUMN_ID_FORMAT));
                if (draggedId != null && !draggedId.equals(listId) && onColumnReorder != null) {
                    onColumnReorder.handle(draggedId, listId);
                    success = true;
                }
            }
            event.setDropCompleted(success);
            removeHeaderDropStyle();
            event.consume();
        });

        columnHeader.setOnDragDone(event -> {
            columnHeader.setCursor(javafx.scene.Cursor.OPEN_HAND);
            event.consume();
        });
    }

    private boolean isColumnDrag(Dragboard db) {
        return db != null && db.hasContent(COLUMN_ID_FORMAT);
    }

    private void addHeaderDropStyle() {
        if (columnHeader == null || headerDropActive) return;
        savedHeaderStyle = columnHeader.getStyle() != null ? columnHeader.getStyle() : "";
        headerDropActive = true;
        columnHeader.setStyle(savedHeaderStyle +
                "; -fx-border-color: #0ba360; -fx-border-width: 0 0 0 4;");
    }

    private void removeHeaderDropStyle() {
        if (columnHeader == null || !headerDropActive) return;
        columnHeader.setStyle(savedHeaderStyle);
        headerDropActive = false;
    }

    private void setupDropTargets() {
        if (tasksContainer == null) {
            return;
        }

        attachDropHandlers(tasksContainer);
        if (emptyState != null) {
            attachDropHandlers(emptyState);
        }
    }

    private void attachDropHandlers(Node node) {
        node.setOnDragOver(event -> {
            if (event.getGestureSource() != node && listId != null && hasDragData(event.getDragboard())) {
                UUID sourceListId = parseUuid(readListId(event.getDragboard()));
                if (sourceListId != null && sourceListId.equals(listId)) {
                    return;
                }
                event.acceptTransferModes(TransferMode.MOVE);
                addDropStyle();
            }
            event.consume();
        });

        node.setOnDragExited(event -> {
            removeDropStyle();
            event.consume();
        });

        node.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (listId != null && hasDragData(dragboard)) {
                UUID cardId = parseUuid(readCardId(dragboard));
                UUID sourceListId = parseUuid(readListId(dragboard));
                if (cardId != null && sourceListId != null && !sourceListId.equals(listId) && onCardDropped != null) {
                    onCardDropped.handle(cardId, sourceListId, listId);
                    success = true;
                }
            }
            event.setDropCompleted(success);
            removeDropStyle();
            event.consume();
        });
    }

    private void setupCardDrag(Node cardNode, CardApiResponse card) {
        if (cardNode == null || card == null || card.id() == null) {
            return;
        }
        cardNode.setOnDragDetected(event -> {
            if (listId == null) {
                return;
            }
            Dragboard dragboard = cardNode.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.put(CARD_ID_FORMAT, card.id().toString());
            content.put(LIST_ID_FORMAT, listId.toString());
            dragboard.setContent(content);
            dragboard.setDragView(cardNode.snapshot(null, null));
            event.consume();
        });
    }

    private void updateCounter() {
        if (taskCounter != null) {
            taskCounter.setText(String.valueOf(cardsById.size()));
        }
    }

    private void renderEmptyState() {
        if (emptyState != null && tasksContainer != null) {
            tasksContainer.getChildren().add(emptyState);
        }
    }

    private void removeEmptyState() {
        if (emptyState != null && tasksContainer != null) {
            tasksContainer.getChildren().remove(emptyState);
        }
    }

    private void removeCardNode(UUID cardId) {
        if (tasksContainer == null || cardId == null) {
            return;
        }
        Node toRemove = null;
        for (Node child : tasksContainer.getChildren()) {
            if (cardId.equals(child.getUserData())) {
                toRemove = child;
                break;
            }
        }
        if (toRemove != null) {
            tasksContainer.getChildren().remove(toRemove);
        }
    }

    private boolean hasDragData(Dragboard dragboard) {
        return dragboard != null
                && dragboard.hasContent(CARD_ID_FORMAT)
                && dragboard.hasContent(LIST_ID_FORMAT);
    }

    private String readCardId(Dragboard dragboard) {
        Object value = dragboard.getContent(CARD_ID_FORMAT);
        return value == null ? null : value.toString();
    }

    private String readListId(Dragboard dragboard) {
        Object value = dragboard.getContent(LIST_ID_FORMAT);
        return value == null ? null : value.toString();
    }

    private UUID parseUuid(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private void addDropStyle() {
        if (tasksContainer != null && !tasksContainer.getStyleClass().contains("drop-target")) {
            tasksContainer.getStyleClass().add("drop-target");
        }
    }

    private void removeDropStyle() {
        if (tasksContainer != null) {
            tasksContainer.getStyleClass().remove("drop-target");
        }
    }

    public void filterCards(String query) {
        boolean showAll = query == null || query.isBlank();
        String lowerQuery = showAll ? "" : query.trim().toLowerCase();
        int visible = 0;
        for (Node node : tasksContainer.getChildren()) {
            if (node == emptyState) continue;
            UUID id = node.getUserData() instanceof UUID u ? u : null;
            if (id == null) continue;
            CardApiResponse card = cardsById.get(id);
            boolean matches = showAll || (card != null && card.title() != null
                    && card.title().toLowerCase().contains(lowerQuery));
            node.setVisible(matches);
            node.setManaged(matches);
            if (matches) visible++;
        }
        if (taskCounter != null) {
            taskCounter.setText(showAll ? String.valueOf(cardsById.size()) : String.valueOf(visible));
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }
}