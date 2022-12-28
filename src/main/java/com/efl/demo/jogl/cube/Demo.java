package com.efl.demo.jogl.cube;

import com.efl.demo.jogl.ShaderUtils;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import org.joml.Matrix4f;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.jogamp.opengl.GL.*;

/**
 * @author: EFL-ryl
 */
public class Demo extends JFrame implements GLEventListener {
    private GLJPanel glJPanel;
    //着色器程序对象(Shader Program Object)
    private int shaderProgram;
    //顶点缓冲对象是我们在OpenGL教程中第一个出现的OpenGL对象。就像OpenGL中的其它对象一样，这个缓冲有一个独一无二的ID，
    //所以我们可以使用glGenBuffers函数和一个缓冲ID生成一个VBO对象
    private int VBO[] = new int[1];
    //顶点数组对象(Vertex Array Object, VAO)可以像顶点缓冲对象那样被绑定，任何随后的顶点属性调用都会储存在这个VAO中。这样的好处就是，
    //当配置顶点属性指针时，你只需要将那些调用执行一次，之后再绘制物体的时候只需要绑定相应的VAO就行了。这使在不同顶点数据和属性配置之间
    //切换变得非常简单，只需要绑定不同的VAO就行了。
    private int VAO[] = new int[1];
    private int EBO[] = new int[1];
    float[] vertices = {
              //坐标                  贴图
            -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
            0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
            0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
            0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
            0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
            0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
            -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

            -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
            -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
            -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

            0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
            0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
            0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
            0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
            0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
            0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
            0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
            0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
            0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
            -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
            -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
    };
    int[] indices = {
            0, 1, 3, // 第一个三角形
            1, 2, 3  // 第二个三角形
    };
    private FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(vertices);
    private IntBuffer indiBuf = Buffers.newDirectIntBuffer(indices);
    //加载并创建贴图
    int[] texture1 = new int[1];
    int[] texture2 = new int[1];

