package com.tasku.core.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CreateTagController {

    private static final String[] COLORES_NORMALES = {
        "#e74c3c", "#e67e22", "#f1c40f", "#2ecc71", "#1abc9c",
        "#3498db", "#9b59b6", "#e91e63", "#34495e", "#95a5a6"
    };

    private static final String[] COLORES_PASTEL = {
        "#fadbd8", "#fdebd0", "#f9e79f", "#abebc6", "#a3e4d7",
        "#aed6f1", "#d7bde2", "#f5b7b1", "#d5dbdb", "#f1948a"
    };

    @FXML
    private TextField txtNombre;

    @FXML
    private ToggleButton toggleNormal;

    @FXML
    private ToggleGroup paletaGroup;

    @FXML
    private GridPane gridColores;

    @FXML
    private Circle colorPreview;

    @FXML
    private Label lblColorHex;

    private String colorSeleccionado = COLORES_NORMALES[0];
    private ObservableList<Circle> circles = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        actualizarPaleta();
        toggleNormal.setOnAction(e -> actualizarPaleta());
        
        ToggleButton pastel = (ToggleButton) paletaGroup.getToggles().get(1);
        pastel.setOnAction(e -> actualizarPaleta());
    }

    private void actualizarPaleta() {
        String[] colores = toggleNormal.isSelected() ? COLORES_NORMALES : COLORES_PASTEL;
        
        gridColores.getChildren().clear();
        circles.clear();

        for (int i = 0; i < colores.length; i++) {
            Circle circle = new Circle(18);
            circle.setFill(Color.web(colores[i]));
            circle.getStyleClass().add("color-swatch");
            circle.setUserData(colores[i]);
            circle.setOnMouseClicked(e -> seleccionarColor(circle));
            
            circles.add(circle);
            
            int col = i % 5;
            int row = i / 5;
            gridColores.add(circle, col, row);
        }

        colorSeleccionado = colores[0];
        actualizarPreview();
    }

    private void seleccionarColor(Circle circle) {
        for (Circle c : circles) {
            c.getStyleClass().remove("color-swatch-selected");
        }
        circle.getStyleClass().add("color-swatch-selected");
        
        colorSeleccionado = (String) circle.getUserData();
        actualizarPreview();
    }

    private void actualizarPreview() {
        colorPreview.setFill(Color.web(colorSeleccionado));
        colorPreview.setStroke(Color.web(colorSeleccionado).darker().darker());
        lblColorHex.setText(colorSeleccionado);
    }

    @FXML
    private void handleCancel() {
        System.out.println("Cancel create tag");
    }

    @FXML
    private void handleCreate() {
        String nombre = txtNombre.getText();
        
        if (nombre == null || nombre.trim().isEmpty()) {
            System.out.println("El nombre de la etiqueta es requerido");
            return;
        }
        
        System.out.println("Crear etiqueta: " + nombre + " con color: " + colorSeleccionado);
    }
}
