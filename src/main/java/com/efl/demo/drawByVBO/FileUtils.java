package com.efl.demo.drawByVBO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: EFL-ryl
 */
public class FileUtils {
    public static String[] readShaderSource(String filename) {
        Path path = Paths.get(filename).toAbsolutePath().normalize();
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
}
