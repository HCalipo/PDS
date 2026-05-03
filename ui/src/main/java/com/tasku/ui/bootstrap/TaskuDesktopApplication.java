package com.tasku.ui.bootstrap;

import com.tasku.ui.SceneManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class TaskuDesktopApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(TaskuDesktopApplication.class);

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) throws IOException {
        log.info("Arrancando TaskU Desktop");
        SceneManager.getInstance().setPrimaryStage(stage);

        Parent root = FXMLLoader.load(Objects.requireNonNull(
                getClass().getResource("/fxml/InicioSesion.fxml")));
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("TaskU");
        stage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();

       // Para permitir movimiento de la ventana: 
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        

        
    }

    public static void main(String[] args) {
        URL configUrl = TaskuDesktopApplication.class.getResource("/log4j2.xml");
        if (configUrl != null) {
            System.setProperty("log4j2.configurationFile", configUrl.toExternalForm());
        }
        launch(args);
    }
}
