package com.ryl.customControls.menu.radialMenu;

import com.ryl.common.SymbolType;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


/**
 * Created with IntelliJ IDEA.
 * User: hansolo
 * Date: 27.09.13
 * Time: 07:40
 * To change this template use File | Settings | File Templates.
 */
public class DemoSimple extends Application {

    @Override public void start(Stage stage) {
        final RadialMenu radialMenu = RadialMenuBuilder.create()
                                              .options(OptionsBuilder.create()
                                                               .degrees(360)
                                                               .offset(-90)
                                                               .radius(200)
                                                               .buttonSize(72)
                                                               .buttonHideOnSelect(true)
                                                               .buttonHideOnClose(false)
                                                               .buttonAlpha(1.0)
                                                               .simpleMode(true)
                                                               .strokeVisible(false)
                                                               .build())
                                              .items(MenuItemBuilder.create().thumbnailImageName(getClass().getResource("/menu/radialMenu/star.png").toExternalForm()).size(64).build(),
                                                            MenuItemBuilder.create().symbol(SymbolType.LOCATION).tooltip("Location").size(64).build(),
                                                            MenuItemBuilder.create().selectable(true).symbol(SymbolType.MUSIC).tooltip("Music").size(64).build(),
                                                            MenuItemBuilder.create().symbol(SymbolType.SPEECH_BUBBLE).tooltip("Chat").size(64).build(),
                                                            MenuItemBuilder.create().symbol(SymbolType.BLUE_TOOTH).tooltip("Bluetooth").size(64).build(),
                                                            MenuItemBuilder.create().symbol(SymbolType.BULB).tooltip("Ideas").size(64).build(),
                                                            MenuItemBuilder.create().symbol(SymbolType.HEAD_PHONES).tooltip("Sound").size(64).build(),
                                                            MenuItemBuilder.create().symbol(SymbolType.TWITTER).tooltip("Twitter").size(64).build(),
                                                            MenuItemBuilder.create().symbol(SymbolType.TAGS).tooltip("Tags").size(64).build(),
                                                            MenuItemBuilder.create().symbol(SymbolType.CART).tooltip("Shop").size(64).build(),
                                                            MenuItemBuilder.create().symbol(SymbolType.ALARM).tooltip("Alarm").size(64).build(),
                                                            MenuItemBuilder.create().symbol(SymbolType.CLOCK).tooltip("Clock").size(64).build())
                                              .build();
        radialMenu.setPrefSize(500, 500);
        radialMenu.setOnItemSelected(selectionEvent -> System.out.println("item " + selectionEvent.item.getTooltip() + " selected"));
        radialMenu.setOnItemClicked(clickEvent -> System.out.println("item " + clickEvent.item.getTooltip() + " clicked"));
        radialMenu.setOnMenuOpenStarted(menuEvent -> System.out.println("Menu starts to open"));
        radialMenu.setOnMenuOpenFinished(menuEvent -> System.out.println("Menu finished to open"));
        radialMenu.setOnMenuCloseStarted(menuEvent -> System.out.println("Menu starts to close"));
        radialMenu.setOnMenuCloseFinished(menuEvent -> System.out.println("Menu finished to close"));

        HBox buttons = new HBox();
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(10, 10, 10, 10));
        Button buttonShow = new Button("Show menu");
        buttonShow.setOnAction(actionEvent -> radialMenu.show());
        buttons.getChildren().add(buttonShow);

        Button buttonHide = new Button("Hide menu");
        buttonHide.setOnAction(actionEvent -> radialMenu.hide());
        buttons.getChildren().add(buttonHide);

        VBox pane = new VBox();
        pane.getChildren().add(radialMenu);
        pane.getChildren().add(buttons);
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(150, 150, 150), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
