package com.ryl.custom.restyleCss;


import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.util.Objects;


/**
 * @author Gerrit Grunwald
 */
public class DemoRestyled extends Application {
    private CheckBox checkBox;
    private Switch reStyleCheckBox;

    @Override
    public void init() {
        checkBox = new CheckBox(".check-box");
        reStyleCheckBox = new Switch(".switch");
    }

    @Override
    public void start(final Stage stage) {
        VBox srcPane = new VBox(30, new CheckBox("CheckBox"), new CheckBox("CheckBox"));
        srcPane.setPadding(new Insets(20));
        VBox pane = new VBox(24, checkBox, reStyleCheckBox);
        pane.setPadding(new Insets(20));
        Scene scene = new Scene(new HBox(srcPane, pane));
        pane.getStylesheets().add(Objects.requireNonNull(DemoRestyled.class.getResource("/restyled.css")).toExternalForm());
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
