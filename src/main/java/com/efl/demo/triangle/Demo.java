package com.efl.demo.triangle;

import com.efl.demo.triangle.entity.Entity;
import com.efl.demo.triangle.entity.Transform;
import com.efl.demo.triangle.mesh.Model;
import com.efl.demo.triangle.mesh.ModelLoader;
import com.efl.demo.triangle.render.FirstPersonCamera;
import com.efl.demo.triangle.render.Projection;
import com.efl.demo.triangle.render.Renderer;
import com.efl.demo.triangle.shader.Shader;
import com.efl.demo.triangle.shader.ShaderLoader;
import com.efl.demo.triangle.shader.ShaderProgram;
import com.efl.demo.triangle.shader.ShaderProgramLoader;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;

import javax.swing.*;

/**
 * @author: EFL-ryl
 */
public class Demo extends JFrame implements GLEventListener {
    private GLJPanel glJPanel;
    private FirstPersonCamera fpCam;
    private Entity house;
    private ShaderProgram program;
    private int width = 1200;
    private int height = 900;
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
        //获取 OpenGL 2 图形上下文
        GL2 gl = drawable.getGL().getGL2();
        Shader vs = ShaderLoader.loadShader(gl, "res/Vertex Shader.glsl", ShaderLoader.VERTEX);
        Shader fs = ShaderLoader.loadShader(gl, "res/Fragment Shader.glsl", ShaderLoader.FRAGMENT);
        program = ShaderProgramLoader.loadShaderProgram(gl, vs, fs);

        Model mod = ModelLoader.loadModel(gl, "res/models/House.dae", program);
        house = new Entity(mod, new Transform(0, 0, 0, -90, 0, 0, 1, 1, 1));

        fpCam = new FirstPersonCamera(0, 0, 5);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        Renderer.clear(gl, 0.5f, 0.5f, 0.5f);
        if (house != null){
            house.render(gl, fpCam.getMatrix(), Projection.getPerspectiveMatrix(55, width, height));
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }
}
