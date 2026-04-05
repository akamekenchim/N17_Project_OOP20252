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

        Image img = SpriteManager.loadImage("grassW.png");
        Image img2 = SpriteManager.loadImage("water2.png");
        //Image tree = SpriteManager.loadImage("treewuwa.png");
        for(int i = 0; i<26; i++){
            for(int j = 0; j<37; j++){
                if(MatrixManager.MAP_LAYOUT[i][j] == 1) gcTemp.drawImage(img2, j*32, i*32, 32, 32);
                else if(MatrixManager.MAP_LAYOUT[i][j] == 0) gcTemp.drawImage(img, j*32, i*32, 32, 32);
            }
        }

        /*for(int i = 20; i<26; i++){
            for(int j = 31; j<37; j++){
                gcTemp.drawImage(tree, j*32, i*32, 30, 30);
            }
        }*/
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        mapCache = cvTemp.snapshot(params, null);
    }

    public Image getMapCache() {
        return mapCache;
    }
}
