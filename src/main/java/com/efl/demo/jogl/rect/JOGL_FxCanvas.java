package com.efl.demo.jogl.rect;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLOffscreenAutoDrawable;
import com.jogamp.opengl.GLProfile;
import javafx.scene.canvas.Canvas;

/**
 * @author: EFL-ryl
 */
public class JOGL_FxCanvas extends Canvas {
    private final GLOffscreenAutoDrawable glDrawBuffer;
    private final GLEventImpl glEvent;

    public JOGL_FxCanvas(int width, int height, Camera camera) {
        super(width, height);

        //从opengl获取的图像的坐标原点在屏幕左上角，而fxCanvas的坐标原点在左下角，需要进行沿Y轴翻转（也可能是opengl坐标系原点在左下角，屏幕原点在左上角，总之进行一次Y轴翻转显示的图像符合笛卡尔坐标系）
        this.setScaleY(-1);

        GLProfile.initSingleton();
        final GLProfile glp = GLProfile.get(GLProfile.GL2);

        final GLCapabilities caps = new GLCapabilities(glp);
        caps.setOnscreen(false);
        caps.setHardwareAccelerated(true);
        caps.setFBO(true);

        GLDrawableFactory factory = GLDrawableFactory.getFactory(caps.getGLProfile());
        glDrawBuffer = factory.createOffscreenAutoDrawable(null, caps, null,
                width, height);
        glDrawBuffer.setAutoSwapBufferMode(true);

        glEvent = new GLEventImpl(width, height, this.getGraphicsContext2D().getPixelWriter(), camera);
        glDrawBuffer.addGLEventListener(glEvent);
        repaint();

    }

    public void repaint() {
        if (glDrawBuffer != null) {
            glDrawBuffer.display();
        }
    }

    public void reshapeAndRepaint(int width, int height) {
        System.out.println("wid:" + width + "hei" + height);
        this.setWidth(width);
        this.setHeight(height);
        glEvent.setBounds(width, height);
        glDrawBuffer.setSurfaceSize(width, height);
        repaint();
    }
}
