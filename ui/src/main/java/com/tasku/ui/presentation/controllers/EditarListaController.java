package com.tasku.ui.presentation.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditarListaController {

    @FXML
    private TextField txtTitulo;

    @FXML
    private javafx.scene.control.Button btnCerrar;

    private boolean saved = false;
    private String newName;

    public void setListName(String name) {
        txtTitulo.setText(name);
        txtTitulo.requestFocus();
        txtTitulo.selectAll();
    }

    @FXML
    private void handleGuardar() {
        String name = txtTitulo.getText().trim();
        if (name.isBlank()) return;
        this.newName = name;
        this.saved = true;
        close();
    }

    @FXML
    private void handleCerrar() {
        close();
    }

    public boolean wasSaved() {
        return saved;
    }

    public String getNewName() {
        return newName;
    }

    private void close() {
        ((Stage) txtTitulo.getScene().getWindow()).close();
    }
}
