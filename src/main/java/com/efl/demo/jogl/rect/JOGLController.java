package com.efl.demo.jogl.rect;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import org.joml.Vector2f;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author EFL-tjl
 */
public class JOGLController implements Initializable {
    private Camera camera;
    public JOGL_FxCanvas canvas;
    private boolean[] keys = new boolean[65536];
    private boolean[] mouse = new boolean[4];

    private Vector2f start = new Vector2f();
    private Vector2f current = new Vector2f();
    private Vector2f delta = new Vector2f();

    @FXML
    private ScrollPane pane3D;
    @FXML
    private ImageView orthoImage;

    public Camera getCamera() {
        return camera;
    }

    /**
     * 返回鼠标的按键状态
     * @param button
     * @return
     */
    public boolean getMouseButton(int button) {
        return mouse[button];
    }
    /**
     * 获得鼠标的起点坐标
     * @return
     */
    public Vector2f getStart() {
        return start;
    }
    /**
     * 获得鼠标的当前坐标
     * @return
     */
    public Vector2f getCurrent() {
        return current;
    }
    /**
     * 返回鼠标的相对位移
     * @return
     */
    public Vector2f getDelta() {
        return delta;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 创建摄像机
        camera = new Camera(640, 480);
        canvas = new JOGL_FxCanvas(640, 480, camera);
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
    void keyPressed(KeyEvent event) {

    }

    public void checkOrthoAndRepaint() {
        if (GLEventImpl.ortho) {
            canvasReshape();
        } else {
            canvas.repaint();
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

    @FXML
    void mousePressed(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            mouse[0] = true;
        } else if (event.getButton() == MouseButton.SECONDARY) {
            mouse[1] = true;
        } else if (event.getButton() == MouseButton.MIDDLE) {
            mouse[2] = true;
        } else {
            mouse[3] = true;
        }
        start.set((float)event.getX(), (float)event.getY());
        current.set((float)event.getX(), (float)event.getY());
        delta.set(0, 0);
    }

    @FXML
    void mouseReleased(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            mouse[0] = false;
        } else if (event.getButton() == MouseButton.SECONDARY) {
            mouse[1] = false;
        } else if (event.getButton() == MouseButton.MIDDLE) {
            mouse[2] = false;
        } else {
            mouse[3] = false;
        }
    }

    // 记录鼠标的点击位置，用于计算鼠标在画布上的相对位移。
    private Vector2f last = new Vector2f(-1, -1);
    private Vector2f cur = new Vector2f();
    // 鼠标灵敏度
    private float sensitive = 0.003f;
    @FXML
    void mouseDragged(MouseEvent event) {
        current.set((float)event.getX(), (float)event.getY());
        current.sub(start, delta);

        if (mouse[0]) {
            // 首次按键PRIMARY
            //if (last.x == -1 && last.y == -1) {
                last.set(start);
            //}
            cur.set(current);

            // 计算相对位移
            float dx = cur.x - last.x;
            float dy = cur.y - last.y;

            if (dx*dx + dy*dy > 0) {
                camera.rotate(-dy * sensitive, -dx * sensitive, 0);
                last.set(cur);
            }

        } else {
            last.set(-1, -1);
        }
        start.set((float)event.getX(), (float)event.getY());
        canvasReshape();
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
        camera.setParallel(!GLEventImpl.ortho);
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
