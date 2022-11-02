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
import java.text.NumberFormat;
import java.util.*;

/**
 * @author: EFL-ryl
 */
public class GenerateImage extends Application {
    private int PROJECT_WIDTH = 1920;
    private int PROJECT_HEIGHT = 1080;
    private int widthDivNum = 4;
    private int heightDivNum = 3;

    @Override
    public void start(Stage primaryStage) {
        //从数据文件中获取数据并生成对应的colorPixels
        String srcDataFilePath = "src/main/resources/txt/均匀度优化数据示例.txt";
        int[] colorPixels= generateColorPixelsArrByDataFile(srcDataFilePath);
        int[][] bufferImages = new int[heightDivNum][widthDivNum];
        int cellWith = PROJECT_WIDTH /widthDivNum;
        int cellHeight = PROJECT_HEIGHT /heightDivNum;
        int index = 0;
        BufferedImage bufferedImage = new BufferedImage(PROJECT_WIDTH, PROJECT_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        WritableImage image = new WritableImage(PROJECT_WIDTH, PROJECT_HEIGHT);
        for (int i = 0; i < bufferImages.length; i++) {
            for (int j = 0; j < bufferImages[i].length; j++) {
                int[] argbArrays = new int[cellWith * cellHeight];
                Arrays.fill(argbArrays, colorPixels[index]);
                image.getPixelWriter().setPixels(j * cellWith, i * cellHeight, cellWith, cellHeight, WritablePixelFormat.getIntArgbInstance(), argbArrays, 0, 0);
                bufferedImage.setRGB(j * cellWith, i * cellHeight, cellWith, cellHeight, argbArrays, 0, 0);
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
        stage.setWidth(PROJECT_WIDTH);
        stage.setHeight(PROJECT_HEIGHT);
        stage.setAlwaysOnTop(true);
        stage.show();
    }

    /**
     * 逐行按需读取均匀度优化数据文件
     *
     * @param filePath 文件路径
     * @return 返回生成的ColorPixels数组
     */
    private int[] generateColorPixelsArrByDataFile(String filePath) {
        List<String> intensityList = new ArrayList<>();
        List<String> controlDataList = new ArrayList<>();
        int[] intensityArr = new int[0];
        String line;
        int index1 = 0;
        int index2 = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
            String[] str = br.readLine().split("x");
            widthDivNum = Integer.parseInt(str[0]);
            heightDivNum = Integer.parseInt(str[1]);
            intensityArr = new int[widthDivNum * heightDivNum];
            while ((line = br.readLine()) != null) {
                //匹配任意空白行
                if (line.matches( "\\s*")){
                    break;
                }
                intensityList.add(index1, line);
                index1++;
            }
            while ((line = br.readLine()) != null) {
                controlDataList.add(index2, line);
                index2++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int index = 0;
        for (String value : intensityList) {
            String[] str = value.split(" ");
            for (String s : str) {
                intensityArr[index] = Integer.parseInt(s);
                index++;
            }
        }
        //处理原始对照数据并将对照数据的存储类型改为hashMap
        Map<Integer, Integer> controlDataMap = getControlDataMap(controlDataList);
        Object[] array = controlDataMap.values().toArray();
        Integer[] controlDataValueArr = Arrays.copyOf(array, array.length, Integer[].class);
        return intensityArrConvert2ColorPixelsArr(intensityArr, controlDataValueArr, controlDataMap);
    }

    /**
     * 对光强数据进行处理，生成对照的灰度像素数组
     *
     * @param intensityArr 光强数组
     * @param binarySearchArr 二分查找数组
     * @param controlDataMap 对照数据集合
     * @return 返回转换后的灰度像素数组
     */
    private int[] intensityArrConvert2ColorPixelsArr(int[] intensityArr, Integer[] binarySearchArr, Map<Integer, Integer> controlDataMap){
        int[] colorStrPixels = new int[intensityArr.length];
        for (int i = 0; i < intensityArr.length; i++) {
            int grayScaleNum = getKey(controlDataMap, binarySearchClosestTargetNum(binarySearchArr, getPercentageOnlyNum(intensityArr[indexOfSmallest(intensityArr)], intensityArr[i])));
            int alphaSetNum = 255 - grayScaleNum;
            colorStrPixels[i] = alphaSetNum << 24;
        }
        return colorStrPixels;
    }

    /**
     * 遍历controlDataList，把每个元素在空格前的字符赋值到controlDataMap的key，
     * 空格后的字符经过数据转换后则再对应controlDataMap的value
     *
     * @param controlDataList 初始数据list
     * @return 转换后的对照数据controlDataMap
     */
    private Map<Integer, Integer> getControlDataMap(List<String> controlDataList) {
        double maxIntensity = 1;
        HashMap<Integer, Integer> controlDataMap = new HashMap<>();
        for (int i = 0; i < controlDataList.size(); i++) {
            String[] str = controlDataList.get(i).split(" ");
            if (i == 0){
                maxIntensity = Double.parseDouble(str[1]);
            }
            controlDataMap.put(Integer.parseInt(str[0]), getPercentageOnlyNum(Double.parseDouble(str[1]), maxIntensity));
        }
        return controlDataMap;
    }

    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     *根据map的value找到map的key
     *
     * @return 返回对应的key
     */
    private Integer getKey(Map<Integer,Integer> map,Integer value){
        int key = 0;
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if(value.equals(entry.getValue())){
                key= entry.getKey();
            }
        }
        return key;
    }

    /**
     * 找到数组的最小值并返回最小值的下标
     *
     * @param array 目标数组
     * @return 最小值的index
     */
    private int indexOfSmallest(int[] array){
        if (array.length == 0)
            return -1;
        int index = 0;
        int min = array[index];
        for (int i = 1; i < array.length; i++){
            if (array[i] < min){
                min = array[i];
                index = i;
            }
        }
        return index;
    }

    /**
     *得到百分数去掉%后的整数
     *
     * @param dividend 被除数
     * @param divisor 除数
     * @return 整数（百分数去掉%）
     */
    private Integer getPercentageOnlyNum(double dividend, double divisor){
        NumberFormat num = NumberFormat.getPercentInstance();
        String str = num.format(dividend/divisor);
        return Integer.parseInt(str.substring(0,str.length() -1));
    }

    /**
     * 二分查找——找到最接近目标值的整数
     *
     * @param array 查找数组
     * @param targetNum 查找目标
     * @return 返回最接近的整数
     */
    private int binarySearchClosestTargetNum(Integer[] array, int targetNum) {
        Arrays.sort(array);
        int left = 0, right = array.length - 1;
        while (left < right -1) {
            int midIndex = (right + left) / 2;
            int midValue = array[midIndex];
            if(targetNum > midValue){
                left = midIndex;
            } else if (targetNum < midValue){
                right = midIndex;
            } else {
                return array[midIndex];
            }
        }
        int rightNum = array[right];
        int leftNum = array[left];
        return (rightNum - leftNum) / 2 > rightNum - targetNum ? rightNum : leftNum;
    }
}
