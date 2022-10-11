package com.ryl.customControls.other.explorer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * @author: EFL-ryl
 */
public class Demo extends Application {
    @Override
    public void start(Stage stage) {
        ExplorerControl<String> control = new ExplorerControl<>();
        Scene scene = new Scene(control);
        scene.setFill(Color.rgb(208, 69, 28));

        stage.setTitle("JavaFX Custom Control");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
