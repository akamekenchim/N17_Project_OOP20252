package com.wildlife.controller;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.*;
import java.util.*;

import com.wildlife.constant.Constants;
import com.wildlife.model.BaseEntity;
import com.wildlife.model.plants.Grass;
import com.wildlife.model.worldmap.*;
import com.wildlife.view.*;

public class SimulationController {
    private WorldMap map;
    private GraphicsContext gc;
    private MapRenderer renderer;
    public static double zoomLevel = 1.0;
    public static double camX = 0.0;
    public static double camY = 0.0;
    public static int currentTime = 0;
    public AnimationTimer AT;
    private int birthCooldown = 0; // Hạn chế sinh sản quá nhanh, mỗi 200 frames mới được sinh sản một lần
    public SimulationController(WorldMap wm, GraphicsContext g, MapRenderer ren) {
        this.map = wm;
        this.gc = g;
        this.renderer = ren;
    }

    public static double screenToWorldX(double screenX) {
        return (screenX - camX) / zoomLevel;
    }

    public static double screenToWorldY(double screenY) {
        return (screenY - camY) / zoomLevel;
    }

    public void Start() {
        AT = new AnimationTimer() {

            Random r = new Random();
            // Image testError = SpriteManager.loadImage("femboy_cute.jpg"); // ảnh không tồn tại
            
            Image logo = SpriteManager.loadImage("geng.png");
            double x = 0;
            long lastTime = 0;

            @Override
            public void handle(long now) {
                currentTime++;
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }
                double deltaTime = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;
                deltaTime = deltaTime * Constants.SIM_SPEED; // deltaTime là hệ số thời gian, để máy lag hay máy mạnh
                                                             // thì con vật vẫn sẽ di chuyển đúng

                gc.clearRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT); // xoa toan bo man hinh
                gc.save();
                gc.translate(camX, camY);
                gc.scale(zoomLevel, zoomLevel);
                gc.drawImage(renderer.getMapCache(), 0, 0);
                renderer.renderSnow(gc, map);
                
                if(currentTime % 100 == 0 && Grass.grassCount < 100 && Constants.SIM_SPEED > 0 ){
                    int placeX = r.nextInt(36);
                    int placeY = r.nextInt(25);
                    if(MatrixManager.MAP_LAYOUT[placeY][placeX] == 0){
                        Grass g = new Grass(placeX*Constants.TILE_SIZE, placeY*Constants.TILE_SIZE,
                        0);
                        map.addEntity(g);
                    }
                    placeX = r.nextInt(34);
                    placeY = r.nextInt(23);
                    if(MatrixManager.MAP_LAYOUT[placeY][placeX] == 0){
                        Grass g = new Grass(placeX*Constants.TILE_SIZE, placeY*Constants.TILE_SIZE,
                        0);
                        map.addEntity(g);
                    }
                    placeX = r.nextInt(36);
                    placeY = r.nextInt(25);
                    if(MatrixManager.MAP_LAYOUT[placeY][placeX] == 0){
                        Grass g = new Grass(placeX*Constants.TILE_SIZE, placeY*Constants.TILE_SIZE,
                        0);
                        map.addEntity(g);
                    }
                }
                // Logic add cỏ theo thời gian
                map.update(deltaTime); 
                birthCooldown += Constants.SIM_SPEED;// update tat ca trang thai cua ban do hien tai (hàm trong WorldMap)
                if (birthCooldown >= 1000) {
                    // ====================================================================
                    // CƠ CHẾ SINH SẢN TỰ NHIÊN (REPRODUCTION)
                    // ====================================================================
                    List<BaseEntity> newBabies = new ArrayList<>(); // "Phòng sinh" tạm thời
                    
                    for (BaseEntity e : map.getEntity()) {
                        if (e.isAlive()) {
                            // 1. Nếu là Động vật ăn cỏ (Passive) - Ví dụ: Thỏ, Hươu
                            if (e instanceof com.wildlife.model.animals.passive.Passive) {
                                com.wildlife.model.animals.passive.Passive p = (com.wildlife.model.animals.passive.Passive) e;
                                if (p.getHunger() >= 85) { // Ăn quá no
                                    
                                    // Tính trước tọa độ tương lai
                                    double newX = p.getX() + 10;
                                    double newY = p.getY() + 10;
                                    int tileX = (int) (newX / Constants.TILE_SIZE);
                                    int tileY = (int) (newY / Constants.TILE_SIZE);
                                    int ff = r.nextInt(2);
                                    // 🌟 CHỐT CHẶN: Chỉ đẻ nếu KHÔNG nằm ở 4 mép tường ma trận
                                    if (tileX > 0 && tileX < Constants.MAP_WIDTH - 1 && tileY > 0 && tileY < Constants.MAP_HEIGHT - 1 && !map.isObstacle(tileX, tileY, e) && ff == 0) {
                                        p.setHunger(50); // Trừ điểm no khi đẻ thành công
                                        
                                        if (p instanceof com.wildlife.model.animals.passive.Rabbit) {
                                            newBabies.add(new com.wildlife.model.animals.passive.Rabbit(newX, newY));
                                        } else if (p instanceof com.wildlife.model.animals.passive.Deer) {
                                            newBabies.add(new com.wildlife.model.animals.passive.Deer(newX, newY));
                                        }
                                    }
                                }
                            }
                            // 2. Nếu là Động vật ăn thịt (Predator) - Ví dụ: Sói, Cáo, Hổ
                            else if (e instanceof com.wildlife.model.animals.predator.Predator) {
                                com.wildlife.model.animals.predator.Predator p = (com.wildlife.model.animals.predator.Predator) e;
                                if (p.getHunger() >= 90) { // Sói sinh sản khó hơn thỏ (cần 90 điểm)
                                    
                                    // Tính trước tọa độ tương lai
                                    double newX = p.getX() + 20;
                                    double newY = p.getY() + 20;
                                    int tileX = (int) (newX / Constants.TILE_SIZE);
                                    int tileY = (int) (newY / Constants.TILE_SIZE);
                                    int ff = r.nextInt(2);
                                    // Chỉ đẻ nếu KHÔNG nằm ở 4 mép tường ma trận
                                    if (tileX > 0 && tileX < Constants.MAP_WIDTH - 1 && tileY > 0 && tileY < Constants.MAP_HEIGHT - 1 && !map.isObstacle(tileX, tileY, e) && ff == 0) {
                                        p.setHunger(50); // Trừ điểm no khi đẻ thành công
                                        
                                        if (p instanceof com.wildlife.model.animals.predator.Wolf) {
                                            newBabies.add(new com.wildlife.model.animals.predator.Wolf(newX, newY));
                                        } else if (p instanceof com.wildlife.model.animals.predator.Tiger) {
                                            newBabies.add(new com.wildlife.model.animals.predator.Tiger(newX, newY));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    for (BaseEntity baby : newBabies) {
                        map.addEntity(baby);
                    }
                    birthCooldown = 0; // Reset cooldown sau khi sinh sản
                }
                // ====================================================================

                renderEntities();
                x += deltaTime * Constants.RABBIT_SPEED;
                if (x >= Constants.SCREEN_WIDTH)
                    x = -1;
                /*
                 * Duyệt qua tất cả các thực thể, dùng vòng lặp for-each cho list lấy được từ
                 * WorldMap (map.getEntity())
                 * Với mỗi thực thể duyệt được, gọi hàm render của nó. Truyền vào gc, lấy
                 * isGraphic = true (chắc thế)
                 */
                
                gc.drawImage(logo, 15, 15, 40, 40);
                // gc.setStroke(Color.PINK);
                // gc.strokeRect(InputControl.hoverx, InputControl.hovery, Constants.TILE_SIZE,
                // Constants.TILE_SIZE);
                gc.setFill(Color.rgb(255, 182, 193, 0.5));
                gc.fillRect(InputController.hoverx, InputController.hovery, Constants.TILE_SIZE, Constants.TILE_SIZE);
                gc.restore();

            }
        };
        AT.start();
    }
    public void stop() {
        if (AT != null) {
            AT.stop(); // Lệnh gốc của JavaFX để dừng đóng băng vòng lặp
            System.out.println("[Engine] Simulation stopped.");
        }
    }
    private void renderEntities() {
        List<BaseEntity> listEN = map.getEntity(); // Lấy list thực thể từ map
        for (BaseEntity e : listEN) {
            if (e instanceof Grass) {
                e.render(gc, false); // với mỗi entity có trong listEN, phải render nó
            }
        }
        for (BaseEntity e : listEN) {
            if (!(e instanceof Grass)) {
                e.render(gc, false); // với mỗi entity có trong listEN, phải render nó
            }
        }
        
    }
}
