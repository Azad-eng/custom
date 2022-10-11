package com.ryl.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class JavaFxUtilities {
	public static enum MessageType {
		NONE, INFORMATION, WARNING, ASK, ERROR;
	}
	
	public static String MESSAGE_INFO ="INFO";
	public static String MESSAGE_WARN ="WARN";
	public static String MESSAGE_ERROR ="ERROR";
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
	private static SimpleDateFormat dateFormatSQL = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat timeStampFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	
	protected FXMLLoader loadFxml(ResourceBundle resources, URL resourceFxml, InputStream resourceAsStream) {
		FXMLLoader loader = new FXMLLoader();
		loader.setResources(resources);
		loader.setLocation(resourceFxml);
		try {
			loader.<Parent>load(resourceAsStream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return loader;
	}
	
	public static Image getImageSVG(InputStream path){
		BufferedImageTranscoder trans = new BufferedImageTranscoder();
		Image img = null;
        TranscoderInput transIn = new TranscoderInput(path);
        try {
            trans.transcode(transIn, null);
            // Use WritableImage if you want to further modify the image (by using a PixelWriter)
            img = SwingFXUtils.toFXImage(trans.getBufferedImage(), null);
        } catch (TranscoderException ex) {
            return null;
        }
      
		return img;
	}

	public static ImageView getImagePNG(String fileName){
		Image img = new Image("images/" + fileName +".png");
		ImageView view = new ImageView(img);
		view.setPickOnBounds(true);
		view.setPreserveRatio(true);
		return view;
	}
}
