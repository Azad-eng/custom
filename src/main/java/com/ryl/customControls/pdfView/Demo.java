package com.ryl.customControls.pdfView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * @author: EFL-ryl
 */
public class Demo extends Application {

    public static void main(String[] args) {
        Application.launch();
    }

    public void start(Stage primaryStage) throws Exception {
        PdfFXViewer web = new PdfFXViewer();
        //web.open("//localhost/c:/Nobilis/Test.pdf");
        //web.open("c:/Nobilis/Test.pdf");
        //web.open("");
        //primaryStage.setTitle("Web View");
        web.openDocument("C:\\Users\\EFL\\Desktop\\my note\\github标星137k的Java总结.pdf");
        primaryStage.setTitle("PDF View");
        Scene scene = new Scene(web,850,700, Color.web("#666970"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}