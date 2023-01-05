package com.efl.demo.triangle.shader;

import com.jogamp.opengl.GL2;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;


public class ShaderProgram {
    public int programID;
    public GL2 gl;

    public ShaderProgram(GL2 gl, int programID) {
        this.gl = gl;
        this.programID = programID;
    }

    public void start() {
        gl.glUseProgram(programID);
    }

    public void uploadMatrix4f(String varName, Matrix4f matrix) {
        int location = gl.glGetUniformLocation(programID, varName);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        matrix.get(buffer);
        gl.glUniformMatrix4fv(location, 1, false, buffer);
    }

    public void uploadTexture(String varName, int slot) {
        int location = gl.glGetUniformLocation(programID, varName);
        gl.glUniform1i(location, slot);
    }

    public void stop() {
        gl.glUseProgram(0);
    }
}
