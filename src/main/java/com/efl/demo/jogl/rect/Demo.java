package com.efl.demo.jogl.rect;

import com.efl.demo.jogl.ShaderUtils;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;

import javax.swing.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2GL3.GL_FILL;
import static com.jogamp.opengl.GL2GL3.GL_LINE;

/**
 * @author: EFL-ryl
 */
public class Demo extends JFrame implements GLEventListener {
    private GLJPanel glJPanel;
    //着色器程序对象(Shader Program Object)
    private int renderingProgram;
    private int VBO[] = new int[1];
    private int VAO[] = new int[1];
    //索引缓冲对象(Element Buffer Object，EBO，也叫Index Buffer Object，IBO)。和顶点缓冲对象VBO一样，EBO也是一个缓冲，
    //它专门储存索引，OpenGL调用这些顶点的索引来决定该绘制哪个顶点。
    private int EBO[] = new int[1];
    //2D矩形
    float[] vertices = {
            0.5f, 0.5f, 0.0f,   // 右上角
            0.5f, -0.5f, 0.0f,  // 右下角
            -0.5f, -0.5f, 0.0f, // 左下角
            -0.5f, 0.5f, 0.0f   // 左上角
    };
    int[] indices = {
            0, 1, 3, // 第一个三角形
            1, 2, 3  // 第二个三角形
    };
    private FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(vertices);
    private IntBuffer indiBuf = Buffers.newDirectIntBuffer(indices);


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
        //生成EBO对象
        gl.glGenBuffers(this.EBO.length, this.EBO, 0);
        //与VBO类似，我们先绑定EBO然后用glBufferData把索引复制到缓冲里。同样，和VBO类似，
        //我们会把这些函数调用放在绑定和解绑函数调用之间，只不过这次我们把缓冲的类型定义为GL_ELEMENT_ARRAY_BUFFER
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO[0]);
        gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indiBuf.limit() * 4L, indiBuf, GL_STATIC_DRAW);

        //生成VBO对象
        gl.glGenBuffers(this.VBO.length, this.VBO, 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, VBO[0]);
        gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit() * 4L, vertBuf, GL_STATIC_DRAW);

        //现在我们已经把顶点数据储存在显卡的内存中，用VBO这个顶点缓冲对象管理。下面会创建一个顶点和片段着色器来真正处理这些数据:
        renderingProgram = ShaderUtils.createShaderProgram(gl,
                "E:\\Aazd-Home\\myStudySpace\\javaxFxLearn\\custom\\src\\main\\resources\\shaders\\1\\elementVS.glsl",
                "E:\\Aazd-Home\\myStudySpace\\javaxFxLearn\\custom\\src\\main\\resources\\shaders\\1\\elementFS.glsl");

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
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO[0]);
        gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indiBuf.limit() * 4L, indiBuf, GL_STATIC_DRAW);
        // 3.2 设置顶点属性指针
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 3 * 4, 0);
        gl.glEnableVertexAttribArray(0);
        //4. 解绑VAO
        gl.glBindVertexArray(0);
        //【通常情况下当我们配置好OpenGL对象以后要解绑它们，这样我们才不会在其它地方错误地配置它们】
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //调用glUseProgram函数，用刚创建的程序对象作为它的参数，以激活这个程序对象。
        //在glUseProgram函数调用之后，每个着色器调用和渲染调用都会使用这个程序对象（也就是之前写的着色器)了
        gl.glUseProgram(renderingProgram);
        //绑定VAO
        gl.glBindVertexArray(VAO[0]);
        //要注意的是，我们传递了GL_ELEMENT_ARRAY_BUFFER当作缓冲目标。最后一件要做的事是用glDrawElements来替换glDrawArrays函数，
        //来指明我们从索引缓冲渲染。使用glDrawElements时，我们会使用当前绑定的索引缓冲对象中的索引进行绘制：
        gl.glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        //gl.glDrawArrays(GL2.GL_TRIANGLES, 0, 3);
        /**
         * 第一个参数指定了我们绘制的模式，这个和glDrawArrays的一样。第二个参数是我们打算绘制顶点的个数，这里填6，也就是说我们一共
         * 需要绘制6个顶点。第三个参数是索引的类型，这里是GL_UNSIGNED_INT。最后一个参数里我们可以指定EBO中的偏移量（或者传递一个索
         * 引数组，但是这是当你不在使用索引缓冲对象的时候），但是我们会在这里填写0。
         */
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
