package com.tasku.ui.bootstrap;

import com.tasku.ui.adapters.ApiEtiquetaAdapter;
import com.tasku.ui.adapters.ApiSessionAdapter;
import com.tasku.ui.adapters.ApiTableroAdapter;
import com.tasku.ui.client.http.TaskuApiClient;
import com.tasku.ui.port.EtiquetaPort;
import com.tasku.ui.port.NavigationPort;
import com.tasku.ui.port.SessionService;
import com.tasku.ui.port.TableroPort;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class TaskuDesktopApplication extends Application {

    private TaskuApiClient apiClient;
    private SessionService sessionService;
    private TableroPort tableroPort;
    private NavigationPort navigationPort;
    private EtiquetaPort etiquetaPort;

    @Override
    public void init() {
        apiClient = new TaskuApiClient();
        sessionService = new ApiSessionAdapter(apiClient);
        tableroPort = new ApiTableroAdapter(apiClient);
        etiquetaPort = new ApiEtiquetaAdapter(apiClient);
    }

    @Override
    public void start(Stage stage) throws IOException {
        navigationPort = new com.tasku.ui.adapters.FxmlNavigationAdapter(stage, this::injectDependencies);

        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/Login.fxml")));
        loader.setControllerFactory(this::createController);

        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("TaskU");
        stage.setScene(scene);
        stage.show();
    }

    private Object createController(Class<?> controllerClass) {
        try {
            Object controller = controllerClass.getDeclaredConstructor().newInstance();
            injectDependencies(controller);
            return controller;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void injectDependencies(Object controller) {
        try {
            var sessionSetter = controller.getClass().getMethod("setSessionService", SessionService.class);
            sessionSetter.invoke(controller, sessionService);
        } catch (NoSuchMethodException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            var tableroSetter = controller.getClass().getMethod("setTableroPort", TableroPort.class);
            tableroSetter.invoke(controller, tableroPort);
        } catch (NoSuchMethodException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            var navSetter = controller.getClass().getMethod("setNavigationPort", NavigationPort.class);
            navSetter.invoke(controller, navigationPort);
        } catch (NoSuchMethodException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            var etiquetaSetter = controller.getClass().getMethod("setEtiquetaPort", EtiquetaPort.class);
            etiquetaSetter.invoke(controller, etiquetaPort);
        } catch (NoSuchMethodException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}