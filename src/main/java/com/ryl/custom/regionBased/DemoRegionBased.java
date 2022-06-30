package com.ryl.custom.regionBased;

import javafx.event.EventHandler;
import javafx.scene.layout.BorderPane;
import javafx.stage.StageStyle;
import com.ryl.custom.regionBased.RegionControl.Type;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.util.function.Consumer;


/**
 * @author hansolo
 */
public class DemoRegionBased extends Application {
    private RegionControl redButton;
    private RegionControl yellowButton;
    private RegionControl greenButton;
    private HBox buttonBox;

    private Stage stage;
    private static double xOffset = 0;
    private static double yOffset = 0;



    @Override
    public void init() {
        redButton = new RegionControl(Type.CLOSE);
        yellowButton = new RegionControl(Type.MINIMIZE);
        greenButton = new RegionControl(Type.ZOOM);
        buttonBox = new HBox(8, redButton, yellowButton, greenButton);

        registerListeners();
    }

    private void registerListeners() {
        redButton.setOnMousePressed((Consumer<MouseEvent>) e -> {
            //"Close pressed"
            stage.close();
        });

        yellowButton.setOnMousePressed((Consumer<MouseEvent>) e -> {
            //"Minimized pressed"
            stage.setIconified(true);
        });

        greenButton.setOnMousePressed((Consumer<MouseEvent>) e -> {
            //"Maximized pressed"
            stage.setMaximized(!stage.isMaximized());
            greenButton.setState(!greenButton.getState());
        });

        buttonBox.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> {
            redButton.setHovered(true);
            yellowButton.setHovered(true);
            greenButton.setHovered(true);
        });
        buttonBox.addEventFilter(MouseEvent.MOUSE_EXITED, e -> {
            redButton.setHovered(false);
            yellowButton.setHovered(false);
            greenButton.setHovered(false);
        });
    }

    @Override
    public void start(final Stage stage) {
        this.stage = stage;
        BorderPane pane = new BorderPane();
        pane.setTop(buttonBox);
        pane.setPadding(new Insets(8));
        pane.setPrefSize(500,300);
        pane.setOnMousePressed(event -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });
        pane.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });
        Scene scene = new Scene(pane);
        stage.initStyle(StageStyle.UNDECORATED);
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