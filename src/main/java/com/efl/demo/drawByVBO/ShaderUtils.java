package com.efl.demo.drawByVBO;

import com.jogamp.opengl.GL2;

/**
 * @author: EFL-ryl
 */
public class ShaderUtils {
    static int createShaderProgram(GL2 gl, String vertexShaderFilePath, String fragmentShaderFilePath) {

        String[] vShaderSource = FileUtils.readShaderSource(vertexShaderFilePath);
        String[] fShaderSource = FileUtils.readShaderSource(fragmentShaderFilePath);

        int vShaderObj = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
        gl.glShaderSource(vShaderObj, vShaderSource.length, vShaderSource, null, 0);
        gl.glCompileShader(vShaderObj);

        ErrorChecker.getCompilationErrors(gl, vShaderObj, "vertex");

        int fShaderObj = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
        gl.glShaderSource(fShaderObj, fShaderSource.length, fShaderSource, null, 0);
        gl.glCompileShader(fShaderObj);

        ErrorChecker.getCompilationErrors(gl, fShaderObj, "fragment");

        int shaderProgram = gl.glCreateProgram();
        gl.glAttachShader(shaderProgram, vShaderObj);
        gl.glAttachShader(shaderProgram, fShaderObj);
        gl.glLinkProgram(shaderProgram);

        ErrorChecker.getLinkingErrors(gl, shaderProgram);

        //在把着色器对象链接到程序对象以后，可以删除着色器对象，因为不再需要它们了
        gl.glDeleteShader(vShaderObj);
        gl.glDeleteShader(fShaderObj);

        return shaderProgram;
    }
}
