package com.tasku.ui.presentation.controllers;

import com.tasku.ui.client.dto.response.CardApiResponse;
import com.tasku.ui.client.dto.response.CardLabelApiResponse;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
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

    private static final DataFormat CARD_ID_FORMAT = new DataFormat("application/x-tasku-card-id");
    private static final DataFormat LIST_ID_FORMAT = new DataFormat("application/x-tasku-list-id");

    @FXML private Label columnTitle;
    @FXML private Label taskCounter;
    @FXML private VBox tasksContainer;
    @FXML private VBox emptyState;

    private UUID listId;
    private final Map<UUID, CardApiResponse> cardsById = new LinkedHashMap<>();
    private CardDropHandler onCardDropped;

    public void setTitulo(String nombre) {
        if (columnTitle != null) {
            columnTitle.setText(nombre);
        }
    }

    public void setListId(UUID listId) {
        this.listId = listId;
        setupDropTargets();
    }

    public UUID getListId() {
        return listId;
    }

    public void setOnCardDropped(CardDropHandler onCardDropped) {
        this.onCardDropped = onCardDropped;
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

        Label title = new Label(safe(card.title()));
        title.getStyleClass().add("task-card-title");
        cardBox.getChildren().add(title);

        String descriptionValue = safe(card.description());
        if (!descriptionValue.isBlank()) {
            Label description = new Label(descriptionValue);
            description.setWrapText(true);
            description.getStyleClass().add("task-card-description");
            cardBox.getChildren().add(description);
        }

        if (card.labels() != null && !card.labels().isEmpty()) {
            HBox labelsBox = new HBox();
            labelsBox.getStyleClass().add("task-card-labels");
            for (CardLabelApiResponse label : card.labels()) {
                String labelName = safe(label.name());
                if (labelName.isBlank()) {
                    continue;
                }
                Label chip = new Label(labelName);
                chip.getStyleClass().add("task-card-label");
                String color = safe(label.colorHex());
                if (!color.isBlank()) {
                    chip.setStyle("-fx-background-color: " + color + ";");
                }
                labelsBox.getChildren().add(chip);
            }
            if (!labelsBox.getChildren().isEmpty()) {
                cardBox.getChildren().add(labelsBox);
            }
        }

        return cardBox;
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
