package com.ryl.custom.controlSkin;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import com.ryl.custom.controlSkin.CustomControl.SkinType;

/**
 * @author Gerrit Grunwald
 * Description: 全新自定义组件程序demo
 */
public class DemoControlSkinBased extends Application {
    private CustomControl ledControl;
    private CustomControl switchControl;

    @Override
    public void init() throws Exception {
        ledControl = new CustomControl();
        ledControl.setState(true);
        ledControl.setPrefSize(100, 100);
        ledControl.setColor(Color.LIME);

        switchControl = new CustomControl(SkinType.SWITCH);
        switchControl.setState(true);
        switchControl.setColor(Color.web("#4bd865"));
        switchControl.stateProperty().addListener((o, ov, nv) -> ledControl.setState(nv));
    }

    @Override
    public void start(Stage stage) throws Exception {
        VBox pane = new VBox(20, ledControl, switchControl);
        pane.setPadding(new Insets(20));
        Scene scene = new Scene(pane, 200, 200);
        scene.getStylesheets().add(DemoControlSkinBased.class.getResource("/controlSkin/styles.css").toExternalForm());
        stage.setTitle("Control-Skin based Control");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
