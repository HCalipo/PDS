package com.tasku.ui;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;

public class SceneManager {

    private static final SceneManager INSTANCE = new SceneManager();

    private Stage primaryStage;
    private String currentUserEmail;
    private String currentBoardUrl;
    private String currentBoardName;
    private UUID currentListId;

    private SceneManager() {}

    public static SceneManager getInstance() {
        return INSTANCE;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setCurrentUserEmail(String email) {
        this.currentUserEmail = email;
    }

    public String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public void setCurrentBoard(String url, String name) {
        this.currentBoardUrl = url;
        this.currentBoardName = name;
    }

    public String getCurrentBoardUrl() {
        return currentBoardUrl;
    }

    public String getCurrentBoardName() {
        return currentBoardName;
    }

    public void setCurrentListId(UUID listId) {
        this.currentListId = listId;
    }

    public UUID getCurrentListId() {
        return currentListId;
    }

    public void switchTo(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlName + ".fxml"));
            Parent root = loader.load();
            Scene current = primaryStage.getScene();
            if (current != null) {
                current.setRoot(root);
            } else {
                primaryStage.setScene(new Scene(root));
            }
        } catch (IOException e) {
            System.err.println("Error al cargar la vista " + fxmlName + ": " + e.getMessage());
        }
    }

    public Stage openDialog(String fxmlName) {
        return openDialogAndGetController(fxmlName, null);
    }

    public <T> T openDialogAndGetController(String fxmlName, Consumer<T> controllerInitializer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlName + ".fxml"));
            Parent root = loader.load();
            T controller = loader.getController();

            if (controllerInitializer != null && controller != null) {
                controllerInitializer.accept(controller);
            }

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage);
            dialog.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            dialog.setScene(scene);

            syncBoundsWithOwner(dialog);
            dialog.showAndWait();
            return controller;
        } catch (IOException e) {
            System.err.println("Error al abrir dialogo " + fxmlName + ": " + e.getMessage());
            return null;
        }
    }

    private void syncBoundsWithOwner(Stage dialog) {
        if (primaryStage == null) return;

        dialog.setX(primaryStage.getX());
        dialog.setY(primaryStage.getY());
        dialog.setWidth(primaryStage.getWidth());
        dialog.setHeight(primaryStage.getHeight());

        ChangeListener<Number> x = (o, ov, nv) -> dialog.setX(nv.doubleValue());
        ChangeListener<Number> y = (o, ov, nv) -> dialog.setY(nv.doubleValue());
        ChangeListener<Number> w = (o, ov, nv) -> dialog.setWidth(nv.doubleValue());
        ChangeListener<Number> h = (o, ov, nv) -> dialog.setHeight(nv.doubleValue());

        primaryStage.xProperty().addListener(x);
        primaryStage.yProperty().addListener(y);
        primaryStage.widthProperty().addListener(w);
        primaryStage.heightProperty().addListener(h);

        dialog.setOnHidden(e -> {
            primaryStage.xProperty().removeListener(x);
            primaryStage.yProperty().removeListener(y);
            primaryStage.widthProperty().removeListener(w);
            primaryStage.heightProperty().removeListener(h);
        });
    }
}
