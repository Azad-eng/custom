package com.efl.demo.drawByVBO;

/**
 * @author: EFL-ryl
 */
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.GLU;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;


public class GLEventImpl implements GLEventListener {
    private static final PixelFormat<ByteBuffer> pxFormat = PixelFormat.getByteBgraInstance();
    private PixelWriter pxWriter;
    private ByteBuffer imageBufferRGB8;
    private int width = 1200;
    private int height = 900;
    public static boolean ortho = false;

    public static double scale = 1.0, maxscale = 20.0, minscale = 0.2;
    public static double[] rotate = {-45.0, 45.0};
    public static double[] translate = {
            -24,
            -13.5,
            -30};
    GLU glu = new GLU();
    private int renderingProgram;
    private int VBO[] = new int[1];
    private int VAO[] = new int[1];
    float[] vertices = {
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.0f, 0.5f, 0.0f
    };
    private FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(vertices);

    public GLEventImpl(int width, int height, PixelWriter pxWriter) {
        setBounds(width, height);
        setPxWriter(pxWriter);
    }

    public static void resetTranslate() {
        translate[0] = -24;
        translate[1] = -13.5;
        translate[2] = -30;
    }

    public static void enlarge() {
        if (scale < maxscale) {
            scale *= 1.1;
        }
    }

    public static void small() {
        if (scale > minscale) {
            scale /= 1.1;
        }
    }

    public static void rotateZ(double degree) {
        rotate[1] += degree;
        while (rotate[1] >= 360) {
            rotate[1] -= 360;
        }
        while (rotate[1] < 0) {
            rotate[1] += 360;
        }
    }

    public static void rotateX(double degree) {
        rotate[0] += degree;
        while (rotate[0] >= 360) {
            rotate[0] -= 360;
        }
        while (rotate[0] < 0) {
            rotate[0] += 360;
        }
    }

    public void setPxWriter(PixelWriter pxWriter) {
        this.pxWriter = pxWriter;
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        final GL2 gl = glAutoDrawable.getGL().getGL2();
        float[] whiteLight = {0.45f, 0.45f, 0.45f, 1.0f};
        float[] sourceLight = {0.25f, 0.25f, 0.25f, 1.0f};
        float[] lightPos = {-50f, 25f, 250f, 0.0f};
        //颜色属性
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, whiteLight, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, sourceLight, 0);
        //漫反射光颜色：光线直接射到物体表面的颜色
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, sourceLight, 0);
        //位置属性
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);
        //启用照明模式
        gl.glEnable(GL2.GL_LIGHTING);
        //打开第一个灯光
        gl.glEnable(GL2.GL_LIGHT0);
        //启用材质的颜色跟踪
        gl.glEnable(GL2.GL_COLOR_MATERIAL);

        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glClearDepthf(10.0f);
        // 设置背景颜色
        gl.glClearColor(0.9f, 0.9f, 0.9f, 1.0f);
        //gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        gl.glGenBuffers(this.VBO.length, this.VBO, 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, VBO[0]);
        gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit() * 4L, vertBuf, GL_STATIC_DRAW);
        this.renderingProgram = ShaderUtils.createShaderProgram(gl,
                "E:\\Aazd-Home\\myStudySpace\\javaxFxLearn\\custom\\src\\main\\resources\\shaders\\elementFS.glsl",
                "E:\\Aazd-Home\\myStudySpace\\javaxFxLearn\\custom\\src\\main\\resources\\shaders\\elementVS.glsl");
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 3 * 4, 0);
        gl.glEnableVertexAttribArray(0);
        gl.glGenVertexArrays(this.VAO.length, this.VAO, 0);
        gl.glBindVertexArray(this.VAO[0]);
        gl.glBindBuffer(GL_ARRAY_BUFFER, VBO[0]);
        gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit() * 4L, vertBuf, GL_STATIC_DRAW);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 3 * 4, 0);
        gl.glEnableVertexAttribArray(0);
        gl.glBindVertexArray(0);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        long startTime = System.currentTimeMillis();
        final GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glClearDepth(1.0);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        //加载当前矩阵为单位矩阵
        gl.glLoadIdentity();
        //如果是正交视图，无需移动视角与模型的相对位置进行缩放，另有方法进行缩放
        if (ortho) {
            gl.glTranslated(0.0, 0.0, -350.0);
        }
        //如果是透视视图，依靠近大远小的效果改变视角与模型距离即可实现模型的缩放
        else {
            gl.glTranslated(0.0, 0.0, -350.0 / scale);
        }
        //绕X轴旋转
        gl.glRotated(rotate[0], 1.0, 0.0, 0.0);
        //绕Z轴旋转
        gl.glRotated(rotate[1], 0.0, 0.0, 1.0);
        gl.glTranslated(translate[0], translate[1], translate[2]);
        gl.glPopMatrix();
        System.out.print("1:" + (System.currentTimeMillis() - startTime));
        gl.glReadBuffer(GL.GL_FRONT);
        gl.glReadPixels(0, 0, width, height, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE,
                imageBufferRGB8);
        pxWriter.setPixels(0, 0, width, height,
                pxFormat, imageBufferRGB8, width * 4);
        gl.glUseProgram(renderingProgram);
        gl.glBindVertexArray(VAO[0]);
        gl.glDrawArrays(GL3.GL_TRIANGLES, 0, 3);
        gl.glBindVertexArray(0);
        System.out.println("   2:" + (System.currentTimeMillis() - startTime));
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i2, int i3, int i4) {
        glu.gluPerspective(80.0f, 1920.0f / 1080.0f, 0.1f, 100f);
    }

    public void setBounds(int width, int height) {
        if (width > 0 && height > 0) {
            this.width = width;
            this.height = height;
            imageBufferRGB8 = Buffers.newDirectByteBuffer(4 * width * height);
        }
    }
}