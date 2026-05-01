package com.tasku.ui.presentation.controllers;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class CreateTagController {

    @FXML private TextField txtNombre;
    @FXML private GridPane gridColores;
    @FXML private Circle colorPreview;
    @FXML private Label lblColorHex;
    @FXML private ToggleGroup paletaGroup;

    private String selectedColor = "#0ea5e9";
    private String resultName;
    private String resultColor;

    private static final Map<String, List<String>> PALETTES = Map.of(
        // Un tono representativo por cada zona del círculo cromático, saturación media-alta
        "Normal", List.of(
            "#EF4444", "#F97316", "#EAB308", "#22C55E", "#14B8A6",
            "#3B82F6", "#6366F1", "#A855F7", "#EC4899", "#64748B"
        ),
        // Mismas zonas cromáticas pero suaves: alta luminosidad, baja saturación
        "Pastel", List.of(
            "#FCA5A5", "#FDBA74", "#FDE68A", "#86EFAC", "#99F6E4",
            "#93C5FD", "#A5B4FC", "#D8B4FE", "#F9A8D4", "#CBD5E1"
        ),
        // Colores eléctricos/fluorescentes: saturación máxima, muy vibrantes
        "Neón", List.of(
            "#FF1744", "#FF6D00", "#FFD600", "#00E676", "#1DE9B6",
            "#2979FF", "#651FFF", "#D500F9", "#FF4081", "#76FF03"
        ),
        // Espectro CÁLIDO: rojo ladrillo → naranja → ámbar → dorado → marrones → olivas
        "Tierra", List.of(
            "#C0392B", "#D35400", "#E67E22", "#F39C12", "#D4AC0D",
            "#7D5A3C", "#5C3317", "#808000", "#6B8E23", "#4E7C32"
        ),
        // Espectro FRÍO y profundo: verdes fríos → azules → índigos → púrpuras → granates
        "Oscuro", List.of(
            "#145A32", "#0D4F5C", "#0A3D62", "#1A237E", "#4A148C",
            "#6A1B9A", "#880E4F", "#4E1929", "#263238", "#1F2D3D"
        )
    );

    @FXML
    private void initialize() {
        buildColorGrid(PALETTES.get("Normal"));
        updatePreview(selectedColor);
    }

    @FXML
    private void handleTogglePaleta() {
        Toggle selected = paletaGroup.getSelectedToggle();
        if (selected instanceof ToggleButton tb) {
            List<String> palette = PALETTES.get(tb.getText());
            if (palette != null) {
                buildColorGrid(palette);
            }
        }
    }

    private void buildColorGrid(List<String> colors) {
        gridColores.getChildren().clear();
        int col = 0, row = 0;
        for (String hex : colors) {
            Circle circle = new Circle(14);
            circle.setFill(Color.web(hex));
            circle.setStroke(Color.web("#e2e8f0"));
            circle.setStrokeWidth(2);
            circle.setCursor(Cursor.HAND);
            circle.setOnMouseClicked(e -> updatePreview(hex));
            gridColores.add(circle, col, row);
            col++;
            if (col >= 5) { col = 0; row++; }
        }
    }

    private void updatePreview(String hex) {
        selectedColor = hex;
        colorPreview.setFill(Color.web(hex));
        lblColorHex.setText(hex);
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    @FXML
    private void handleCreate() {
        String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
        if (nombre.isBlank()) {
            txtNombre.setStyle("-fx-border-color: #dc2626; -fx-border-width: 2px;");
            return;
        }
        resultName = nombre;
        resultColor = selectedColor;
        closeDialog();
    }

    public String getResultName() {
        return resultName;
    }

    public String getResultColor() {
        return resultColor;
    }

    private void closeDialog() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }
}
