package com.tasku.ui.presentation.controllers;

import com.tasku.ui.SceneManager;
import com.tasku.ui.client.dto.RolComparticion;
import com.tasku.ui.client.dto.request.ShareBoardApiRequest;
import com.tasku.ui.client.http.DesktopApiException;
import com.tasku.ui.client.http.TaskuApiClient;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.ResourceBundle;

public class CompartirTableroController implements Initializable {

    @FXML private TextField txtEmail;
    @FXML private TextField txtEnlace;
    @FXML private ToggleButton toggleLectura;
    @FXML private ToggleButton toggleEditar;
    @FXML private ToggleButton toggleAdministrador;
    @FXML private ToggleGroup permisosGroup;
    @FXML private Button btnCopiar;
    @FXML private Button btnCompartir;
    @FXML private Label lblEstado;

    private final TaskuApiClient apiClient = new TaskuApiClient();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String boardUrl = SceneManager.getInstance().getCurrentBoardUrl();
        if (boardUrl != null) {
            txtEnlace.setText(boardUrl);
            btnCopiar.setDisable(false);
        } else {
            btnCopiar.setDisable(true);
        }
        lblEstado.setText("");
    }

    @FXML
    private void handleCompartir() {
        String boardUrl = SceneManager.getInstance().getCurrentBoardUrl();
        String actorEmail = SceneManager.getInstance().getCurrentUserEmail();

        if (boardUrl == null || boardUrl.isBlank()) {
            setEstado("No hay tablero seleccionado.", true);
            return;
        }

        String recipientEmail = txtEmail.getText() != null ? txtEmail.getText().trim() : "";
        if (recipientEmail.isBlank()) {
            setEstado("Introduce el email del destinatario.", true);
            return;
        }

        RolComparticion role = resolveSelectedRole();

        try {
            btnCompartir.setDisable(true);
            apiClient.shareBoard(new ShareBoardApiRequest(boardUrl, recipientEmail, role, actorEmail));
            setEstado("Tablero compartido con " + recipientEmail + " como " + roleLabel(role) + ". "
                    + "Aparecerá en su lista de tableros compartidos.", false);
            txtEmail.clear();
        } catch (DesktopApiException ex) {
            setEstado("Error: " + ex.getMessage(), true);
        } finally {
            btnCompartir.setDisable(false);
        }
    }

    @FXML
    private void handleCopiar() {
        String enlace = txtEnlace.getText();
        if (enlace != null && !enlace.isBlank()) {
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(enlace);
            clipboard.setContent(content);
            setEstado("Enlace copiado al portapapeles.", false);
        }
    }

    @FXML
    private void handleCancelar() {
        txtEnlace.getScene().getWindow().hide();
    }

    private RolComparticion resolveSelectedRole() {
        ToggleButton selected = (ToggleButton) permisosGroup.getSelectedToggle();
        if (selected == null) return RolComparticion.VIEWER;
        return switch (selected.getText()) {
            case "Puede editar" -> RolComparticion.EDITOR;
            case "Administrador" -> RolComparticion.ADMIN;
            default -> RolComparticion.VIEWER;
        };
    }

    private String roleLabel(RolComparticion role) {
        return switch (role) {
            case EDITOR -> "editor";
            case ADMIN -> "administrador";
            default -> "solo lectura";
        };
    }

    private void setEstado(String mensaje, boolean esError) {
        if (lblEstado != null) {
            lblEstado.setText(mensaje);
            lblEstado.setStyle(esError
                    ? "-fx-text-fill: #d63031;"
                    : "-fx-text-fill: #0ba360;");
        }
    }
}
