package com.tasku.core.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class AñadirTableroController {

    @FXML
    private VBox templateBlank;

    @FXML
    private VBox template1;

    @FXML
    private VBox template2;

    private static final String ACTIVE_CLASS = "template-selector-active";

    @FXML
    private void onTemplateSelected(javafx.scene.input.MouseEvent event) {
        Node selected = event.getPickResult().getIntersectedNode();
        
        VBox templateCard = findParentVBox(selected);
        if (templateCard == null) return;

        clearAllActiveStates();
        templateCard.getStyleClass().add(ACTIVE_CLASS);
    }

    private VBox findParentVBox(Node node) {
        Node current = node;
        while (current != null) {
            if (current instanceof VBox) {
                VBox vbox = (VBox) current;
                if (vbox.getStyleClass().contains("template-selector-card")) {
                    return vbox;
                }
            }
            current = current.getParent();
        }
        return null;
    }

    private void clearAllActiveStates() {
        templateBlank.getStyleClass().remove(ACTIVE_CLASS);
        template1.getStyleClass().remove(ACTIVE_CLASS);
        template2.getStyleClass().remove(ACTIVE_CLASS);
    }
}
