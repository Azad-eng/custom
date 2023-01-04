package com.efl.demo.jogl.rect;

import com.efl.demo.jogl.ShaderUtils;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.GLU;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL.*;

/**
 * @author: EFL-ryl
 */
public class GLEventImpl extends Canvas implements GLEventListener {
    //着色器程序对象(Shader Program Object)
    private int shaderProgram, lightProgram;
    private int VBO[] = new int[1];
    private int VAO[] = new int[1];
    private int lightVAO[] = new int[1];
    private int screenWidth = 1920;
    private int screenHeight = 1080;
    public static float scale = 1.0f, maxscale = 20.0f, minscale = 0.2f;
    public static float[] rotate = {-45.0f, 45.0f};
    public static float[] translate = {-24f, -14f, -30f};
    public static float[] lightPos = {0.0f, 50f, -100f};
    public static float[][] cubePositions = {{0.0f,  0.0f,  0.0f}};
    public static boolean ortho = false;
    private GLU glu = new GLU();
    private static final PixelFormat<ByteBuffer> pxFormat = PixelFormat.getByteBgraInstance();
    float[] vertices = {
            //坐标                //法线向量
            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,

            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
            0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
            -0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,

            -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,

            0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
            0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
            0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
            0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
            0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,

            -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
            0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,

            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
            -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f
    };
    //int[] indices = {
    //        0, 1, 3, // 第一个三角形
    //        1, 2, 3  // 第二个三角形
    //};
    private FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(vertices);
    //private IntBuffer indiBuf = Buffers.newDirectIntBuffer(indices);
    private PixelWriter pxWriter;
    private ByteBuffer imageBufferRGB8;
    private Camera camera;

    public GLEventImpl(int screenWidth, int screenHeight, PixelWriter pxWriter, Camera camera) {
        setBounds(screenWidth, screenHeight);
        setPxWriter(pxWriter);
        this.camera = camera;
    }

    public void setPxWriter(PixelWriter pxWriter) {
        this.pxWriter = pxWriter;
    }

    public void setBounds(int width, int height) {
        if (width > 0 && height > 0) {
            this.screenWidth = width;
            this.screenHeight = height;
            imageBufferRGB8 = Buffers.newDirectByteBuffer(4 * width * height);
        }
    }


    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //生成EBO对象
        //gl.glGenBuffers(this.EBO.length, this.EBO, 0);
        //与VBO类似，我们先绑定EBO然后用glBufferData把索引复制到缓冲里。同样，和VBO类似，
        //我们会把这些函数调用放在绑定和解绑函数调用之间，只不过这次我们把缓冲的类型定义为GL_ELEMENT_ARRAY_BUFFER
        //gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO[0]);
        //gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indiBuf.limit() * 4L, indiBuf, GL_STATIC_DRAW);

