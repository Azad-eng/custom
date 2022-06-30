package com.ryl.custom.extended;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author Gerrit Grunwald
 */
public class DemoExtended extends Application {
    private ExtendedControl control;
    private Button button;

    @Override
    public void init() {
        control = new ExtendedControl();
        control.setPromptText("Name");
        button = new Button("Focus");
    }

    @Override
    public void start(final Stage stage) {
        VBox pane = new VBox(24, control, button);
        pane.setPadding(new Insets(20));
        Scene scene = new Scene(pane);
        stage.setTitle("Extended");
        stage.setScene(scene);
        stage.show();
        button.requestFocus();
    }

    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}