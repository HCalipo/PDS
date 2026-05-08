package com.tasku.ui.presentation.controllers;

import com.tasku.ui.client.dto.response.CardApiResponse;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditarTarjetaTextoController {

    @FXML
    private TextField txtTitulo;

    @FXML
    private javafx.scene.control.Button btnCerrar;

    private boolean saved = false;
    private String newTitle;

    public void setCard(CardApiResponse card) {
        txtTitulo.setText(card.title());
        txtTitulo.requestFocus();
        txtTitulo.selectAll();
    }

    @FXML
    private void handleGuardar() {
        String title = txtTitulo.getText().trim();
        if (title.isBlank()) return;
        this.newTitle = title;
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

    public String getNewTitle() {
        return newTitle;
    }

    private void close() {
        ((Stage) txtTitulo.getScene().getWindow()).close();
    }
}
