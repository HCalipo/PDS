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

    private boolean newUser = false;

    private double xOffset = 0;
    private double yOffset = 0;


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

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }

    public boolean consumeNewUser() {
        boolean val = newUser;
        newUser = false;
        return val;
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
            
            //para el movimiento de las ventanas
            root.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            root.setOnMouseDragged(event -> {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            });

            Scene current = primaryStage.getScene();
            if (current != null) {
                current.setRoot(root); 
            } else {
                primaryStage.setScene(new Scene(root));
            }
        } catch (IOException e) {
        }
    }

    //funcion para abrir la ventana principal con las cosas de windows y maximizada.
    public void startMainApp() {
        try {
            // 1. Cargar la vista principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Principal.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // 2. Crear un Stage COMPLETAMENTE NUEVO
            Stage tableroStage = new Stage();
            tableroStage.setTitle("TaskU");
            tableroStage.setScene(scene);
            
            tableroStage.initStyle(StageStyle.DECORATED);
            tableroStage.setMaximized(true);
            tableroStage.show();

    
            if (primaryStage != null) {
                primaryStage.close();
            }
            //para no cambiar futuras referencias.
            this.primaryStage = tableroStage;

        } catch (IOException e) {
        }
    }

    public void openDialog(String fxmlName) {
        openDialogAndGetController(fxmlName, null);
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

            // TODO: hacer que se mueva la ventana.
            
            
            root.applyCss();
            root.layout();
            double prefWidth = root.prefWidth(-1);
            double prefHeight = root.prefHeight(-1);
            double minWidth = root.minWidth(-1);
            double minHeight = root.minHeight(-1);

            //aplicar tamaño mínimo para los popUps
            dialog.setMinWidth(minWidth > 0 ? minWidth : prefWidth);
            dialog.setMinHeight(minHeight > 0 ? minHeight : prefHeight);

            //centrarar el popUP
            if (primaryStage != null) {
                dialog.setX(primaryStage.getX() + (primaryStage.getWidth() / 2) - (prefWidth / 2));
                dialog.setY(primaryStage.getY() + (primaryStage.getHeight() / 2) - (prefHeight / 2));
            }



            dialog.showAndWait();
            return controller;
        } catch (IOException e) {
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
