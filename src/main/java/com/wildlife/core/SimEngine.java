package com.wildlife.core;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.wildlife.model.entity.*;
import java.util.*;
public class SimEngine {
    private WorldMap map;
    private GraphicsContext gc;
    public SimEngine(WorldMap wm, GraphicsContext g){
        this.map = wm;
        this.gc = g;
    }
    public void Start(){
        AnimationTimer AT = new AnimationTimer() {
            @Override
            public void handle(long now){
                gc.clearRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT); // xoa toan bo man hinh
                map.Update(); //update tat ca trang thai cua ban do hien tai (hàm update chưa có gì, ở trong WorldMap) 
                renderEntities();
                /*Duyệt qua tất cả các thực thể, dùng vòng lặp for-each cho list lấy được từ WorldMap (map.getEntity())
                Với mỗi thực thể duyệt được, gọi hàm render của nó. Truyền vào gc, lấy isGraphic = true (chắc thế)*/ 
            }
        };
        AT.start();
    }
    private void renderEntities(){
        List<BaseEntity> listEN = map.getEntity(); //Lấy list thực thể từ map
        for(BaseEntity e : listEN){
            e.render(gc, false); //với mỗi entity có trong listEN, phải render nó
        }
    }
}
