package com.ryl.customControls.meter.oldMeter;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * User: hansolo
 * Date: 26.11.18
 * Time: 12:50
 */
public class Demo extends Application {
    private Odometer odometer;
    private Timeline timeline;

    @Override public void init() {
        odometer = OdometerBuilder.create()
                .digits(6)
                .decimals(1)
                .digitBackgroundColor(Color.WHITE)
                .digitForegroundColor(Color.BLACK)
                .decimalBackgroundColor(Color.BLACK)
                .decimalForegroundColor(Color.WHITE)
                .build();
        timeline = new Timeline();

        KeyValue kv0 = new KeyValue(odometer.valueProperty(), 0, Interpolator.LINEAR);
        KeyValue kv1 = new KeyValue(odometer.valueProperty(), 120, Interpolator.LINEAR);

        KeyFrame kf0 = new KeyFrame(Duration.ZERO, kv0);
        KeyFrame kf1 = new KeyFrame(Duration.seconds(1200), kv1);

        timeline.getKeyFrames().setAll(kf0, kf1);
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(odometer);
        pane.setPadding(new Insets(10));
        pane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane);

        stage.setTitle("Odometer");
        stage.setScene(scene);
        stage.show();

        timeline.play();
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}