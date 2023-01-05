package com.efl.demo.triangle.shader;

import com.jogamp.opengl.GL2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;


public class ShaderLoader {
    public static final int VERTEX = GL_VERTEX_SHADER;
    public static final int FRAGMENT = GL_FRAGMENT_SHADER;

    private static ArrayList<Integer> shaders = new ArrayList<Integer>();

    public static Shader loadShader(GL2 gl, String shaderPath, int type) {
        return new Shader(compile(gl, getSource(shaderPath), type));
    }

    private static int compile(GL2 gl, String[] src, int type) {
        int shaderID = gl.glCreateShader(type);
        gl.glShaderSource(shaderID, src.length, src, null, 0);
        gl.glCompileShader(shaderID);
        shaders.add(shaderID);

        return shaderID;
    }

    private static String[] getSource(String shaderPath) {
        Path path = Paths.get(shaderPath).toAbsolutePath().normalize();
        try {
            List<String> lines = Files.readAllLines(path);
            //Alternative 1 [ Append \n at preprocessor lines ]
            return lines.parallelStream()
                    /*Must append a newline separator to the preprocessor directives
                    lines to make GLSL compiler capable to distinguish it from the rest of the code*/
                    .map(line -> line.startsWith("#", 0) ? line += " \n" : line)
                    .collect(Collectors.toList())
                    .toArray(new String[]{});

            /**
             //Alternative 2 [] Append \n at the first line only ]
             var arr = lines.toArray( new String[]{} );
             arr[0] += "\n";
             return arr;
             */
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new String[]{};
    }

    public static void deleteShaders(GL2 gl) {
        for (int i = 0; i < shaders.size(); i++) {
            gl.glDeleteShader(shaders.get(i));
        }
    }
}
