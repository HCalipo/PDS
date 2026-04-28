package com.tasku.ui.adapters;

import com.tasku.ui.port.ListaCallback;
import com.tasku.ui.port.NavigationPort;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

public class FxmlNavigationAdapter implements NavigationPort {
    private Stage primaryStage;
    private final Consumer<Object> dependencyInjector;

    public FxmlNavigationAdapter(Stage primaryStage, Consumer<Object> dependencyInjector) {
        this.primaryStage = primaryStage;
        this.dependencyInjector = dependencyInjector;
    }

    @Override
    public void showPrincipal() {
        loadScene("/fxml/Principal.fxml", "TaskU - Principal");
    }

    @Override
    public void showAñadirTablero() {
        showModal("/fxml/AñadirTablero.fxml", "Añadir Tablero");
    }

    @Override
    public void showUnirTablero() {
        showModal("/fxml/UnirTablero.fxml", "Unirse a Tablero");
    }

    @Override
    public void showHistorial() {
        loadScene("/fxml/Historial.fxml", "TaskU - Historial");
    }

    @Override
    public void showAñadirLista(ListaCallback callback) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/AñadirLista.fxml")));
            loader.setControllerFactory(controllerClass -> {
                try {
                    Object controller = controllerClass.getDeclaredConstructor().newInstance();
                    if (dependencyInjector != null) {
                        dependencyInjector.accept(controller);
                    }
                    return controller;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller != null) {
                controller.getClass().getMethod("setCallback", ListaCallback.class).invoke(controller, callback);
            }
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Añadir Lista");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showCreateCard() {
        showModal("/fxml/CreateCard.fxml", "Crear Tarjeta");
    }

    @Override
    public void showCreateTag() {
        showModal("/fxml/CreateTag.fxml", "Crear Etiqueta");
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            if (dependencyInjector != null) {
                loader.setControllerFactory(controllerClass -> {
                    try {
                        Object controller = controllerClass.getDeclaredConstructor().newInstance();
                        dependencyInjector.accept(controller);
                        return controller;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showModal(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            if (dependencyInjector != null) {
                loader.setControllerFactory(controllerClass -> {
                    try {
                        Object controller = controllerClass.getDeclaredConstructor().newInstance();
                        dependencyInjector.accept(controller);
                        return controller;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    public interface Consumer<T> {
        void accept(T t);
    }
}
