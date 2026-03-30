package com.wildlife.core;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.util.*;

import com.wildlife.model.abstracts.BaseEntity;
import com.wildlife.view.*;
import com.wildlife.worldmap.WorldMap;

public class SimEngine {
    private WorldMap map;
    private GraphicsContext gc;

    public SimEngine(WorldMap wm, GraphicsContext g) {
        this.map = wm;
        this.gc = g;
    }

    public void Start() {
        AnimationTimer AT = new AnimationTimer() {
            // Image akame = SpriteManager.loadImage("akamesc.jpg"); // ảnh có tồn tại
            // Image testError = SpriteManager.loadImage("femboy_cute.jpg"); // ảnh không
            // tồn tại
            // Image uma_1 = SpriteManager.loadImage("uma1.png");
            // Image uma_2 = SpriteManager.loadImage("uma2.png");
            // Image wolf_1 = SpriteManager.loadImage("wolf1.png");
            // Image wolf_2 = SpriteManager.loadImage("wolf2.png");
            Image geng = SpriteManager.loadImage("haiten.png");
            int x = 0;

            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT); // xoa toan bo man hinh
                map.Update(); // update tat ca trang thai cua ban do hien tai (hàm trong WorldMap)
                renderEntities();
                x += Constants.RABBIT_SPEED;
                if (x >= Constants.SCREEN_WIDTH)
                    x = -1;
                /*
                 * Duyệt qua tất cả các thực thể, dùng vòng lặp for-each cho list lấy được từ
                 * WorldMap (map.getEntity())
                 * Với mỗi thực thể duyệt được, gọi hàm render của nó. Truyền vào gc, lấy
                 * isGraphic = true (chắc thế)
                 */

                // gc.drawImage(wolf_1, x, 200, 150, 150);
                // gc.drawImage(uma_1, x, 400, 140, 140); // sẽ in ra ảnh default - ô vuông lưới
                // hồng xanh
                // gc.drawImage(uma_2, x, 600, 140, 140);
                gc.drawImage(geng, 15, 15, 50, 50);
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
