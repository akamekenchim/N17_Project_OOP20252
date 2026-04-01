package com.wildlife.core;

import javafx.animation.AnimationTimer;
//import javafx.application.Application;
//import javafx.scene.Group;
//import javafx.scene.Scene;
//import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.*;
//import javafx.stage.Stage;
import java.util.*;
import com.wildlife.control.*;
import com.wildlife.model.abstracts.BaseEntity;
import com.wildlife.view.*;
import com.wildlife.worldmap.WorldMap;

public class SimEngine {
    private WorldMap map;
    private GraphicsContext gc;
    public static double zoomLevel = 1.0;
    public static double camX = 0.0;
    public static double camY = 0.0;
    public SimEngine(WorldMap wm, GraphicsContext g) {
        this.map = wm;
        this.gc = g;
    }
    public static double screenToWorldX(double screenX) {
        return(screenX - camX) / zoomLevel;
    }

    public static double screenToWorldY(double screenY) {
        return(screenY - camY) / zoomLevel;
    }
    public void Start() {
        AnimationTimer AT = new AnimationTimer() {
            // Image testError = SpriteManager.loadImage("femboy_cute.jpg"); // ảnh không tồn tại
            // Image wolf_2 = SpriteManager.loadImage("wolf2.png");
            Image uma_2 = SpriteManager.loadImage("uma2.png");
            Image geng = SpriteManager.loadImage("haiten.png");
            double x = 0;
            long lastTime = 0;

            @Override
            public void handle(long now) {
                if(lastTime == 0){ lastTime = now; return; }
                double deltaTime = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;
                deltaTime = deltaTime * Constants.SIM_SPEED;  //deltaTime là hệ số thời gian, để máy lag hay máy mạnh thì con vật vẫn sẽ di chuyển đúng

                gc.clearRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT); // xoa toan bo man hinh
                gc.save();
                gc.translate(camX, camY);
                gc.scale(zoomLevel, zoomLevel);
                map.Update(deltaTime); // update tat ca trang thai cua ban do hien tai (hàm trong WorldMap)
                renderEntities();
                x += deltaTime*Constants.RABBIT_SPEED;
                if (x >= Constants.SCREEN_WIDTH)
                    x = -1;
                /*
                 * Duyệt qua tất cả các thực thể, dùng vòng lặp for-each cho list lấy được từ
                 * WorldMap (map.getEntity())
                 * Với mỗi thực thể duyệt được, gọi hàm render của nó. Truyền vào gc, lấy
                 * isGraphic = true (chắc thế)
                 */
                gc.drawImage(uma_2, x, 600, 140, 140);
                gc.drawImage(geng, 15, 15, 50, 50);
                //gc.setStroke(Color.PINK);
                //gc.strokeRect(InputControl.hoverx, InputControl.hovery, Constants.TILE_SIZE, Constants.TILE_SIZE);
                gc.setFill(Color.rgb(255, 182, 193, 0.5));
                gc.fillRect(InputControl.hoverx, InputControl.hovery, Constants.TILE_SIZE, Constants.TILE_SIZE);

                gc.restore();
            }
        };
        AT.start();
    }

    private void renderEntities() {
        List<BaseEntity> listEN = map.getEntity(); // Lấy list thực thể từ map
        for (BaseEntity e : listEN) {
            e.render(gc, false); // với mỗi entity có trong listEN, phải render nó
        }
    }
}
