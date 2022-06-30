package com.ryl.custom.comb;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Objects;


/**
 * @author Gerrit Grunwald
 * Description: 组合组件程序demo
 */
public class DemoCombined extends Application {
    private CombinedControl control;

    @Override
    public void init() {
        control = new CombinedControl();
    }

    @Override
    public void start(final Stage stage) {
        Pane pane = new Pane();
        control.setLayoutX(80);
        control.setLayoutY(60);
        pane.getChildren().add(control);
        pane.setPadding(new Insets(40));
        Scene scene = new Scene(pane);
        //添加css样式表
        scene.getStylesheets().add(Objects.requireNonNull(DemoCombined.class.getResource("/combined.css")).toExternalForm());
        stage.setTitle("Combined");
        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
