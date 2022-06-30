package com.ryl.custom.restyleCss;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;


/**
 * @author Gerrit Grunwald
 */
public class DemoRestyled extends Application {
    private CheckBox checkBox;
    private Switch reStyleCheckBox;

    @Override
    public void init() {
        checkBox = new CheckBox("CheckBox");
        reStyleCheckBox = new Switch("Restyled CheckBox Design");
    }

    @Override
    public void start(final Stage stage) {
        VBox pane = new VBox(24, checkBox, reStyleCheckBox);
        pane.setPadding(new Insets(20));
        Scene scene = new Scene(pane);
        scene.getStylesheets().add(Objects.requireNonNull(DemoRestyled.class.getResource("/restyled.css")).toExternalForm());
        stage.setTitle("Restyled");
        stage.setScene(scene);
        stage.show();
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
