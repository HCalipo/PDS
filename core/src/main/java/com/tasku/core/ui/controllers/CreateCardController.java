package com.tasku.core.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class CreateCardController {
    @FXML
    private TextField titleField;
    
    @FXML
    private TextArea descriptionArea;
    
    @FXML
    private RadioButton radioTexto;
    
    @FXML
    private RadioButton radioChecklist;
    
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
        
        String cardType = radioChecklist.isSelected() ? "CHECKLIST" : "TEXT";
        
        System.out.println("Create card: " + title + " - Type: " + cardType);
        System.out.println("Description: " + description);
    }
    
    @FXML
    private void handleAddChecklistItem() {
        System.out.println("Add checklist item");
    }
}
