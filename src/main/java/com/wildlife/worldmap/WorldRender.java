package com.wildlife.worldmap;

import com.wildlife.view.SpriteManager;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import com.wildlife.core.Constants;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene.*;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
public class WorldRender {
    private static WritableImage mapCache;
    //private static final int MAP_WIDTH = 37 * 32;
    //private static final int MAP_HEIGHT = 26 * 32;

    public void generateMapCache(){
        Canvas cvTemp = new Canvas(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        GraphicsContext gcTemp = cvTemp.getGraphicsContext2D();

        Image img = SpriteManager.loadImage("water2.png");

        for(int i = 0; i<37; i++){
            for(int j = 0; j<26; j++){
                gcTemp.drawImage(img, i*32, j*32, 32, 32);
            }
        }

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        mapCache = cvTemp.snapshot(params, null);
    }

    public Image getMapCache() {
        return mapCache;
    }
}
