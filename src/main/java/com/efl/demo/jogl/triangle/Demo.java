package com.efl.demo.jogl.triangle;

import com.efl.demo.jogl.ShaderUtils;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import javax.swing.*;
import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;

/**
 * @author: EFL-ryl
 */
public class Demo extends JFrame implements GLEventListener {
    private GLJPanel glJPanel;
    //着色器程序对象(Shader Program Object)
    private int renderingProgram;
    //顶点缓冲对象是我们在OpenGL教程中第一个出现的OpenGL对象。就像OpenGL中的其它对象一样，这个缓冲有一个独一无二的ID，
    //所以我们可以使用glGenBuffers函数和一个缓冲ID生成一个VBO对象
    private int VBO[] = new int[1];
    //顶点数组对象(Vertex Array Object, VAO)可以像顶点缓冲对象那样被绑定，任何随后的顶点属性调用都会储存在这个VAO中。这样的好处就是，
    //当配置顶点属性指针时，你只需要将那些调用执行一次，之后再绘制物体的时候只需要绑定相应的VAO就行了。这使在不同顶点数据和属性配置之间
    //切换变得非常简单，只需要绑定不同的VAO就行了。
    private int VAO[] = new int[1];
    //三角形：由于OpenGL是在3D空间中工作的，而我们渲染的是一个2D三角形，我们将它顶点的z坐标设置为0.0。
    //       这样子的话三角形每一点的深度(Depth)都是一样的，从而使它看上去像是2D的
    float[] vertices = {
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.0f, 0.5f, 0.0f
    };
    private FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(vertices);


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
        //GL3 gl = (GL3) drawable.getGL();
        //生成VBO对象
        gl.glGenBuffers(this.VBO.length, this.VBO, 0);
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
        renderingProgram = ShaderUtils.createShaderProgram(gl,
                "E:\\Aazd-Home\\myStudySpace\\javaxFxLearn\\custom\\src\\main\\resources\\shaders\\elementFS.glsl",
                "E:\\Aazd-Home\\myStudySpace\\javaxFxLearn\\custom\\src\\main\\resources\\shaders\\elementVS.glsl");

        //现在，我们已经把输入顶点数据发送给了GPU，并指示了GPU如何在顶点和片段着色器中处理它。就快要完成了，但还没结束，
        //OpenGL还不知道它该如何解释内存中的顶点数据，以及它该如何将顶点数据链接到顶点着色器的属性上。我们需要告诉OpenGL怎么做:
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 3 * 4, 0);
        //现在我们已经定义了OpenGL该如何解释顶点数据，我们现在应该使用glEnableVertexAttribArray，以顶点属性位置值作为参数，启用顶点属性；顶点属性默认是禁用的。
        //自此，所有东西都已经设置好了：我们使用一个顶点缓冲对象将顶点数据初始化至缓冲中，建立了一个顶点和一个片段着色器，并告诉了OpenGL如何把顶点数据链接到顶点着色器的顶点属性上
        gl.glEnableVertexAttribArray(0);
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
        gl.glGenVertexArrays(this.VAO.length, this.VAO, 0);
        //要想使用VAO，要做的只是使用glBindVertexArray绑定VAO。从绑定之后起，我们应该绑定和配置对应的VBO和属性指针，
        //之后解绑VAO供之后使用。当我们打算绘制一个物体的时候，我们只要在绘制物体前简单地把VAO绑定到希望使用的设定上就行了
        // ..:: 初始化代码（只运行一次 (除非你的物体频繁改变)） :: ..
        // 1. 绑定VAO
        gl.glBindVertexArray(this.VAO[0]);
        // 2. 把顶点数组复制到缓冲中供OpenGL使用
        gl.glBindBuffer(GL_ARRAY_BUFFER, VBO[0]);
        gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit() * 4L, vertBuf, GL_STATIC_DRAW);
        // 3. 设置顶点属性指针
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
        //OpenGL给我们提供了glDrawArrays函数，它使用当前激活的着色器，之前定义的顶点属性配置，和VBO的顶点数据（通过VAO间接绑定）来绘制图元
        gl.glDrawArrays(GL2.GL_TRIANGLES, 0, 3);
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
