
package com.ryl.customControls.led.skinLed;

import com.ryl.customControls.led.skinLed.control.Led;
import com.ryl.customControls.led.skinLed.control.LedBuilder;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


/**
 * Created by
 * User: hansolo
 * Date: 28.02.13
 * Time: 16:34
 */
public class DemoSingle extends Application {
    private static int noOfNodes = 0;
    private Led led;
    private boolean toggle;
    private long lastTimerCall;
    private AnimationTimer timer;


    @Override
    public void init() {
        led = LedBuilder.create()
                .ledType(Led.LedType.ROUND)
                .ledColor(Color.MAGENTA)
                .prefWidth(64)
                .prefHeight(64)
                .build();
        toggle = false;
        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now > lastTimerCall + 500_000_000L) {
                    toggle ^= true;
                    led.setOn(toggle);
                    lastTimerCall = now;
                }
            }
        };
    }

    @Override
    public void start(Stage stage) {
        StackPane pane = new StackPane();
        pane.getChildren().add(led);

        led.setStyle("-led-color: lime;");

        Scene scene = new Scene(pane, 128, 128);

        stage.setScene(scene);
        stage.show();

        timer.start();

        calcNoOfNodes(scene.getRoot());
        System.out.println(noOfNodes + " Nodes in SceneGraph");
    }

    public static void main(String[] args) {
        launch(args);
    }

    // ******************** Misc **********************************************
    private static void calcNoOfNodes(Node node) {
        if (node instanceof Parent) {
            if (((Parent) node).getChildrenUnmodifiable().size() != 0) {
                ObservableList<Node> tempChildren = ((Parent) node).getChildrenUnmodifiable();
                noOfNodes += tempChildren.size();
                for (Node n : tempChildren) {
                    calcNoOfNodes(n);
                    //System.out.println(n.getStyleClass().toString());
                }
            }
        }
    }
}
