package com.ryl.customControls.other.textfield;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author: EFL-ryl
 */
public class Demo extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/errorTextField.fxml"));
        final Parent root = loader.load();
        if (root == null) {
            return;
        }
        final Scene rootScene = new Scene(root);
        primaryStage.setScene(rootScene);
        primaryStage.show();
    }
}
