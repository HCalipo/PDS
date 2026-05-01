package com.tasku.ui.presentation.controllers;

import com.tasku.ui.SceneManager;
import com.tasku.ui.client.dto.EstadoTablero;
import com.tasku.ui.client.dto.request.ChangeBoardStatusApiRequest;
import com.tasku.ui.client.dto.request.CreateListApiRequest;
import com.tasku.ui.client.dto.request.MoveCardApiRequest;
import com.tasku.ui.client.dto.response.BoardApiResponse;
import com.tasku.ui.client.dto.response.BoardListApiResponse;
import com.tasku.ui.client.dto.response.CardApiResponse;
import com.tasku.ui.client.http.DesktopApiException;
import com.tasku.ui.client.http.TaskuApiClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PrincipalController {

    @FXML private Button ButtonTableroBlock;
    @FXML private SVGPath CandadoImage;
    @FXML private MenuButton boardMenuButton;
    @FXML private HBox boardContainer;

    private final TaskuApiClient apiClient = new TaskuApiClient();
    private boolean estaBloqueado = false;
    private final ToggleGroup boardToggleGroup = new ToggleGroup();
    private final List<RadioMenuItem> boardMenuItems = new ArrayList<>();
    private List<BoardApiResponse> boards = List.of();
    private BoardApiResponse currentBoard;
    private List<BoardListApiResponse> currentBoardLists = List.of();
    private final Map<UUID, ListaTareasController> listControllers = new LinkedHashMap<>();
    private SeparatorMenuItem boardMenuSeparator;
    private MenuItem addBoardMenuItem;
    private MenuItem joinBoardMenuItem;
    private final Map<String, RadioMenuItem> boardMenuByUrl = new LinkedHashMap<>();

    @FXML
    private void initialize() {
        refreshBoards();
        if (SceneManager.getInstance().consumeNewUser()) {
            Platform.runLater(this::handleAñadirTablero);
        }
    }

    @FXML
    private void handleBloquearTablero() {
        String boardUrl = SceneManager.getInstance().getCurrentBoardUrl();
        if (boardUrl == null) {
            showAlert("Selecciona un tablero antes de cambiar su estado.", Alert.AlertType.WARNING);
            return;
        }

        estaBloqueado = !estaBloqueado;
        EstadoTablero nuevoEstado = estaBloqueado ? EstadoTablero.BLOCKED : EstadoTablero.ACTIVE;
        try {
            apiClient.changeBoardStatus(new ChangeBoardStatusApiRequest(boardUrl, nuevoEstado));
            System.out.println("Tablero " + (estaBloqueado ? "bloqueado" : "desbloqueado") + ".");
        } catch (DesktopApiException ex) {
            estaBloqueado = !estaBloqueado;
            showAlert("Error al cambiar estado del tablero: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
        actualizarIconoBloqueo();
    }

    private void actualizarIconoBloqueo() {
        if (estaBloqueado) {
            CandadoImage.setContent("M405.333,179.712v-30.379C405.333,66.859,338.475,0,256,0S106.667,66.859,106.667,149.333v30.379   c-38.826,16.945-63.944,55.259-64,97.621v128C42.737,464.214,90.452,511.93,149.333,512h213.333   c58.881-0.07,106.596-47.786,106.667-106.667v-128C469.278,234.971,444.159,196.657,405.333,179.712z M277.333,362.667   c0,11.782-9.551,21.333-21.333,21.333c-11.782,0-21.333-9.551-21.333-21.333V320c0-11.782,9.551-21.333,21.333-21.333   c11.782,0,21.333,9.551,21.333,21.333V362.667z M362.667,170.667H149.333v-21.333c0-58.91,47.756-106.667,106.667-106.667   s106.667,47.756,106.667,106.667V170.667z");
            ButtonTableroBlock.setStyle("-fx-background-color: #0ba360; -fx-text-fill: white;");
            CandadoImage.setScaleX(0.03);
            CandadoImage.setScaleY(0.03);
        } else {
            CandadoImage.setContent("M264-168h432v-384H264v384Zm267-141.21q21-21.21 21-51T530.79-411q-21.21-21-51-21T429-410.79q-21 21.21-21 51T429.21-309q21.21 21 51 21T531-309.21ZM264-168v-384 384Zm-.28 72Q234-96 213-117.15T192-168v-384q0-29.7 21.15-50.85Q234.3-624 264-624h264v-96q0-79.68 56.23-135.84 56.22-56.16 136-56.16Q800-912 856-855.84q56 56.16 56 135.84h-72q0-50-35-85t-85-35q-50 0-85 35t-35 85v96h96q29.7 0 50.85 21.15Q768-581.7 768-552v384q0 29.7-21.16 50.85Q725.68-96 695.96-96H263.72Z");
            ButtonTableroBlock.setStyle("");
            CandadoImage.setScaleX(0.02);
            CandadoImage.setScaleY(0.02);
        }
    }

    private void buildBoardMenu(List<BoardApiResponse> boards) {
        boardMenuButton.getItems().clear();
        boardMenuItems.clear();
        boardMenuByUrl.clear();

        MenuItem labelSeccion = new MenuItem("TUS TABLEROS");
        labelSeccion.setDisable(true);
        labelSeccion.getStyleClass().add("menu-section-label");
        boardMenuButton.getItems().add(labelSeccion);

        ensureBoardMenuActions();

        for (BoardApiResponse board : boards) {
            addOrUpdateBoardMenuItem(board);
        }

        if (!boardMenuButton.getItems().contains(boardMenuSeparator)) {
            boardMenuButton.getItems().add(boardMenuSeparator);
        }

        if (!boardMenuButton.getItems().contains(addBoardMenuItem)) {
            boardMenuButton.getItems().add(addBoardMenuItem);
        }

        if (!boardMenuButton.getItems().contains(joinBoardMenuItem)) {
            boardMenuButton.getItems().add(joinBoardMenuItem);
        }
    }

    private void ensureBoardMenuActions() {
        if (boardMenuSeparator == null) {
            boardMenuSeparator = new SeparatorMenuItem();
        }
        if (addBoardMenuItem == null) {
            addBoardMenuItem = new MenuItem("＋ Añadir tablero");
            addBoardMenuItem.setOnAction(e -> handleAñadirTablero());
            addBoardMenuItem.getStyleClass().add("menu-action-item");
        }
        if (joinBoardMenuItem == null) {
            joinBoardMenuItem = new MenuItem("👥 Unirse a tablero");
            joinBoardMenuItem.setOnAction(e -> handleUnirseTablero());
            joinBoardMenuItem.getStyleClass().add("menu-action-item");
        }

    }

    private void refreshBoards() {
        String email = SceneManager.getInstance().getCurrentUserEmail();
        if (email == null || email.isBlank()) {
            buildBoardMenu(List.of());
            boardMenuButton.setText("Sin usuario");
            clearDynamicColumns();
            estaBloqueado = false;
            actualizarIconoBloqueo();
            return;
        }

        try {
            boards = apiClient.findBoardsByOwner(email);
        } catch (DesktopApiException ex) {
            boards = List.of();
            showAlert("No se pudieron cargar los tableros: " + ex.getMessage(), Alert.AlertType.ERROR);
        }

        buildBoardMenu(boards);
        seleccionarTableroDesdeContexto();
    }

    private void seleccionarTableroDesdeContexto() {
        if (boards.isEmpty()) {
            boardMenuButton.setText("Sin tableros");
            currentBoard = null;
            currentBoardLists = List.of();
            clearDynamicColumns();
            estaBloqueado = false;
            actualizarIconoBloqueo();
            return;
        }

        String currentBoardUrl = SceneManager.getInstance().getCurrentBoardUrl();
        for (int i = 0; i < boards.size(); i++) {
            BoardApiResponse board = boards.get(i);
            if (board.url() != null && board.url().equals(currentBoardUrl)) {
                RadioMenuItem item = boardMenuItems.get(i);
                item.setSelected(true);
                seleccionarTablero(board, item);
                return;
            }
        }

        RadioMenuItem item = boardMenuItems.get(0);
        item.setSelected(true);
        seleccionarTablero(boards.get(0), item);
    }

    private void seleccionarTablero(BoardApiResponse board, RadioMenuItem itemSeleccionado) {
        if (itemSeleccionado != null) {
            itemSeleccionado.setSelected(true);
        }

        currentBoard = board;
        currentBoardLists = board.lists() == null ? List.of() : board.lists();
        boardMenuButton.setText(board.name());
        SceneManager.getInstance().setCurrentBoard(board.url(), board.name());

        UUID selectedListId = resolveCurrentListId(currentBoardLists);
        SceneManager.getInstance().setCurrentListId(selectedListId);

        estaBloqueado = board.status() == EstadoTablero.BLOCKED;
        actualizarIconoBloqueo();
        renderBoardLists(currentBoardLists);
    }

    private void agregarColumna(BoardListApiResponse list) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ListaTareas.fxml"));
            VBox nuevaColumna = loader.load();
            ListaTareasController controller = loader.getController();
            controller.setTitulo(list.name());
            controller.setListId(list.id());
            controller.setOnCardDropped(this::handleCardDropped);
            controller.setOnCreateCard(this::handleCrearTarea);
            listControllers.put(list.id(), controller);
            int indice = boardContainer.getChildren().size() - 1;
            boardContainer.getChildren().add(indice, nuevaColumna);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderBoardLists(List<BoardListApiResponse> lists) {
        clearDynamicColumns();
        if (lists == null || lists.isEmpty()) {
            return;
        }
        for (BoardListApiResponse list : lists) {
            agregarColumna(list);
        }
        refreshCardsForBoard();
    }

    private void clearDynamicColumns() {
        int total = boardContainer.getChildren().size();
            if (total <= 2) {
                // preserve static first and last children (e.g., doneColumn and addButton)
                listControllers.clear();
                return;
            }
            // remove children between first (index 0) and last (index total-1)
            boardContainer.getChildren().remove(1, total - 1);
            listControllers.clear();
    }

    private UUID resolveCurrentListId(List<BoardListApiResponse> lists) {
        UUID current = SceneManager.getInstance().getCurrentListId();
        if (current != null) {
            for (BoardListApiResponse list : lists) {
                if (current.equals(list.id())) {
                    return current;
                }
            }
        }
        return lists.isEmpty() ? null : lists.get(0).id();
    }

    @FXML
    private void handleAñadirTablero() {
        SceneManager.getInstance().openDialogAndGetController(
                "AñadirTablero",
                (AñadirTableroController controller) -> controller.setOnBoardCreated(this::handleBoardCreated)
        );
    }

    @FXML
    private void handleUnirseTablero() {
        SceneManager.getInstance().openDialog("UnirTablero");
    }

    @FXML
    private void handleVerHistorial() {
        SceneManager.getInstance().openDialog("Historial");
    }

    @FXML
    private void handleAñadirLista() {
        String boardUrl = SceneManager.getInstance().getCurrentBoardUrl();
        if (boardUrl == null) {
            showAlert("Selecciona un tablero antes de crear una lista.", Alert.AlertType.WARNING);
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nueva lista");
        dialog.setHeaderText(null);
        dialog.setContentText("Nombre de la lista:");

        Optional<String> resultado = dialog.showAndWait();
        resultado.filter(nombre -> !nombre.isBlank()).ifPresent(nombre -> {
            try {
                BoardApiResponse updatedBoard = apiClient.createList(new CreateListApiRequest(boardUrl, nombre, 100));
                handleListCreated(updatedBoard);
            } catch (DesktopApiException ex) {
                showAlert("Error al crear lista: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void handleCrearTarea() {
        if (currentBoard == null) {
            showAlert("Selecciona un tablero para crear una tarjeta.", Alert.AlertType.WARNING);
            return;
        }
        if (currentBoardLists.isEmpty()) {
            showAlert("El tablero no tiene listas. Crea una lista primero.", Alert.AlertType.WARNING);
            return;
        }
        SceneManager.getInstance().openDialogAndGetController(
                "CreateCard",
            (CrearTarjetaController controller) -> {
                controller.loadAvailableLists(
                    currentBoardLists,
                    SceneManager.getInstance().getCurrentListId()
                );
                controller.setOnCardCreated(this::refreshCardsForList);
            }
        );
    }

    private void refreshCardsForBoard() {
        if (currentBoardLists == null || currentBoardLists.isEmpty()) {
            return;
        }
        boolean errorShown = false;
        for (BoardListApiResponse list : currentBoardLists) {
            try {
                List<CardApiResponse> cards = apiClient.findCardsByList(list.id());
                updateColumnCards(list.id(), cards);
            } catch (DesktopApiException ex) {
                updateColumnCards(list.id(), List.of());
                if (!errorShown) {
                    showAlert("Error al cargar tarjetas: " + ex.getMessage(), Alert.AlertType.ERROR);
                    errorShown = true;
                }
            }
        }
    }

    private void refreshCardsForList(UUID listId) {
        if (listId == null) {
            return;
        }
        try {
            List<CardApiResponse> cards = apiClient.findCardsByList(listId);
            updateColumnCards(listId, cards);
        } catch (DesktopApiException ex) {
            updateColumnCards(listId, List.of());
            showAlert("Error al cargar tarjetas: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateColumnCards(UUID listId, List<CardApiResponse> cards) {
        ListaTareasController controller = listControllers.get(listId);
        if (controller != null) {
            controller.updateCards(cards);
        }
    }

    private void handleBoardCreated(BoardApiResponse board) {
        if (board == null) {
            return;
        }
        mergeBoard(board);
        addOrUpdateBoardMenuItem(board);
        seleccionarTablero(board, boardMenuByUrl.get(board.url()));
    }

    private void handleListCreated(BoardApiResponse updatedBoard) {
        if (updatedBoard == null) {
            return;
        }
        mergeBoard(updatedBoard);
        if (updatedBoard.lists() == null) {
            return;
        }

        List<BoardListApiResponse> updatedLists = updatedBoard.lists();
        List<BoardListApiResponse> newLists = findNewLists(currentBoardLists, updatedLists);
        currentBoard = updatedBoard;
        currentBoardLists = updatedLists;

        if (newLists.isEmpty()) {
            return;
        }

        for (BoardListApiResponse list : newLists) {
            agregarColumna(list);
            updateColumnCards(list.id(), List.of());
        }
        SceneManager.getInstance().setCurrentListId(newLists.get(newLists.size() - 1).id());
    }

    private void mergeBoard(BoardApiResponse updatedBoard) {
        if (updatedBoard == null) {
            return;
        }
        List<BoardApiResponse> updated = new ArrayList<>(boards);
        boolean replaced = false;
        for (int i = 0; i < updated.size(); i++) {
            if (updated.get(i).url().equals(updatedBoard.url())) {
                updated.set(i, updatedBoard);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            updated.add(updatedBoard);
        }
        boards = List.copyOf(updated);
    }

    private void addOrUpdateBoardMenuItem(BoardApiResponse board) {
        if (board == null || board.url() == null) {
            return;
        }

        ensureBoardMenuActions();

        RadioMenuItem existing = boardMenuByUrl.get(board.url());
        if (existing != null) {
            existing.setText(board.name());
            return;
        }

        String boardUrl = board.url();
        RadioMenuItem item = new RadioMenuItem(board.name());
        item.getStyleClass().add("menu-board-item");
        item.setToggleGroup(boardToggleGroup);
        item.setOnAction(e -> {
            BoardApiResponse current = findBoardByUrl(boardUrl);
            if (current != null) {
                seleccionarTablero(current, item);
            }
        });

        boardMenuItems.add(item);
        boardMenuByUrl.put(boardUrl, item);

        int insertIndex = boardMenuButton.getItems().indexOf(boardMenuSeparator);
        if (insertIndex < 0) {
            insertIndex = boardMenuButton.getItems().size();
        }
        boardMenuButton.getItems().add(insertIndex, item);
    }

    private void handleCardDropped(UUID cardId, UUID sourceListId, UUID targetListId) {
        if (cardId == null || sourceListId == null || targetListId == null) {
            return;
        }
        if (sourceListId.equals(targetListId)) {
            return;
        }
        String authorEmail = SceneManager.getInstance().getCurrentUserEmail();
        if (authorEmail == null || authorEmail.isBlank()) {
            showAlert("Debes iniciar sesión para mover tarjetas.", Alert.AlertType.WARNING);
            return;
        }
        ListaTareasController source = listControllers.get(sourceListId);
        ListaTareasController target = listControllers.get(targetListId);
        if (source == null || target == null) {
            return;
        }

        try {
            apiClient.moveCard(new MoveCardApiRequest(cardId, targetListId, authorEmail));
            CardApiResponse moved = source.removeCard(cardId);
            if (moved != null) {
                CardApiResponse updated = new CardApiResponse(
                        moved.id(),
                        targetListId,
                        moved.type(),
                        moved.title(),
                        moved.description(),
                        moved.archived(),
                        moved.labels(),
                        moved.checklistItems()
                );
                target.addCard(updated);
            } else {
                refreshCardsForList(sourceListId);
                refreshCardsForList(targetListId);
            }
            SceneManager.getInstance().setCurrentListId(targetListId);
        } catch (DesktopApiException ex) {
            showAlert("No se pudo mover la tarjeta: " + ex.getMessage(), Alert.AlertType.ERROR);
            refreshCardsForList(sourceListId);
            refreshCardsForList(targetListId);
        }
    }

    private BoardApiResponse findBoardByUrl(String boardUrl) {
        if (boardUrl == null) {
            return null;
        }
        for (BoardApiResponse board : boards) {
            if (boardUrl.equals(board.url())) {
                return board;
            }
        }
        return null;
    }

    private List<BoardListApiResponse> findNewLists(List<BoardListApiResponse> existing,
                                                   List<BoardListApiResponse> updated) {
        if (updated == null || updated.isEmpty()) {
            return List.of();
        }
        Map<UUID, BoardListApiResponse> existingById = new LinkedHashMap<>();
        if (existing != null) {
            for (BoardListApiResponse list : existing) {
                existingById.put(list.id(), list);
            }
        }
        List<BoardListApiResponse> newLists = new ArrayList<>();
        for (BoardListApiResponse list : updated) {
            if (!existingById.containsKey(list.id())) {
                newLists.add(list);
            }
        }
        return newLists;
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("TaskU");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
