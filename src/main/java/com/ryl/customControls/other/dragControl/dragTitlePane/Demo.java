package com.ryl.customControls.other.dragControl.dragTitlePane;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @author: EFL-ryl
 */
public class Demo extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            PanesContainer container = new PanesContainer();
            Pane root = container.getRootPane();
            Scene scene = new Scene(root);
            primaryStage.setTitle("DndTitledPaneExample");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