        //生成VBO对象
        gl.glGenBuffers(VBO.length, VBO, 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, VBO[0]);
        gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit() * 4L, vertBuf, GL_STATIC_DRAW);

        //现在我们已经把顶点数据储存在显卡的内存中，用VBO这个顶点缓冲对象管理。下面会创建一个顶点和片段着色器来真正处理这些数据:
        shaderProgram = ShaderUtils.createShaderProgram(gl,
                "E:\\Aazd-Home\\myStudySpace\\javaxFxLearn\\custom\\src\\main\\resources\\shaders\\3\\elementVS.glsl",
                "E:\\Aazd-Home\\myStudySpace\\javaxFxLearn\\custom\\src\\main\\resources\\shaders\\3\\elementFS.glsl");
        lightProgram = ShaderUtils.createShaderProgram(gl,
                "E:\\Aazd-Home\\myStudySpace\\javaxFxLearn\\custom\\src\\main\\resources\\shaders\\3\\lightVS.glsl",
                "E:\\Aazd-Home\\myStudySpace\\javaxFxLearn\\custom\\src\\main\\resources\\shaders\\3\\lightFS.glsl");

        //Get a id number to the uniform_Projection matrix
        //so that we can update it.

        //现在，我们已经把输入顶点数据发送给了GPU，并指示了GPU如何在顶点和片段着色器中处理它。就快要完成了，但还没结束，
        //OpenGL还不知道它该如何解释内存中的顶点数据，以及它该如何将顶点数据链接到顶点着色器的属性上。我们需要告诉OpenGL怎么做:
        //gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 3 * 4, 0);
        //现在我们已经定义了OpenGL该如何解释顶点数据，我们现在应该使用glEnableVertexAttribArray，以顶点属性位置值作为参数，启用顶点属性；顶点属性默认是禁用的。
        //自此，所有东西都已经设置好了：我们使用一个顶点缓冲对象将顶点数据初始化至缓冲中，建立了一个顶点和一个片段着色器，并告诉了OpenGL如何把顶点数据链接到顶点着色器的顶点属性上
        //gl.glEnableVertexAttribArray(0);
        /**
         * glVertexAttribPointer函数的参数非常多：
         *
         * 第一个参数指定我们要配置的顶点属性。还记得我们在顶点着色器中使用layout(location = 0)定义了position顶点属性的位置值(Location)吗？它可以把顶点属性的位置值设置为0。因为我们希望把数据传递到这一个顶点属性中，所以这里我们传入0。
         * 第二个参数指定顶点属性的大小。顶点属性是一个vec3，它由3个值组成，所以大小是3。
         * 第三个参数指定数据的类型，这里是GL_FLOAT(GLSL中vec*都是由浮点数值组成的)。
         * 下个参数定义我们是否希望数据被标准化(Normalize)。如果我们设置为GL_TRUE，所有数据都会被映射到0（对于有符号型signed数据是-1）到1之间。我们把它设置为GL_FALSE。
         * 第五个参数叫做步长(Stride)，它告诉我们在连续的顶点属性组之间的间隔。由于下个组位置数据在3个GLfloat之后，我们把步长设置为3 * sizeof(GLfloat)。要注意的是由于我们知道这个数组是紧密排列的（在两个顶点属性之间没有空隙）我们也可以设置为0来让OpenGL决定具体步长是多少（只有当数值是紧密排列时才可用）。一旦我们有更多的顶点属性，我们就必须更小心地定义每个顶点属性之间的间隔，我们在后面会看到更多的例子(译注: 这个参数的意思简单说就是从这个属性第二次出现的地方到整个数组0位置之间有多少字节)。
         * 最后一个参数的类型是GLvoid*，所以需要我们进行这个奇怪的强制类型转换。它表示位置数据在缓冲中起始位置的偏移量(Offset)。由于位置数据在数组的开头，所以这里是0。我们会在后面详细解释这个参数。
         */
        //【每个顶点属性从一个VBO管理的内存中获得它的数据，而具体是从哪个VBO（程序中可以有多个VBO）获取则是通过在调用glVetexAttribPointer时
        // 绑定到GL_ARRAY_BUFFER的VBO决定的。由于在调用glVetexAttribPointer之前绑定的是先前定义的VBO对象，顶点属性0现在会链接到它的顶点数据。】

        //创建VAO对象
        gl.glGenVertexArrays(VAO.length, VAO, 0);
        //要想使用VAO，要做的只是使用glBindVertexArray绑定VAO。从绑定之后起，我们应该绑定和配置对应的VBO和属性指针，
        //之后解绑VAO供之后使用。当我们打算绘制一个物体的时候，我们只要在绘制物体前简单地把VAO绑定到希望使用的设定上就行了
        // ..:: 初始化代码（只运行一次 (除非你的物体频繁改变)） :: ..
        // 1. 绑定VAO
        gl.glBindVertexArray(VAO[0]);
        // 2. 把顶点数组复制到缓冲中供OpenGL使用
        gl.glBindBuffer(GL_ARRAY_BUFFER, VBO[0]);
        gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit() * 4L, vertBuf, GL_STATIC_DRAW);
        // 3.1 复制我们的索引数组到一个索引缓冲中，供OpenGL使用
        //gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO[0]);
        //gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indiBuf.limit() * 4L, indiBuf, GL_STATIC_DRAW);
        // 3.2 设置顶点属性指针
        //位置属性
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * 4, 0);
        gl.glEnableVertexAttribArray(0);
        //法向量属性
        gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * 4, 3 * 4);
        gl.glEnableVertexAttribArray(1);
        //4. 解绑VAO
        gl.glBindVertexArray(0);
        //【通常情况下当我们配置好OpenGL对象以后要解绑它们，这样我们才不会在其它地方错误地配置它们】

        //创建lightVAO对象
        gl.glGenVertexArrays(lightVAO.length, lightVAO, 0);
        gl.glBindVertexArray(lightVAO[0]);
        // 只需要绑定VBO不用再次设置VBO的数据，因为容器(物体)的VBO数据中已经包含了正确的立方体顶点数据
        gl.glBindBuffer(GL_ARRAY_BUFFER, VBO[0]);
        // 设置灯立方体的顶点属性指针(仅设置灯的顶点数据)
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * 4, 0);
        gl.glEnableVertexAttribArray(0);
        gl.glBindVertexArray(0);
        //既然我们已经创建了表示灯和被照物体的立方体，我们只需要再定义一个东西就行了了，那就是片段着色器:这个片段着色器接受两个分别
        //表示物体颜色和光源颜色的uniform变量,然后将光源的颜色与物体(能反射)的颜色相乘。接下来让我们把物体的颜色设置为珊瑚红并把光源设置为白色：

        //开启混合功能
        //gl.glEnable(GL2.GL_BLEND);
        //透明计算函数设置
        //gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        //gl.glBlendEquation(GL.GL_FUNC_ADD);
        //开启对线\多边形的抗锯齿功能
        //gl.glEnable(GL.GL_LINE_SMOOTH);
        //gl.glEnable(GL2.GL_POLYGON_SMOOTH);
        gl.glClearColor(0.9f, 0.9f, 0.9f, 1.0f);
        //深度测试，避免后画的始终显示在先画的上面
        gl.glEnable(GL2.GL_DEPTH_TEST);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        //doMovement();
        // Update variables used in animation
        final GL2 gl = drawable.getGL().getGL2();
        // 渲染
        // 清空颜色缓冲
        //gl.glClearDepth(1.0);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        // Get location objects for the matrices on the lamp shader (these could be different on a different shader)
        int modelLoc = gl.glGetUniformLocation(lightProgram, "model");
        int viewLoc  = gl.glGetUniformLocation(lightProgram, "view");
        int projLoc  = gl.glGetUniformLocation(lightProgram, "projection");
        FloatBuffer lmb = Buffers.newDirectFloatBuffer(16);
        FloatBuffer mb = Buffers.newDirectFloatBuffer(16);
        FloatBuffer vb = Buffers.newDirectFloatBuffer(16);
        FloatBuffer pb = Buffers.newDirectFloatBuffer(16);

        /**
         * 观察矩阵
         * 2.world coordinates ——[view matrix]——>> camera coordinates
         */
        camera.setLocation(new Vector3f(0,0,0));
        camera.getViewMatrix().get(vb);

        /**
         * 投影矩阵
         * 3.camera coordinates ——[projection matrix]——>> homogeneous coordinates
         *
         * clip = projection * view * model * local （注意：在着色器中 顺序不能变，需要从右往左乘上每个矩阵，因为
         * 每个矩阵被运算的顺序是相反。最后的顶点应该被赋予顶点着色器中的gl_Position且OpenGL将会自动进行透视划分和裁剪）
         */
        camera.getProjectionMatrix().get(pb);

        // 记得激活着色器
        gl.glUseProgram(lightProgram);
        gl.glUniformMatrix4fv(viewLoc, 1, false, vb);
        gl.glUniformMatrix4fv(projLoc, 1,false, pb);
        new Matrix4f().scale(1f).get(lmb);
        new Matrix4f().translate( lightPos[0], lightPos[1], lightPos[2]).get(lmb);
        gl.glUniformMatrix4fv(modelLoc, 1, false, lmb);
        // Draw the light object (using light's vertex attributes)
        gl.glBindVertexArray(lightVAO[0]);
        gl.glDrawArrays(GL_TRIANGLES, 0, 36);
        gl.glBindVertexArray(0);

        gl.glUseProgram(shaderProgram);
        // 在此之前不要忘记首先'使用'对应的着色器程序(来设定uniform)
        int objectColorLoc = gl.glGetUniformLocation(shaderProgram, "objectColor");
        int lightColorLoc  = gl.glGetUniformLocation(shaderProgram, "lightColor");
        int lightPosLoc = gl.glGetUniformLocation(shaderProgram, "lightPos");
        gl.glUniform3f(objectColorLoc, 1.0f, 0.5f, 0.31f);// 珊瑚红
        gl.glUniform3f(lightColorLoc, 1.0f, 1.0f, 1.0f); // 依旧把光源设置为白色
        gl.glUniform3f(lightPosLoc, lightPos[0], lightPos[1], lightPos[2]); //

        // Get their uniform location
        modelLoc = gl.glGetUniformLocation(shaderProgram, "model");
        viewLoc = gl.glGetUniformLocation(shaderProgram, "view");
        projLoc = gl.glGetUniformLocation(shaderProgram, "projection");
        // Pass the matrices to the shader
        gl.glUniformMatrix4fv(viewLoc, 1, false, vb);
        gl.glUniformMatrix4fv(projLoc  , 1, false, pb);

        // 绘制三角形
        //绑定VAO
        gl.glBindVertexArray(VAO[0]);
        //要注意的是，我们传递了GL_ELEMENT_ARRAY_BUFFER当作缓冲目标。最后一件要做的事是用glDrawElements来替换glDrawArrays函数，
        //来指明我们从索引缓冲渲染。使用glDrawElements时，我们会使用当前绑定的索引缓冲对象中的索引进行绘制：
        //gl.glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        //// 12*3 indices starting at 0 -> 12 triangles -> 6 squares
        for (int i = 0; i < cubePositions.length; i++) {
            /**
             * 模型矩阵
             * 1.model coordinates ——[model matrix]——>> world coordinates
             * 通过将顶点坐标乘以下面的模型矩阵我们将该顶点坐标转换到世界坐标
             */
            Matrix4f model = new Matrix4f();
            model.scale(GLEventImpl.scale)
                 .translate(cubePositions[i][0], cubePositions[i][1], cubePositions[i][2])
                 //.rotate(20.0f * i, 1.0f, 0.3f, 0.5f)
                 .get(mb);
            gl.glUniformMatrix4fv(modelLoc, 1, false, mb);
            gl.glDrawArrays(GL2.GL_TRIANGLES, 0, 12 * 3);
        }
        //解绑VAO
        gl.glBindVertexArray(0);
        drawPlatform(gl);

        gl.glReadBuffer(GL.GL_FRONT);
        gl.glReadPixels(0, 0, screenWidth, screenHeight, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, imageBufferRGB8);
        pxWriter.setPixels(0, 0, screenWidth, screenHeight, pxFormat, imageBufferRGB8, screenWidth * 4);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }

    //public static void scrollCallback(double yoffset) {
    //    camera.ProcessMouseScroll((float)yoffset);
    //}

    public static void resetTranslate() {
        translate[0] = -24;
        translate[1] = -14;
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

    public static void drawPlatform(GL2 gl) {
        drawColorAxis(gl);
        //drawFloor(gl);
        //drawOrigin(gl);
    }

    private static void drawColorAxis(GL2 gl) {
        //范围框
        gl.glLineWidth((float) (4.0 * GLEventImpl.scale));

        gl.glBegin(GL2.GL_LINES);

        //x-red
        gl.glColor3d(1, 0, 0.2);
        gl.glVertex3d(0, 0, 0);
        gl.glVertex3d(48, 0, 0);
        //y-green
        //gl.glBegin(GL2.GL_LINES);
        gl.glColor3d(0, 0.5, 0);
        gl.glVertex3d(0, 0, 0);
        gl.glVertex3d(0, 27, 0);
        //z-blue
        //gl.glBegin(GL2.GL_LINES);
        gl.glColor3d(0, 0.0, 1);
        gl.glVertex3d(0, 0, 0);
        gl.glVertex3d(0, 0, 60);

        gl.glEnd();
    }
}
