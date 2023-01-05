package com.efl.demo.triangle.shader;

import com.jogamp.opengl.GL2;

import java.util.ArrayList;


public class ShaderProgramLoader {
    private static ArrayList<Integer> programs = new ArrayList<Integer>();
    private static ArrayList<Integer> vertexShaders = new ArrayList<Integer>();
    private static ArrayList<Integer> fragmentShaders = new ArrayList<Integer>();

    public static ShaderProgram loadShaderProgram(GL2 gl, Shader vs, Shader fs) {
        vertexShaders.add(vs.getShaderID());
        fragmentShaders.add(fs.getShaderID());

        int programID = gl.glCreateProgram();
        gl.glAttachShader(programID, vs.getShaderID());
        gl.glAttachShader(programID, fs.getShaderID());
        gl.glLinkProgram(programID);
        gl.glValidateProgram(programID);
        programs.add(programID);

        return new ShaderProgram(gl, programID);
    }

    public static void deletePrograms(GL2 gl) {
        for (int index = 0; index < programs.size(); index++) {
            gl.glDetachShader(programs.get(index), vertexShaders.get(index));
            gl.glDetachShader(programs.get(index), fragmentShaders.get(index));
            gl.glDeleteProgram(programs.get(index));
        }
    }
}
