

package com.ryl.customControls.button.pushButton;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Demo extends Application {
    @Override public void start(Stage stage) {
        PushButton control = PushButtonBuilder.create()
                                              .prefWidth(150)
                                              .prefHeight(43)
                                              .build();

        ToggleButton button = new ToggleButton("ToggleButton");
        button.setPrefSize(150, 43);
        button.getStylesheets().add(getClass().getResource("/button/pushButton/demo.css").toExternalForm());

        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.getChildren().setAll(control, button);

        Scene scene = new Scene(vBox);
        scene.setFill(Color.rgb(208,69,28));

        stage.setTitle("JavaFX Custom Control");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}


