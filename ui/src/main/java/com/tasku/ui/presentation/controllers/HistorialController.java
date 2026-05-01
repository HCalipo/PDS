package com.tasku.ui.presentation.controllers;

import com.tasku.ui.SceneManager;
import com.tasku.ui.client.dto.response.BoardApiResponse;
import com.tasku.ui.client.dto.response.BoardListApiResponse;
import com.tasku.ui.client.dto.response.TraceApiResponse;
import com.tasku.ui.client.http.DesktopApiException;
import com.tasku.ui.client.http.TaskuApiClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistorialController {

    @FXML
    private ListView<TraceApiResponse> listHistorial;

    private final TaskuApiClient apiClient = new TaskuApiClient();

    @FXML
    private void initialize() {
        String boardUrl = SceneManager.getInstance().getCurrentBoardUrl();
        if (boardUrl == null || boardUrl.isBlank()) {
            showAlert("No hay tablero seleccionado.", Alert.AlertType.WARNING);
            return;
        }

        Map<String, String> listNames = resolveListNames(boardUrl);
        final Map<String, String> resolvedNames = Collections.unmodifiableMap(listNames);

        listHistorial.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(TraceApiResponse trace, boolean empty) {
                super.updateItem(trace, empty);
                if (empty || trace == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Historial_card.fxml"));
                    HBox card = loader.load();
                    Historial_cardController controller = loader.getController();
                    controller.setTrace(trace, resolvedNames);
                    setGraphic(card);
                    setText(null);
                } catch (IOException e) {
                    setGraphic(null);
                    setText(trace.description());
                }
            }
        });

        try {
            List<TraceApiResponse> traces = new ArrayList<>(apiClient.getTraces(boardUrl));
            Collections.reverse(traces);
            listHistorial.getItems().setAll(traces);
        } catch (DesktopApiException ex) {
            showAlert("No se pudo cargar el historial: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Map<String, String> resolveListNames(String boardUrl) {
        Map<String, String> names = new HashMap<>();
        try {
            BoardApiResponse board = apiClient.getBoardByUrl(boardUrl);
            if (board.lists() != null) {
                for (BoardListApiResponse list : board.lists()) {
                    if (list.id() != null && list.name() != null) {
                        names.put(list.id().toString(), list.name());
                    }
                }
            }
        } catch (DesktopApiException ignored) {
        }
        return names;
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("TaskU");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
