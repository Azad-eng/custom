package com.efl.demo.triangle.render;

import com.efl.demo.triangle.entity.Entity;
import com.jogamp.opengl.GL2;
import org.joml.Matrix4f;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2GL3.GL_FILL;
import static com.jogamp.opengl.GL2GL3.GL_LINE;

public class Renderer {
    public static final int WIREFRAME = 0;

    private static boolean[] hints = new boolean[1];

    public static void clear(GL2 gl, float r, float g, float b) {
        gl.glEnable(GL_DEPTH_TEST);
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glClearColor(r, g, b, 1);
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void setRenderHints(int hint, boolean val) {
        hints[hint] = val;
    }

    public static void render(GL2 gl, Entity entity, Matrix4f camera, Matrix4f projection) {
        if (hints[WIREFRAME]) {
            gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        } else {
            gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }

        entity.getModel().getShaderProgram().start();

        entity.getModel().getShaderProgram().uploadMatrix4f("transform", entity.getTransform().getMatrix());
        entity.getModel().getShaderProgram().uploadMatrix4f("view", camera);
        entity.getModel().getShaderProgram().uploadMatrix4f("projection", projection);

        gl.glBindVertexArray(entity.getModel().getVaoID());
        gl.glEnableVertexAttribArray(0);
        gl.glEnableVertexAttribArray(1);
        gl.glEnableVertexAttribArray(2);
        gl.glEnableVertexAttribArray(3);
        gl.glDrawArrays(GL_TRIANGLES, 0, entity.getModel().getVertexCount());
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);
        gl.glDisableVertexAttribArray(2);
        gl.glDisableVertexAttribArray(3);
        gl.glBindVertexArray(0);

        entity.getModel().getShaderProgram().stop();
    }
}
