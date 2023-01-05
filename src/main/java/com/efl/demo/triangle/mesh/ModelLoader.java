package com.efl.demo.triangle.mesh;

import com.efl.demo.triangle.shader.ShaderProgram;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static com.jogamp.opengl.GL.*;

public class ModelLoader {
    private static ArrayList<Integer> vao = new ArrayList<>();
    private static ArrayList<Integer> vbo = new ArrayList<>();
    private static GL2 gl;
    private static int vaoID = 0 , vboID = 0;

    public static Model loadModel(GL2 gl, String modelPath, ShaderProgram program) {
        ModelLoader.gl = gl;
        ArrayList<Float> positions = new ArrayList<>();
        ArrayList<Float> colors = new ArrayList<>();
        ArrayList<Float> texCoords = new ArrayList<>();
        ArrayList<Float> normals = new ArrayList<>();

        ModelReader.readModel(modelPath, positions, texCoords, normals, colors);

        return getModel(listToArray(positions), listToArray(colors), listToArray(texCoords), listToArray(normals), program);
    }

    public static Model getModel(float[] positions, float[] colors, float[] texCoords, float[] normals, ShaderProgram program) {
        int vaoID = createVao();
        storeInAttribute(0, 3, positions);
        storeInAttribute(1, 4, colors);
        storeInAttribute(2, 2, texCoords);
        storeInAttribute(3, 3, normals);
        unbind();

        return new Model(vaoID, positions.length / 3, program);
    }

    private static int createVao() {
        gl.glGenVertexArrays(vaoID, Buffers.newDirectIntBuffer(listToIntArray(vao)));
        vao.add(vaoID);
        vaoID ++;
        gl.glBindVertexArray(vaoID);

        return vaoID;
    }

    private static void storeInAttribute(int location, int vecSize, float[] data) {
        gl.glGenBuffers(vboID, Buffers.newDirectIntBuffer(listToIntArray(vbo)));
        vbo.add(vboID);
        vboID ++;
        gl.glBindBuffer(GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = Buffers.newDirectFloatBuffer(data);
        gl.glBufferData(GL_ARRAY_BUFFER, buffer.limit() * 4L, buffer, GL_STATIC_DRAW);
        gl.glVertexAttribPointer(location, vecSize, GL_FLOAT, false, 0, 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private static void unbind() {
        gl.glBindVertexArray(0);
    }

    public static void deleteModels() {
        for (int i = 0; i < vao.size(); i++) {
            gl.glDeleteVertexArrays(vao.get(i), Buffers.newDirectIntBuffer(listToIntArray(vao)));
        }
        for (int i = 0; i < vbo.size(); i++) {
            gl.glDeleteBuffers(vbo.get(i), Buffers.newDirectIntBuffer(listToIntArray(vao)));
        }
    }

    private static float[] listToArray(ArrayList<Float> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private static int[] listToIntArray(ArrayList<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}
