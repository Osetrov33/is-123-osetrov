package com;

import com.controller.LoginController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        stage.initStyle(StageStyle.UNDECORATED);
        LoginController loginController = new LoginController(stage);

        Scene scene = new Scene(loginController.getView(), 1480, 920);
        scene.getStylesheets().add(
                getClass().getResource("/style/styles.css").toExternalForm()
        );

        stage.getIcons().add(new Image(
                getClass().getResourceAsStream("/style/pulsebeat-icon.png")
        ));
        stage.setMinWidth(1240);
        stage.setMinHeight(780);
        stage.setTitle("PulseBeat");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
