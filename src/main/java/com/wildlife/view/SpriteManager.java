package com.wildlife.view;
import java.io.InputStream;
import java.util.*;

import javafx.scene.paint.*;
/*import com.wildlife.core.Constants;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;*/
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
//import javafx.stage.Stage;
public class SpriteManager {
    private static final String IMAGE_PATH = "/images/";
    private static Map<String, Image> imageMap = new HashMap<>();
    public static Image loadImage(String fileName){
        if(imageMap.containsKey(fileName)){
            return imageMap.get(fileName);
        }
        try {
            String FULL_IMAGE_PATH = IMAGE_PATH + fileName;
            InputStream IS = SpriteManager.class.getResourceAsStream(FULL_IMAGE_PATH);
            if(IS == null) throw new Exception("File not found: " + FULL_IMAGE_PATH);
            Image i = new Image(IS);
            imageMap.put(fileName, i);
            return i;
        }
        catch(Exception e){
            System.out.println("Ko tim thay anh");
            return createDefaultImage();
        }
        
    }
    private static Image createDefaultImage(){
        int size = 32;
        WritableImage wImage = new WritableImage(size, size);
        PixelWriter pW = wImage.getPixelWriter();

        for(int i = 0; i<size; i++){
            for(int j = 0; j<size; j++){
                if((i+j) % 2 == 0)pW.setColor(i, j, Color.AZURE);
                else pW.setColor(i, j, Color.PINK);
            }
        }
        return wImage;
    }
}
