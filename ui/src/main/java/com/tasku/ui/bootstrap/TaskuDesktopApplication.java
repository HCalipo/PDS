package com.tasku.ui.bootstrap;

import com.tasku.ui.SceneManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class TaskuDesktopApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        SceneManager.getInstance().setPrimaryStage(stage);

        Parent root = FXMLLoader.load(Objects.requireNonNull(
                getClass().getResource("/fxml/inicioSesion.fxml")));
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("TaskU");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
