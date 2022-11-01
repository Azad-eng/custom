package com.efl.demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * @author: EFL-ryl
 */
public class GenerateImage extends Application {
    private int width = 1920;
    private int height = 1080;
    private int widthDivNum = 4;
    private int heightDivNum = 3;

    @Override
    public void start(Stage primaryStage) {
        getData();
        int[] colorPixels = {0x00000000, 0x04000000, 0x04000000, 0x02000000,
                             0x02000000, 0x04000000, 0x06000000, 0x02000000,
                             0x02000000, 0x04000000, 0x06000000, 0x02000000};
        int[][] bufferImages = new int[3][4];
        int index = 0;
        BufferedImage bufferedImage = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_ARGB);
        WritableImage image = new WritableImage(1920, 1080);
        for (int i = 0; i < bufferImages.length; i++) {
            for (int j = 0; j < bufferImages[i].length; j++) {
                int[] argbArrays = new int[width/4 * height/3];
                Arrays.fill(argbArrays, colorPixels[index]);
                image.getPixelWriter().setPixels(j * 480, i * 360, 480, 360, WritablePixelFormat.getIntArgbInstance(), argbArrays, 0, 0);
                bufferedImage.setRGB(j * 480, i * 360, 480, 360, argbArrays, 0, 0);
                System.out.println("(" + j * 480 + "," + i * 360 + ")  index=" + index);
                index ++;
            }
        }
        try {
            ImageIO.write(bufferedImage, "png", new File("C:\\Users\\EFL\\Pictures\\argb.png"));
        } catch(IOException e) {
            e.printStackTrace();
        }
        BorderPane borderPane = new BorderPane();
        Parent root = new Group(borderPane, new ImageView(image));
        Scene scene = new Scene(root);
        //scene.setCursor(Cursor.NONE);
        Stage stage = new Stage();
        root.setOnMouseClicked(event -> {
            //右键点击
            if (event.getButton() == MouseButton.SECONDARY) {
                stage.close();
            }
        });
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        if(/*MachineConfig.PRO*/false) {
            stage.setX(600);
        } else {
            stage.setX(0);
        }
        stage.setY(0);
        stage.setWidth(1920); //MachineConfig.PROJECT_WIDTH
        stage.setHeight(1080); //MachineConfig.PROJECT_HEIGHT)
        stage.setAlwaysOnTop(true);
        stage.show();
    }

    private void getData() {
        List<String> list = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        //匹配任意空白行
        String regexContainN = "\\s*";
        String line;
        int index1 = 0;
        int index2 = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(new File("E:\\Aazd-Home\\myStudySpace\\javaxFxLearn\\custom\\src\\main\\resources\\txt\\均匀度优化数据示例.txt")))) {
            while ((line = br.readLine()) != null) {
                if (line.matches(regexContainN)){
                    break;
                }
                list.add(index1, line);
                index1++;
            }
            while ((line = br.readLine()) != null) {
                list2.add(index2, line);
                index2++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(list.toString());
        System.out.println(list2.toString());
    }

    private static int binarySearchKey(Object[] array, int targetNum) {
        Arrays.sort(array);
        int left = 0, right = 0;
        for (right = array.length - 1; left != right; ) {
            int midIndex = (right + left) / 2;
            int mid = (right - left);
            int midValue = (Integer) array[midIndex];
            if (targetNum == midValue) {
                return midIndex;
            }
            if (targetNum > midValue) {
                left = midIndex;
            } else {
                right = midIndex;
            }
            if (mid <= 1) {
                break;
            }
        }
        int rightnum = ((Integer) array[right]).intValue();
        int leftnum = ((Integer) array[left]).intValue();
        int ret = Math.abs((rightnum - leftnum) / 2) > Math.abs(rightnum - targetNum) ? rightnum : leftnum;
        return ret;
    }

    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
