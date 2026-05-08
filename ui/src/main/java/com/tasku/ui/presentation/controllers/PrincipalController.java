package com.tasku.ui.presentation.controllers;

import com.tasku.ui.SceneManager;
import com.tasku.ui.client.dto.EstadoTablero;
import com.tasku.ui.client.dto.RolComparticion;
import com.tasku.ui.client.dto.request.ChangeBoardStatusApiRequest;
import com.tasku.ui.client.dto.request.CompleteCardApiRequest;
import com.tasku.ui.client.dto.request.MoveCardApiRequest;
import com.tasku.ui.client.dto.response.BoardApiResponse;
import com.tasku.ui.client.dto.response.BoardListApiResponse;
import com.tasku.ui.client.dto.response.CardApiResponse;
import com.tasku.ui.client.dto.response.CardLabelApiResponse;
import com.tasku.ui.client.dto.response.ChecklistItemApiResponse;
import com.tasku.ui.client.http.DesktopApiException;
import com.tasku.ui.client.http.TaskuApiClient;
import com.tasku.ui.client.dto.TipoTarjeta;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



public class PrincipalController {

    @FXML
    private Button ButtonTableroBlock;

    @FXML
    private StackPane btnCompartirTablero;

    @FXML
    private SVGPath SVG_NoCompartir;

    @FXML
    private SVGPath SVG_CpURL;

    @FXML
    private Button btnAddList;

    @FXML
    private SVGPath CandadoImage;

    @FXML
    private Label ContadorTareasCompletadas;

    @FXML
    private HBox boardContainer;

    @FXML
    private MenuButton boardMenuButton;

    @FXML
    private Button btnCreateTask;

    @FXML
    private VBox doneColumn;

    @FXML
    private TextField searchField;

    private final ToggleGroup boardToggleGroup = new ToggleGroup();
    private final List<RadioMenuItem> boardMenuItems = new ArrayList<>();
    private List<BoardApiResponse> boards = List.of();
    private List<BoardApiResponse> sharedBoards = List.of();
    private List<BoardApiResponse> allBoards = new ArrayList<>();
    private List<BoardListApiResponse> currentBoardLists = List.of();
    private final Map<UUID, ListaTareasController> listControllers = new LinkedHashMap<>();
    private final Map<UUID, VBox> columnNodes = new LinkedHashMap<>();
    private SeparatorMenuItem boardMenuSeparator;
    private MenuItem addBoardMenuItem;
    private MenuItem joinBoardMenuItem;
    private final Map<String, RadioMenuItem> boardMenuByUrl = new LinkedHashMap<>();

    private boolean estaBloqueado = false;
    private BoardApiResponse currentBoard;
    private final TaskuApiClient apiClient = new TaskuApiClient();
    private final Map<UUID, CardApiResponse> completedCards = new LinkedHashMap<>();
    private javafx.scene.Node doneEmptyState;

