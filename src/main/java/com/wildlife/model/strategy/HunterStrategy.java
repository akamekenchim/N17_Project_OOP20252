package com.wildlife.model.strategy;

import com.wildlife.constant.*;
import com.wildlife.model.BaseEntity;
import com.wildlife.model.animals.aggressive.Aggressive;
import com.wildlife.model.animals.passive.Passive;
import com.wildlife.model.animals.predator.Predator;
import com.wildlife.model.animals.priority.Priority;
import com.wildlife.model.worldmap.TerrainType;
import com.wildlife.model.worldmap.Tile;
import com.wildlife.model.worldmap.WorldMap;
//import com.wildlife.view.SoundManager;

import java.util.List;
import java.util.Random;

public class HunterStrategy {
    private static final double SCAN_RADIUS = 200.0;
    private static final double MAX_WATER_SCAN = 300.0;
    private static final int MAX_CYCLE = 15;
    private static final double WATER_THRESHOLD = 50.0;
    private int cycle = 0;
    private Random random = new Random();

    public Vector execute(Predator hunter, WorldMap map) {
        if(isInWater(hunter.getX(), hunter.getY(), map)){
            hunter.setAlive(false);
            return (new Vector(hunter.getDx(), hunter.getDy())); 
        }
        List<BaseEntity> entities = map.getEntity();
        
        // 1. SỬA ĐỔI: Dùng 1 biến BaseEntity duy nhất để lưu con mồi (không phân biệt Thỏ hay Cáo)
        BaseEntity targetPrey = null; 
        double minDistance = SCAN_RADIUS;

        // Quét bán kính xung quanh tìm mồi
        for (BaseEntity entity : entities) {
            if ((entity instanceof Passive || entity instanceof Aggressive) && entity.isAlive() && entity != hunter) {
                double dist = getDistance(hunter.getX(), hunter.getY(), entity.getX(), entity.getY());
                if (isInForest(entity.getX(), entity.getY())) {
                    continue; // Mồi đang trong rừng -> Mù dấu, bỏ qua không quét con này nữa!
                }
                // 2. SỬA ĐỔI: Gán thẳng mục tiêu gần nhất vào targetPrey
                if (dist < minDistance && dist > 20) {
                    minDistance = dist;
                    targetPrey = entity; 
                }
                if (dist < 25.0 && hunter.getHunger() < 80) { // Đủ gần để tấn công, và sói chưa quá no
                    //SoundManager.playSound("predatorExecute.wav");
                    hunter.setHunger(Math.min(100, hunter.getHunger() + 40));
                    entity.setAlive(false);
                    return (new Vector(hunter.getDx(), hunter.getDy())); 
                }
            }
        }
        BaseEntity closestPriority = null;
        double minPriorityDist = 120.0; // Bán kính nhận diện sự hiện diện của con người (120 pixel)

        for (BaseEntity e : map.getEntity()) {
            if (e instanceof Priority && e.isAlive()) {
                double dist = getDistance(hunter.getX(), hunter.getY(), e.getX(), e.getY());
                if (dist < minPriorityDist) {
                    minPriorityDist = dist;
                    closestPriority = e;
                }
            }
        }

        // Nếu thấy con người lởn vởn gần đó
        if (closestPriority != null) {
            // Toán học: Tính Vector dạt ra (Tọa độ của mình TRỪ ĐI tọa độ con người)
            double dx = hunter.getX() - closestPriority.getX();
            double dy = hunter.getY() - closestPriority.getY();
            double length = Math.sqrt(dx * dx + dy * dy);
            
            if (length > 0) {
                // Chuẩn hóa vector và rẽ ngang ra để né. 
                // Không nhân 1.5 tốc độ (vì đây là dạt ra nhường đường chứ không phải hoảng loạn bỏ chạy)
                return new Vector(dx / length, dy / length);
            }
        }
        // (Logic khát nước giữ nguyên)
        if (hunter.getThirst() < WATER_THRESHOLD && hunter.getAvoidanceTimer() <= 0) {
            Vector waterDir = findWaterVector(hunter, map);
            if (waterDir != null) return waterDir;
        }
        
        // 3. SỬA ĐỔI: Cho sói đuổi theo targetPrey (áp dụng cho cả Thỏ và Cáo)
        if (targetPrey != null && hunter.getHunger() < 80) { // Chỉ đuổi nếu chưa quá no, tránh tình trạng sói đuổi mồi rồi bỏ đói meo không ăn
            // Đuổi theo con mồi
            double dx = targetPrey.getX() - hunter.getX();
            double dy = targetPrey.getY() - hunter.getY();
            hunter.setSpeed(Math.min(hunter.getSpeed()*1.1, Constants.WOLF_SPEED * 1.4));
            double length = Math.sqrt(dx * dx + dy * dy);
            
            if (length > 0) {
                return (new Vector(dx / length, dy / length));
            } else {
                return (new Vector(hunter.getDx(), hunter.getDy()));
            }
        }
        else if (hunter.getInnerDirectionTime() > Constants.DIRECTION_UPDATE_INTERVAL) {
            hunter.setSpeed(Math.max(hunter.getSpeed()/1.1, Constants.WOLF_SPEED));
            if (hunter.getDx() == 0 && hunter.getDy() == 0) {
                if(cycle < MAX_CYCLE){
                    cycle++;
                    return new Vector(0, 0);
                } 
                cycle = 0;
                double randomAngle = random.nextDouble() * 360; 
                double rad = Math.toRadians(randomAngle);
                return new Vector(Math.cos(rad), Math.sin(rad));
            }
            // Di chuyển bừa (Random wander)
            double randomAngle = random.nextDouble() * 2 * Math.PI;
            double dx = Math.cos(randomAngle);
            double dy = Math.sin(randomAngle);
            hunter.setInnerDirectionTime(hunter.getInnerDirectionTime() - 120);
            // hunter.setX(hunter.getX() + dx * speed * delta);
            // hunter.setY(hunter.getY() + dy * speed * delta);
            return (new Vector(dx, dy));

        } else {
            hunter.setSpeed(Math.max(hunter.getSpeed()/1.2, Constants.WOLF_SPEED));
            return (new Vector(hunter.getDx(), hunter.getDy()));
        }
    }

