package com.wildlife.view;
import com.wildlife.constant.Constants;
import com.wildlife.model.worldmap.MatrixManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
/*import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.Group;
import javafx.scene.Scene.*; */
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class MapRenderer {
    private static WritableImage mapCache;
    private static Image snowImg = SpriteManager.loadImage("snow.png");
    // private static final int MAP_WIDTH = 37 * 32;
    // private static final int MAP_HEIGHT = 26 * 32;

    public void generateMapCache() {
        Canvas cvTemp = new Canvas(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        GraphicsContext gcTemp = cvTemp.getGraphicsContext2D();

        Image img = SpriteManager.loadImage("grass3_0.png");
        Image img2 = SpriteManager.loadImage("water3.png");
        Image img3 = SpriteManager.loadImage("water2.png");
        Image img4 = SpriteManager.loadImage("water_with_rock.png");
        Image img5 = SpriteManager.loadImage("water_lake.png");
        Image img6 = SpriteManager.loadImage("dirt.png");
        // Image tree = SpriteManager.loadImage("treewuwa.png");
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < 37; j++) {
                if (MatrixManager.MAP_LAYOUT[i][j] == 1) {
                    if (j < 6)
                        gcTemp.drawImage(img3, j * 32, i * 32, 32, 32);
                    else
                        gcTemp.drawImage(img2, j * 32, i * 32, 32, 32);
                } else if (MatrixManager.MAP_LAYOUT[i][j] == 0)
                    gcTemp.drawImage(img, j * 32, i * 32, 32, 32);
                else if (MatrixManager.MAP_LAYOUT[i][j] == 2) {
                    gcTemp.drawImage(img4, j * 32, i * 32, 32, 32);
                } else if (MatrixManager.MAP_LAYOUT[i][j] == 3) {
                    gcTemp.drawImage(img5, j * 32, i * 32, 32, 32);
                } else if (MatrixManager.MAP_LAYOUT[i][j] == 4) {
                    gcTemp.drawImage(img6, j * 32, i * 32, 32, 32);
                }
            }
        }
        gcTemp.setFill(Color.rgb(34, 139, 34, 0.75)); // Màu xanh rừng, độ mờ 25%
        // Vẽ từ tọa độ X=0, Y=(Chiều cao màn hình - 300) với kích thước 300x300
        gcTemp.fillRect(0, Constants.SCREEN_HEIGHT - 200, 300, 200);
        gcTemp.fillRect(0, Constants.SCREEN_HEIGHT - 250, 250, 50);
        gcTemp.fillRect(300, Constants.SCREEN_HEIGHT - 200, 50, 180);

        if (snowImg == null) {
            snowImg = SpriteManager.loadImage("snow.png");
        }

        /*
         * for(int i = 20; i<26; i++){
         * for(int j = 31; j<37; j++){
         * gcTemp.drawImage(tree, j*32, i*32, 30, 30);
         * }
         * }
         */
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        mapCache = cvTemp.snapshot(params, null);
    }
    public void renderSnow(GraphicsContext gc, com.wildlife.model.worldmap.WorldMap map) {
        if (!map.isWinter) return; // Chưa mùa đông thì không vẽ gì cả
        
        for (int y = 0; y < Constants.MAP_HEIGHT; y++) {
            for (int x = 0; x < Constants.MAP_WIDTH; x++) {
                if (map.snowMap[y][x]) {
                    gc.drawImage(snowImg, x * Constants.TILE_SIZE, y * Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE);
                }
            }
        }
        gc.setFill(Color.rgb(34, 139, 34, 0.75)); // Màu xanh rừng, độ mờ 25%
        // Vẽ từ tọa độ X=0, Y=(Chiều cao màn hình - 300) với kích thước 300x300
        gc.fillRect(0, Constants.SCREEN_HEIGHT - 200, 300, 200);
        gc.fillRect(0, Constants.SCREEN_HEIGHT - 250, 250, 50);
        gc.fillRect(300, Constants.SCREEN_HEIGHT - 200, 50, 180);
    }
    public Image getMapCache() {
        return mapCache;
    }

    
}
