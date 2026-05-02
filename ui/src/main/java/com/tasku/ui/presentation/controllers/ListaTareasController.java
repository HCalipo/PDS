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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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

    private static final DataFormat CARD_ID_FORMAT   = new DataFormat("application/x-tasku-card-id");
    private static final DataFormat LIST_ID_FORMAT   = new DataFormat("application/x-tasku-list-id");
    private static final DataFormat COLUMN_ID_FORMAT = new DataFormat("application/x-tasku-column-list-id");

    @FXML private HBox columnHeader;
    @FXML private Label columnTitle;
    @FXML private Label taskCounter;
    @FXML private VBox tasksContainer;
    @FXML private VBox emptyState;

    private UUID listId;
    private final Map<UUID, CardApiResponse> cardsById = new LinkedHashMap<>();
    private CardDropHandler onCardDropped;
    private CardCompletedHandler onCardCompleted;
    private ColumnReorderHandler onColumnReorder;
    private Runnable onCreateCard;
    private String savedHeaderStyle = "";
    private boolean headerDropActive = false;
    private final TaskuApiClient apiClient = new TaskuApiClient();

    public void setTitulo(String nombre) {
        if (columnTitle != null) {
            columnTitle.setText(nombre != null ? nombre.toUpperCase() : "");
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

    @FXML
    private void handleCrearTarea() {
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
        VBox cardBox = new VBox();
        cardBox.getStyleClass().add("task-card");
        cardBox.setUserData(card.id());

        String accentColor = firstLabelColor(card);
        if (!accentColor.isBlank()) {
            cardBox.setStyle("-fx-border-color: " + accentColor + "; -fx-border-width: 2;");
        }

        // 1. Título
        Label title = new Label(safe(card.title()));
        title.getStyleClass().add("task-card-title");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(javafx.geometry.Pos.CENTER);
        title.setWrapText(true);
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        cardBox.getChildren().add(title);

        // 2. Nombre de etiqueta(s)
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
                cardBox.getChildren().add(labelsBox);
            }
        }

        // 3. Checklist items
        if (card.type() == TipoTarjeta.CHECKLIST
                && card.checklistItems() != null
                && !card.checklistItems().isEmpty()) {

            long done = card.checklistItems().stream()
                    .filter(ChecklistItemApiResponse::completed).count();
            int total = card.checklistItems().size();

            Label progress = new Label(done + " / " + total);
            progress.getStyleClass().add("checklist-progress");
            cardBox.getChildren().add(progress);

            ProgressBar progressBar = new ProgressBar(total > 0 ? (double) done / total : 0);
            progressBar.setMaxWidth(Double.MAX_VALUE);
            progressBar.getStyleClass().add("progress-bar-mini");
            cardBox.getChildren().add(progressBar);

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
            cardBox.getChildren().add(checklistBox);
        }

        // 4. Descripción
        String descriptionValue = safe(card.description());
        if (!descriptionValue.isBlank()) {
            Label description = new Label(descriptionValue);
            description.setWrapText(true);
            description.getStyleClass().add("task-card-description");
            cardBox.getChildren().add(description);
        }

        // 5. Botón completar
        javafx.scene.control.Button completeBtn = new javafx.scene.control.Button("✓ Completar");
        completeBtn.getStyleClass().add("btn-complete-card");
        completeBtn.setMaxWidth(Double.MAX_VALUE);
        completeBtn.setOnAction(e -> {
            if (listId != null && onCardCompleted != null) {
                completeBtn.setDisable(true);
                onCardCompleted.handle(card.id(), listId);
            }
        });
        cardBox.getChildren().add(completeBtn);

        return cardBox;
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

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