    private double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private Vector findWaterVector(Predator herbivore, WorldMap map) {
        double curX = herbivore.getX();
        double curY = herbivore.getY();
        double minDist = Double.MAX_VALUE;
        
        // Lưu tọa độ TÂM của bờ hồ và TÂM của ô nước
        double targetX = -1, targetY = -1;
        double bestWaterX = -1, bestWaterY = -1; 

        // 4 hướng quét ô hàng xóm liền kề: Trái, Phải, Trên, Dưới
        int[] dx = {-Constants.TILE_SIZE, Constants.TILE_SIZE, 0, 0};
        int[] dy = {0, 0, -Constants.TILE_SIZE, Constants.TILE_SIZE};

        for (double x = curX - MAX_WATER_SCAN; x <= curX + MAX_WATER_SCAN; x += Constants.TILE_SIZE) {
            for (double y = curY - MAX_WATER_SCAN; y <= curY + MAX_WATER_SCAN; y += Constants.TILE_SIZE) {
                
                // 1. Kiểm tra ô gốc có nằm trong bản đồ và là NƯỚC không
                if (x >= 0 && x < Constants.SCREEN_WIDTH && y >= 0 && y < Constants.SCREEN_HEIGHT) {
                    Tile waterTile = map.getTile(x, y);
                    if (waterTile != null && waterTile.getType() == TerrainType.WATER) {
                        
                        // 2. KHẢO SÁT 4 Ô XUNG QUANH ĐỂ TÌM "BỜ HỒ"
                        for (int i = 0; i < 4; i++) {
                            double nx = x + dx[i];
                            double ny = y + dy[i];

                            if (nx >= 0 && nx < Constants.SCREEN_WIDTH && ny >= 0 && ny < Constants.SCREEN_HEIGHT) {
                                Tile neighborTile = map.getTile(nx, ny);
                                
                                if (neighborTile != null && neighborTile.isPassable()) {
                                    
                                    // ĐIỂM CHUẨN 1: Tính tọa độ TÂM của ô đất để con vật đi mượt hơn
                                    double centerNx = nx + (Constants.TILE_SIZE / 2.0);
                                    double centerNy = ny + (Constants.TILE_SIZE / 2.0);
                                    double animalCenterX = curX + 15; // Giả sử con vật rộng 30
                                    double animalCenterY = curY + 15;

                                    double d2 = (centerNx - animalCenterX)*(centerNx - animalCenterX) + (centerNy - animalCenterY)*(centerNy - animalCenterY);
                                    
                                    if (d2 < minDist) {
                                        minDist = d2;
                                        targetX = centerNx;
                                        targetY = centerNy;
                                        
                                        // ĐIỂM CHUẨN 2: Lưu lại TÂM của mặt nước
                                        bestWaterX = x + (Constants.TILE_SIZE / 2.0);
                                        bestWaterY = y + (Constants.TILE_SIZE / 2.0);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. ĐIỀU HƯỚNG VÀ UỐNG NƯỚC
        if (targetX != -1) {
            double animalCenterX = curX + 15;
            double animalCenterY = curY + 15;
            
            // Tính khoảng cách THỰC TẾ từ Tâm con vật đến Tâm mặt nước
            double distToWater = Math.sqrt((bestWaterX - animalCenterX)*(bestWaterX - animalCenterX) + (bestWaterY - animalCenterY)*(bestWaterY - animalCenterY));
            
            // Nếu khoảng cách <= TILE_SIZE + 15 pixel (sai số), nghĩa là con vật đang đứng sát mép nước
            if (distToWater <= Constants.TILE_SIZE + 15) {
                herbivore.setThirst(Math.min(100, herbivore.getThirst() + 70));
                // System.out.println("Thirst: " + herbivore.getThirst());
                return new Vector(0, 0); // Cúi xuống uống nước
            }
            
            // Nếu chưa tới sát mép, tiếp tục đi về phía TÂM của bờ hồ
            double moveDx = targetX - animalCenterX;
            double moveDy = targetY - animalCenterY;
            double length = Math.sqrt(moveDx * moveDx + moveDy * moveDy);
            
            return new Vector(moveDx / length, moveDy / length);
        }
        return null;
    }
    public boolean isInForest(double x, double y) {
        double h = Constants.SCREEN_HEIGHT;
        
        // Kiểm tra xem tọa độ có lọt vào 1 trong 3 khối hình chữ nhật của rừng không
        boolean block1 = (x >= 0 && x <= 300) && (y >= h - 200 && y <= h);
        boolean block2 = (x >= 0 && x <= 250) && (y >= h - 250 && y <= h - 200);
        boolean block3 = (x >= 300 && x <= 350) && (y >= h - 200 && y <= h - 20); // h - 200 + 180 = h - 20

        return block1 || block2 || block3;
    }

    public boolean isInWater(double x, double y, WorldMap m) {
        return ((m.getTile(x, y)).getType() == TerrainType.WATER);
    }
}