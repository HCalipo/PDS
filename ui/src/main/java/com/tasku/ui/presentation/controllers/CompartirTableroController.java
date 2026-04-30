package com.tasku.ui.presentation.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.ResourceBundle;

public class CompartirTableroController implements Initializable {

    @FXML
    private TextField txtEnlace;

    @FXML
    private ToggleButton toggleLectura;

    @FXML
    private ToggleGroup permisosGroup;

    @FXML
    private Button btnCopiar;

    @FXML
    private Button btnGenerar;

    private String enlaceGenerado = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnCopiar.setDisable(true);
    }

    @FXML
    private void handleGenerarEnlace() {
        String permiso = obtenerPermisoSeleccionado();
        String codigoTablero = "codigo-del-tablero";
        enlaceGenerado = "https://tasku.app/b/" + codigoTablero + "?permiso=" + permiso;
        txtEnlace.setText(enlaceGenerado);
        btnCopiar.setDisable(false);
    }

    @FXML
    private void handleCopiar() {
        if (!enlaceGenerado.isEmpty()) {
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(enlaceGenerado);
            clipboard.setContent(content);
        }
    }

    @FXML
    private void handleCancelar() {
        txtEnlace.getScene().getWindow().hide();
    }

    private String obtenerPermisoSeleccionado() {
        ToggleButton selected = (ToggleButton) permisosGroup.getSelectedToggle();
        if (selected != null) {
            String texto = selected.getText();
            switch (texto) {
                case "Solo lectura":
                    return "lectura";
                case "Puede editar":
                    return "editar";
                case "Administrador":
                    return "admin";
                default:
                    return "lectura";
            }
        }
        return "lectura";
    }
}