    Demo() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("JOGL demo");
        this.setSize(800, 600);
        this.setLocation(270, 65);
        glJPanel = new GLJPanel();
        glJPanel.addGLEventListener(this);
        this.add(glJPanel);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new Demo();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0,0, 800, 600);
        gl.glEnable(GL_DEPTH_TEST);
        //GL3 gl = (GL3) drawable.getGL();
        //生成EBO对象
        //gl.glGenBuffers(EBO.length, EBO, 0);
        //与VBO类似，我们先绑定EBO然后用glBufferData把索引复制到缓冲里。同样，和VBO类似，
        //我们会把这些函数调用放在绑定和解绑函数调用之间，只不过这次我们把缓冲的类型定义为GL_ELEMENT_ARRAY_BUFFER
        //gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO[0]);
        //gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indiBuf.limit() * 4L, indiBuf, GL_STATIC_DRAW);

        //生成VBO对象
        gl.glGenBuffers(VBO.length, VBO, 0);
        //OpenGL有很多缓冲对象类型，顶点缓冲对象的缓冲类型是GL_ARRAY_BUFFER。OpenGL允许我们同时绑定多个缓冲，只要它们是不同的缓冲类型。
        //我们可以使用glBindBuffer函数把新创建的缓冲绑定到GL_ARRAY_BUFFER目标上：
        gl.glBindBuffer(GL_ARRAY_BUFFER, VBO[0]);
        //从这一刻起，我们使用的任何（在GL_ARRAY_BUFFER目标上的）缓冲调用都会用来配置当前绑定的缓冲(VBO)。
        //然后我们可以调用glBufferData函数，它会把之前定义的顶点数据复制到缓冲的内存中：
        gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit() * 4L, vertBuf, GL_STATIC_DRAW);
        /**
         * glBufferData是一个专门用来把用户定义的数据复制到当前绑定缓冲的函数。它的第一个参数是目标缓冲的类型：
         * 顶点缓冲对象当前绑定到GL_ARRAY_BUFFER目标上。第二个参数指定传输数据的大小(以字节为单位)；用一个简单的sizeof
         * 计算出顶点数据大小就行。第三个参数是我们希望发送的实际数据。
         *
         * 第四个参数指定了我们希望显卡如何管理给定的数据。它有三种形式：
         *
         * GL_STATIC_DRAW ：数据不会或几乎不会改变
         * GL_DYNAMIC_DRAW：数据会被改变很多
         * GL_STREAM_DRAW ：数据每次绘制时都会改变
         */

        //现在我们已经把顶点数据储存在显卡的内存中，用VBO这个顶点缓冲对象管理。下面会创建一个顶点和片段着色器来真正处理这些数据:
        shaderProgram = ShaderUtils.createShaderProgram(gl,
                "E:\\Aazd-Home\\myStudySpace\\javaxFxLearn\\custom\\src\\main\\resources\\shaders\\4\\elementVS.glsl",
                "E:\\Aazd-Home\\myStudySpace\\javaxFxLearn\\custom\\src\\main\\resources\\shaders\\4\\elementFS.glsl");
        //Get a id number to the uniform_Projection matrix
        //so that we can update it.
        //ModelViewProjectionMatrix_location = gl.glGetUniformLocation(shaderProgram, "uniform_Projection");

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
        //gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO[0]);
        //gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indiBuf.limit() * 4L, indiBuf, GL_STATIC_DRAW);
        // 3. 设置顶点属性指针
        /**
         * 由于我们现在有了两个顶点属性，我们不得不重新计算步长值。为获得数据队列中下一个属性值（比如位置向量的下个x分量）我们
         * 必须向右移动6个float，其中3个是位置值，另外3个是颜色值。这使我们的步长值为6乘以float的字节数（=24字节）。
         * 同样，这次我们必须指定一个偏移量。对于每个顶点来说，位置顶点属性在前，所以它的偏移量是0。颜色属性紧随位置数据之后，所以
         * 偏移量就是3 * sizeof(GLfloat)，用字节来计算就是12字节
         */
        //位置属性
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 5 * 4, 0);
        gl.glEnableVertexAttribArray(0);
        //贴图属性
        gl.glVertexAttribPointer(2, 2, GL.GL_FLOAT, false, 5 * 4, 3 * 4);
        gl.glEnableVertexAttribArray(2);

        //4. 解绑VAO
        gl.glBindVertexArray(0);
        //【通常情况下当我们配置好OpenGL对象以后要解绑它们，这样我们才不会在其它地方错误地配置它们】
        // ====================
        // Texture 1
        // ====================
        gl.glGenTextures(texture1.length, texture1, 0);
        gl.glBindTexture(GL_TEXTURE_2D, texture1[0]);
        // All upcoming GL_TEXTURE_2D operations now have effect on our texture object
        // Set our texture parameters
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);	// Set texture wrapping to GL_REPEAT
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        // Set texture filtering
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // Load, create texture and generate mipmaps
        try {
            BufferedImage image = ImageIO.read(getClass().getClassLoader().getResource("image/GRID.png"));
            DataBufferByte dbb = (DataBufferByte)image.getRaster().getDataBuffer();
            byte[] data = dbb.getData();
            ByteBuffer pixels = Buffers.newDirectByteBuffer(data.length);
            pixels.put(data);
            pixels.flip();
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, 800, 600, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, pixels);
            gl.glGenerateMipmap(GL_TEXTURE_2D);
            gl.glBindTexture(GL_TEXTURE_2D, 0); // Unbind texture when done, so we won't accidentily mess up our texture.
        } catch(Throwable t) {
            t.printStackTrace();
        }

        // ===================
        // Texture 2
        // ===================
        gl.glGenTextures(texture2.length, texture2, 0);
        gl.glBindTexture(GL_TEXTURE_2D, texture2[0]);
        // Set our texture parameters
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        // Set texture filtering
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // Load, create texture and generate mipmaps
        try {
            BufferedImage image = ImageIO.read(getClass().getClassLoader().getResource("image/black.png"));
            DataBufferByte dbb = (DataBufferByte)image.getRaster().getDataBuffer();
            byte[] data = dbb.getData();
            ByteBuffer pixels = Buffers.newDirectByteBuffer(data.length);
            pixels.put(data);
            pixels.flip();
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, 800, 600, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, pixels);
            gl.glGenerateMipmap(GL_TEXTURE_2D);
            gl.glBindTexture(GL_TEXTURE_2D, 0); // Unbind texture when done, so we won't accidentily mess up our texture.
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        // Update variables used in animation
        GL2 gl = drawable.getGL().getGL2();
        // 渲染
        // 背景颜色
        gl.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        // 清空颜色缓冲
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Bind Textures using texture units
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, texture1[0]);
        gl.glUniform1i(gl.glGetUniformLocation(shaderProgram, "ourTexture1"), 0);
        gl.glActiveTexture(GL_TEXTURE1);
        gl.glBindTexture(GL_TEXTURE_2D, texture2[0]);
        gl.glUniform1i(gl.glGetUniformLocation(shaderProgram, "ourTexture2"), 1);


        //调用glUseProgram函数，用刚创建的程序对象作为它的参数，以激活这个程序对象。
        //在glUseProgram函数调用之后，每个着色器调用和渲染调用都会使用这个程序对象（也就是之前写的着色器)了
        gl.glUseProgram(shaderProgram);

        // Get their uniform location
        int mat4ModLoc = gl.glGetUniformLocation(shaderProgram, "model");
        int mat4ViewLoc = gl.glGetUniformLocation(shaderProgram, "view");
        int mat4ProLoc = gl.glGetUniformLocation(shaderProgram, "projection");


        FloatBuffer mb = Buffers.newDirectFloatBuffer(16);
        FloatBuffer vb = Buffers.newDirectFloatBuffer(16);
        FloatBuffer pb = Buffers.newDirectFloatBuffer(16);
        //Matrix4f matrix4f = new Matrix4f();
        new Matrix4f().rotate(50.0f, 0.5f, 1.0f, 0.0f/*, matrix4f*/).get(mb);
        new Matrix4f().translate(0.0f, 0.2f, -1.0f/*, matrix4f*/).get(vb);
        new Matrix4f().perspective((float) Math.toRadians(45.0f), (float)1, 0.1f, 100.0f/*, matrix4f*/)
                .lookAt(0.0f, 0.0f, 3.0f,
                        0.0f, 0.0f, 0.0f,
                        0.0f, -1.0f, 0.0f).get(pb);

        gl.glUniformMatrix4fv(mat4ModLoc, 1, false, mb);
        gl.glUniformMatrix4fv(mat4ViewLoc, 1, false, vb);
        gl.glUniformMatrix4fv(mat4ProLoc, 1, false, pb);


        //绑定VAO
        gl.glBindVertexArray(VAO[0]);
        //OpenGL给我们提供了glDrawArrays函数，它使用当前激活的着色器，之前定义的顶点属性配置，和VBO的顶点数据（通过VAO间接绑定）来绘制图元
        gl.glDrawArrays(GL2.GL_TRIANGLES, 0, 36);
        //gl.glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        //解绑VAO
        gl.glBindVertexArray(0);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }
}