    @FXML
    private void initialize() {
        if (doneColumn != null && !doneColumn.getChildren().isEmpty()) {
            doneEmptyState = doneColumn.getChildren().get(0);
        }
        refreshBoards();
        if (SceneManager.getInstance().consumeNewUser()) {
            Platform.runLater(this::handleAñadirTablero);
        }

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> applySearchFilter(newVal));
        }
    }

    private void applySearchFilter(String query) {
        boolean showAll = query == null || query.isBlank();
        String lowerQuery = showAll ? "" : query.trim().toLowerCase();

        for (ListaTareasController ctrl : listControllers.values()) {
            ctrl.filterCards(query);
        }

        int visibleDone = 0;
        for (javafx.scene.Node node : doneColumn.getChildren()) {
            if (node == doneEmptyState) continue;
            UUID cardId = node.getUserData() instanceof UUID u ? u : null;
            if (cardId == null) continue;
            CardApiResponse card = completedCards.get(cardId);
            boolean matches = showAll || (card != null && card.title() != null
                    && card.title().toLowerCase().contains(lowerQuery));
            node.setVisible(matches);
            node.setManaged(matches);
            if (matches) visibleDone++;
        }
        if (ContadorTareasCompletadas != null) {
            ContadorTareasCompletadas.setText(
                showAll ? String.valueOf(completedCards.size()) : String.valueOf(visibleDone));
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
            String actorEmail = SceneManager.getInstance().getCurrentUserEmail();
            apiClient.changeBoardStatus(new ChangeBoardStatusApiRequest(boardUrl, nuevoEstado, actorEmail));
        } catch (DesktopApiException ex) {
            estaBloqueado = !estaBloqueado;
            showAlert("Error al cambiar estado del tablero: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
        actualizarIconoBloqueo();
        RolComparticion rolActual = SceneManager.getInstance().getCurrentUserRole();
        if (rolActual != null) applyRoleRestrictions(rolActual);
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
            showAlert("No se pudieron cargar tus tableros: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
        try {
            sharedBoards = apiClient.getSharedBoards(email);
        } catch (DesktopApiException ex) {
            sharedBoards = List.of();
            showAlert("No se pudieron cargar los tableros compartidos: " + ex.getMessage(), Alert.AlertType.ERROR);
        }

        allBoards.clear();
        allBoards.addAll(boards);
        allBoards.addAll(sharedBoards);

        buildBoardMenu(allBoards);
        seleccionarTableroDesdeContexto();
    }

    private void seleccionarTableroDesdeContexto() {
        if (allBoards.isEmpty()) {
            boardMenuButton.setText("Sin tableros");
            currentBoard = null;
            currentBoardLists = List.of();
            clearDynamicColumns();
            estaBloqueado = false;
            actualizarIconoBloqueo();
            return;
        }

        String currentBoardUrl = SceneManager.getInstance().getCurrentBoardUrl();
        for (int i = 0; i < allBoards.size(); i++) {
            BoardApiResponse board = allBoards.get(i);
            if (board.url() != null && board.url().equals(currentBoardUrl)) {
                RadioMenuItem item = boardMenuItems.get(i);
                item.setSelected(true);
                seleccionarTablero(board, item);
                return;
            }
        }

        RadioMenuItem item = boardMenuItems.get(0);
        item.setSelected(true);
        seleccionarTablero(allBoards.get(0), item);
    }

    private void seleccionarTablero(BoardApiResponse board, RadioMenuItem itemSeleccionado) {
        if (itemSeleccionado != null) {
            itemSeleccionado.setSelected(true);
        }

        if (searchField != null) {
            searchField.clear();
        }

        currentBoard = board;
        currentBoardLists = board.lists() == null ? List.of() : board.lists();
        boardMenuButton.setText(board.name());
        SceneManager.getInstance().setCurrentBoard(board.url(), board.name());

        UUID selectedListId = resolveCurrentListId(currentBoardLists);
        SceneManager.getInstance().setCurrentListId(selectedListId);

        estaBloqueado = board.status() == EstadoTablero.BLOCKED;
        actualizarIconoBloqueo();
        determineAndApplyRole(board);
        clearDoneColumn();
        renderBoardLists(currentBoardLists);
        loadCompletedCards(board.url());
    }

    private void agregarColumna(BoardListApiResponse list) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ListaTareas.fxml"));
            VBox nuevaColumna = loader.load();
            ListaTareasController controller = loader.getController();
            controller.setTitulo(list.name());
            controller.setListId(list.id());
            controller.setHeaderColor(list.colorHex());
            controller.setOnCardDropped(this::handleCardDropped);
            controller.setOnCardCompleted(this::handleCardCompleted);
            controller.setOnCreateCard(this::handleCrearTarea);
            controller.setOnColumnReorder(this::handleColumnReorder);
            controller.setOnListDeleted(this::handleListDeleted);
            controller.setOnListRenamed(this::handleListRenamed);
            RolComparticion rol = SceneManager.getInstance().getCurrentUserRole();
            boolean canEdit = rol != null && rol != RolComparticion.VIEWER;
            controller.setEditingEnabled(canEdit);
            controller.setCardCreationEnabled(canEdit && !estaBloqueado);
            controller.setRenamingEnabled(canEdit && !estaBloqueado);
            controller.setDeletionEnabled(canEdit && !estaBloqueado);
            listControllers.put(list.id(), controller);
            columnNodes.put(list.id(), nuevaColumna);
            int indice = boardContainer.getChildren().size() - 1;
            boardContainer.getChildren().add(indice, nuevaColumna);
        } catch (Exception e) {
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
        if (total <= 1) {
            listControllers.clear();
            columnNodes.clear();
            return;
        }
        boardContainer.getChildren().remove(0, total - 1);
        listControllers.clear();
        columnNodes.clear();
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
        SceneManager.getInstance().openDialogAndGetController(
            "UnirTablero", 
            (UnirTableroController controller) -> {
                controller.setOnJoinedAvisar((String nuevaUrl) -> {
                    SceneManager.getInstance().setCurrentBoard(nuevaUrl, "");
                    refreshBoards();
                });
            }
        );
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

        SceneManager.getInstance().openDialogAndGetController(
                "AñadirLista",
                (AñadirListaController controller) -> {
                    controller.setBoardUrl(boardUrl);
                    controller.setOnListCreated(this::handleListCreated);
                }
        );
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
    @FXML
void handleCompartirTablero(MouseEvent event) {
    String boardUrl = SceneManager.getInstance().getCurrentBoardUrl();
    if (boardUrl == null || boardUrl.isBlank()) {
        showAlert("Selecciona un tablero antes de compartirlo.", Alert.AlertType.WARNING);
        return;
    }
    
    SceneManager.getInstance().openDialog("CompartirTablero");
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
            List<CardApiResponse> active = cards == null ? List.of()
                    : cards.stream().filter(c -> !c.archived()).toList();
            controller.updateCards(active);
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

    private void handleColumnReorder(UUID draggedListId, UUID targetListId) {
        VBox draggedNode = columnNodes.get(draggedListId);
        VBox targetNode  = columnNodes.get(targetListId);
        if (draggedNode == null || targetNode == null) return;

        int draggedIndex = boardContainer.getChildren().indexOf(draggedNode);
        int targetIndex  = boardContainer.getChildren().indexOf(targetNode);
        if (draggedIndex < 0 || targetIndex < 0 || draggedIndex == targetIndex) return;

        boardContainer.getChildren().remove(draggedIndex);
        int insertAt = boardContainer.getChildren().indexOf(targetNode);
        boardContainer.getChildren().add(insertAt, draggedNode);
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

        for (BoardApiResponse board : allBoards) {
            if (boardUrl.endsWith(board.url())) {
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

    private void handleListDeleted(UUID listId) {
        if (listId == null) return;
        String boardUrl = SceneManager.getInstance().getCurrentBoardUrl();
        if (boardUrl == null) return;

        try {
            apiClient.deleteList(listId, boardUrl);
            VBox columnNode = columnNodes.remove(listId);
            if (columnNode != null) {
                boardContainer.getChildren().remove(columnNode);
            }
            listControllers.remove(listId);
            currentBoardLists = currentBoardLists.stream()
                    .filter(l -> !l.id().equals(listId))
                    .toList();
        } catch (DesktopApiException ex) {
            showAlert("No se pudo eliminar la lista: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleListRenamed(UUID listId, String newName) {
        if (listId == null || newName == null) return;
        String boardUrl = SceneManager.getInstance().getCurrentBoardUrl();
        if (boardUrl == null) return;

        try {
            BoardApiResponse updatedBoard = apiClient.renameList(listId, boardUrl, newName);
            ListaTareasController ctrl = listControllers.get(listId);
            if (ctrl != null) {
                ctrl.setTitulo(newName);
            }
            mergeBoard(updatedBoard);
            currentBoard = updatedBoard;
            currentBoardLists = updatedBoard.lists() != null ? updatedBoard.lists() : List.of();
        } catch (DesktopApiException ex) {
            showAlert("No se pudo renombrar la lista: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleCardCompleted(UUID cardId, UUID sourceListId) {
        if (cardId == null || sourceListId == null) {
            return;
        }
        String authorEmail = SceneManager.getInstance().getCurrentUserEmail();
        if (authorEmail == null || authorEmail.isBlank()) {
            showAlert("Debes iniciar sesión para completar tarjetas.", Alert.AlertType.WARNING);
            return;
        }
        ListaTareasController source = listControllers.get(sourceListId);
        try {
            CardApiResponse completed = apiClient.completeCard(new CompleteCardApiRequest(cardId, authorEmail));
            if (source != null) {
                source.removeCard(cardId);
            }
            addCardToDoneColumn(completed);
        } catch (DesktopApiException ex) {
            showAlert("No se pudo completar la tarjeta: " + ex.getMessage(), Alert.AlertType.ERROR);
            if (source != null) {
                refreshCardsForList(sourceListId);
            }
        }
    }

    private void loadCompletedCards(String boardUrl) {
        if (boardUrl == null || boardUrl.isBlank()) {
            return;
        }
        try {
            List<CardApiResponse> cards = apiClient.getCompletedCards(boardUrl);
            populateDoneColumn(cards);
        } catch (DesktopApiException ex) {
            showAlert("Error al cargar tarjetas completadas: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void populateDoneColumn(List<CardApiResponse> cards) {
        clearDoneColumn();
        if (cards == null) {
            return;
        }
        for (CardApiResponse card : cards) {
            if (card != null && card.id() != null) {
                completedCards.put(card.id(), card);
            }
        }
        if (completedCards.isEmpty()) {
            showDoneEmptyState();
        } else {
            hideDoneEmptyState();
            for (CardApiResponse card : completedCards.values()) {
                doneColumn.getChildren().add(buildCompletedCardNode(card));
            }
        }
        updateDoneCounter();
    }

    private void addCardToDoneColumn(CardApiResponse card) {
        if (card == null || card.id() == null || doneColumn == null) {
            return;
        }
        completedCards.put(card.id(), card);
        hideDoneEmptyState();
        doneColumn.getChildren().add(buildCompletedCardNode(card));
        updateDoneCounter();
    }

    private void clearDoneColumn() {
        completedCards.clear();
        if (doneColumn != null) {
            doneColumn.getChildren().clear();
        }
        showDoneEmptyState();
        updateDoneCounter();
    }

    private void showDoneEmptyState() {
        if (doneEmptyState != null && doneColumn != null
                && !doneColumn.getChildren().contains(doneEmptyState)) {
            doneColumn.getChildren().add(0, doneEmptyState);
        }
    }

    private void hideDoneEmptyState() {
        if (doneEmptyState != null && doneColumn != null) {
            doneColumn.getChildren().remove(doneEmptyState);
        }
    }

    private void updateDoneCounter() {
        if (ContadorTareasCompletadas != null) {
            ContadorTareasCompletadas.setText(String.valueOf(completedCards.size()));
        }
    }

    private javafx.scene.Node buildCompletedCardNode(CardApiResponse card) {
        VBox cardBox = new VBox();
        cardBox.getStyleClass().addAll("task-card", "task-card-done");
        cardBox.setUserData(card.id());

        Label title = new Label(safe(card.title()));
        title.getStyleClass().add("task-card-title");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(javafx.geometry.Pos.CENTER);
        title.setWrapText(true);
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-strikethrough: true;");
        cardBox.getChildren().add(title);

        if (card.labels() != null && !card.labels().isEmpty()) {
            javafx.scene.layout.HBox labelsBox = new javafx.scene.layout.HBox();
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

        if (card.type() == TipoTarjeta.CHECKLIST
                && card.checklistItems() != null
                && !card.checklistItems().isEmpty()) {
            long done = card.checklistItems().stream()
                    .filter(ChecklistItemApiResponse::completed).count();
            int total = card.checklistItems().size();
            Label progress = new Label(done + " / " + total);
            progress.getStyleClass().add("checklist-progress");
            cardBox.getChildren().add(progress);
        }

        String desc = safe(card.description());
        if (!desc.isBlank()) {
            Label description = new Label(desc);
            description.setWrapText(true);
            description.getStyleClass().add("task-card-description");
            cardBox.getChildren().add(description);
        }

        return cardBox;
    }

    private void determineAndApplyRole(BoardApiResponse board) {
        String userEmail = SceneManager.getInstance().getCurrentUserEmail();
        RolComparticion role = RolComparticion.VIEWER;
        if (userEmail != null && board.ownerEmail() != null
                && board.ownerEmail().equalsIgnoreCase(userEmail)) {
            role = RolComparticion.ADMIN;
        } else if (board.sharedWith() != null) {
            for (com.tasku.ui.client.dto.response.BoardShareApiResponse share : board.sharedWith()) {
                if (share.email() != null && share.email().equalsIgnoreCase(userEmail)) {
                    role = share.role();
                    break;
                }
            }
        }
        SceneManager.getInstance().setCurrentUserRole(role);
        applyRoleRestrictions(role);
    }

    private void applyRoleRestrictions(RolComparticion role) {
        boolean isAdmin = role == RolComparticion.ADMIN;
        boolean canEdit = role != RolComparticion.VIEWER;
        boolean canCreate = canEdit && !estaBloqueado;
        boolean canRename = canEdit && !estaBloqueado;
        boolean canDelete = canEdit && !estaBloqueado;


        if (ButtonTableroBlock != null) ButtonTableroBlock.setDisable(!isAdmin);
        if (btnCompartirTablero != null){
            
            btnCompartirTablero.setDisable(!isAdmin);
            SVG_CpURL.setVisible(isAdmin);
            SVG_NoCompartir.setVisible(!isAdmin);
        }
        
        
        if (btnAddList != null) btnAddList.setDisable(!canCreate);
        if (btnCreateTask != null) btnCreateTask.setDisable(!canCreate);
        for (ListaTareasController ctrl : listControllers.values()) {
            ctrl.setEditingEnabled(canEdit);
            ctrl.setCardCreationEnabled(canCreate);
            ctrl.setRenamingEnabled(canRename);
            ctrl.setDeletionEnabled(canDelete);
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("TaskU");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



}
