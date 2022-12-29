package com.efl.demo.jogl.rect;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

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
    @FXML
    private ImageView orthoImage;

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
    void mouseScroll(ScrollEvent event) {
        //DeltaY会产生响应
        double wheel = event.getDeltaY();
        if (wheel > 0) {
            GLEventImpl.small();
        } else if (wheel < 0) {
            GLEventImpl.enlarge();
        }
        checkOrthoAndRepaint();
    }

    public void checkOrthoAndRepaint() {
        if (GLEventImpl.ortho) {
            canvasReshape();
        } else {
            canvas.repaint();
        }
    }

    @FXML
    void mousePressed(MouseEvent event) {
        x0 = (int) event.getX();
        y0 = (int) event.getY();
    }


    @FXML
    void mouseDragged(MouseEvent event) {
        //拖拽增量，不同于swing，原点在左下角
        int dx = (int) event.getX() - x0;
        int dy = y0 - (int) event.getY();

        //左键拖拽——视角旋转
        if (event.getButton() == MouseButton.PRIMARY) {
            if (dx != 0) {
                GLEventImpl.rotateZ(dx * 0.5);
            }
            if (dy != 0) {
                GLEventImpl.rotateX(dy * -0.5);
            }
        } else {
            float[] rotate = GLEventImpl.rotate;
            double moveX = (dx * Math.cos(rotate[1] / 180 * Math.PI) + dy * Math.cos(rotate[0] / 180 * Math.PI) * Math.sin(rotate[1] / 180 * Math.PI)) * 0.5 / GLEventImpl.scale;
            double moveY = (dy * Math.cos(rotate[0] / 180 * Math.PI) * Math.cos(rotate[1] / 180 * Math.PI) - dx * Math.sin(rotate[1] / 180 * Math.PI)) * 0.5 / GLEventImpl.scale;

            //中键拖拽——模型平移
            if (event.getButton() == MouseButton.MIDDLE) {
                //todo
                return;
            }

            //右键拖拽——视角平移
            if (event.getButton() == MouseButton.SECONDARY) {
                GLEventImpl.translate[0] += moveX;

                GLEventImpl.translate[1] += moveY;

                GLEventImpl.translate[2] += -dy * Math.sin(rotate[0] / 180 * Math.PI) * 0.5;
            }
        }
        canvas.repaint();
        x0 = (int) event.getX();
        y0 = (int) event.getY();
    }

    @FXML
    void orthoView() {
        if (GLEventImpl.ortho) {
            GLEventImpl.ortho = false;
            orthoImage.setImage(new Image("/icons/orthoNot.png"));
        } else {
            GLEventImpl.ortho = true;
            orthoImage.setImage(new Image("/icons/ortho.png"));
        }
        canvasReshape();
    }

    @FXML
    void frontView() {
        GLEventImpl.rotate[0] = -90;
        GLEventImpl.rotate[1] = 0;
        GLEventImpl.resetTranslate();
        canvas.repaint();
    }

    @FXML
    void topView() {
        GLEventImpl.rotate[0] = 0;
        GLEventImpl.rotate[1] = 0;
        GLEventImpl.resetTranslate();
        canvas.repaint();
    }

    @FXML
    void leftView() {
        GLEventImpl.rotate[0] = -90;
        GLEventImpl.rotate[1] = 90;
        GLEventImpl.resetTranslate();
        canvas.repaint();
    }

    @FXML
    void isometricView() {
        GLEventImpl.scale = 1;
        GLEventImpl.rotate[0] = -70;
        GLEventImpl.rotate[1] = 20;
        GLEventImpl.resetTranslate();
        checkOrthoAndRepaint();
    }

}
