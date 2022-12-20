package com.efl.demo.jogl.rect;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author EFL-tjl
 */
public class JOGLController implements Initializable {
    public JOGL_FxCanvas canvas;
    int x0, y0;
    @FXML
    private ScrollPane pane3D;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        canvas = new JOGL_FxCanvas(640, 480);
        pane3D.setContent(canvas);
        //去除scrollpane的蓝色边框，此种方法将来可能会过时
        pane3D.getStyleClass().add("edge-to-edge");
        addListener();
    }

    private void addListener() {
        pane3D.widthProperty().addListener((observable, oldValue, newValue) -> canvasReshape());
        pane3D.heightProperty().addListener((observable, oldValue, newValue) -> canvasReshape());
    }

    /**
     * 在修改GLEventImpl的scale值时，重绘canvas需要调用此函数
     */
    public void canvasReshape() {
        if (pane3D.getWidth() > 0 && pane3D.getHeight() > 0) {
            canvas.reshapeAndRepaint((int) pane3D.getWidth(), (int) pane3D.getHeight());
        }
    }

    @FXML
    void mousePressed(MouseEvent event) {
        x0 = (int) event.getX();
        y0 = (int) event.getY();
    }
}
