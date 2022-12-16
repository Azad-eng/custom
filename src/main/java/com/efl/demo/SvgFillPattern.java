package com.efl.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;

/**
 * @author: EFL-ryl
 */
public class SvgFillPattern extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // set title for the stage
        stage.setTitle("creating Polyline");

        // points
        double points[] = {20.0d, 20.0d, 40.0d, 240.0d, 60.0d,
                180.0d, 80.0d, 200.0d, 100.0d, 90.0d};

        double points2[] = {120.0d, 120.0d, 140.0d, 240.0d, 160.0d,
                180.0d, 180.0d, 200.0d, 100.0d, 90.0d};

        // create a polyline
        Polyline polyline = new Polyline(points);
        Polyline polyline2 = new Polyline(points2);
        polyline.setStroke(Paint.valueOf("white"));
        polyline.setStrokeWidth(5);
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: gold;");
        anchorPane.getChildren().addAll(polyline, polyline2);

        // create a scene
        Scene scene = new Scene(anchorPane, 500, 300);

        // set the scene
        stage.setScene(scene);

        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
