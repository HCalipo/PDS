package com.tasku.core.adapters.ui.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class CreateCardController {
    @FXML
    private TextField titleField;
    
    @FXML
    private TextArea descriptionArea;
    
    @FXML
    private ToggleButton toggleTipo;
    
    @FXML
    private ToggleGroup tipoCardGroup;
    
    @FXML
    private ChoiceBox<String> choiceEtiqueta;
    
    @FXML
    private VBox checklistPanel;
    
    @FXML
    private VBox checklistItemsContainer;
    
    @FXML
    private void handleCancel() {
        System.out.println("Cancel create card");
    }
    
    @FXML
    private void handleCreate() {
        String title = titleField.getText();
        String description = descriptionArea.getText();
        
        if (title == null || title.trim().isEmpty()) {
            System.out.println("Title is required");
            return;
        }
        
        String cardType = toggleTipo.isSelected() ? "TEXT" : "CHECKLIST";
        String etiqueta = choiceEtiqueta.getValue();
        
        System.out.println("Create card: " + title + " - Type: " + cardType + " - Etiqueta: " + etiqueta);
        System.out.println("Description: " + description);
    }
    
    @FXML
    private void handleAddChecklistItem() {
        System.out.println("Add checklist item");
    }
    
    @FXML
    private void handleNuevaEtiqueta() {
        System.out.println("Crear nueva etiqueta");
    }
}