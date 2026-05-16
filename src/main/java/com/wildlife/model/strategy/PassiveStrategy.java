package com.wildlife.model.strategy;

import com.wildlife.constant.Constants;

import com.wildlife.model.BaseEntity;
import com.wildlife.model.animals.passive.Passive;
import com.wildlife.model.animals.predator.Predator;
import com.wildlife.model.plants.Grass;
import com.wildlife.model.worldmap.TerrainType;
import com.wildlife.model.worldmap.Tile;
import com.wildlife.model.worldmap.WorldMap;
import java.util.*;
public class PassiveStrategy {
    public static final double MAX_SCAN = 120.0;
    public static final double MAX_WATER_SCAN = 200.0;
    public static final int MAX_CYCLE = 20;
    private int cycle = 0;
    private Random random = new Random();
    public Vector execute(Passive herbivore, WorldMap map){
        if(herbivore.isAlive() == false){
            return new Vector(herbivore.getDx(), herbivore.getDy());
        }
        
        List<BaseEntity> allEntities = map.getEntity();
        //List<BaseEntity> ScannedEntities = new ArrayList<>();
        double minDistGrass = 1000000;
        BaseEntity closestGrass = null;
        double minDistPred = 1000000;
        BaseEntity closestPredator = null;
        for(BaseEntity e : allEntities){
            if(e instanceof Predator && e.isAlive() == true){
                double dist = getDistance(herbivore.getX(), herbivore.getY(), e.getX(), e.getY());
                if(dist < 20){
                    herbivore.setAlive(false);
                    ((Predator) e).setHunger(Math.min(100, ((Predator) e).getHunger() + 40));
                    return new Vector(herbivore.getDx(), herbivore.getDy());
                }
                if(dist < minDistPred && dist <= MAX_SCAN + 50){
                    minDistPred = dist;
                    closestPredator = e;
                }
            }
            if(e instanceof Grass && e.isAlive() == true){
                double dist = getDistance(herbivore.getX(), herbivore.getY(), e.getX(), e.getY());
                if(dist < 20 && herbivore.getHunger() < 70){
                    e.setAlive(false);
                    herbivore.setHunger(Math.min(100, herbivore.getHunger() + 5));
                    //System.out.println("New hunger: " + herbivore.getHunger());
                    return new Vector(herbivore.getDx(), herbivore.getDy());
                }
                if(dist < minDistGrass && dist <= MAX_SCAN){
                    minDistGrass = dist;
                    closestGrass = e;
                }
            }
        }
        if (closestPredator != null && herbivore.getAvoidanceTimer() <= 0) {
            // chạy luôn
            double dx = -(closestPredator.getX() - herbivore.getX());
            double dy = -(closestPredator.getY() - herbivore.getY());
            double length = Math.sqrt(dx * dx + dy * dy);

            if (length > 0) {
                return (new Vector(dx / length, dy / length));
            }
            return (new Vector(herbivore.getDx(), herbivore.getDy()));
        }
        // Ưu tiên 2: GIẢI KHÁT (Chỉ đi tìm khi an toàn)
        if (herbivore.getThirst() < 90 && herbivore.getAvoidanceTimer() <= 0) {
            Vector waterDir = findWaterVector(herbivore, map);
            if (waterDir != null) return waterDir;
        }
        if (closestGrass != null && herbivore.getAvoidanceTimer() <= 0 && herbivore.getHunger() < 70) {
            // Lao đến ăn
            double dx = closestGrass.getX() - herbivore.getX();
            double dy = closestGrass.getY() - herbivore.getY();
            double length = Math.sqrt(dx * dx + dy * dy);

            if (length > 0) {
                return (new Vector(dx / length, dy / length));
            }
            return (new Vector(herbivore.getDx(), herbivore.getDy()));
        }
        

        // Nếu không đói hoặc không thấy thực thể nào phù hợp trong bán kính, di chuyển bừa
        if (herbivore.getInnerDirectionTime() > Constants.DIRECTION_UPDATE_INTERVAL && herbivore.getAvoidanceTimer() <= 0) {
            if (herbivore.getDx() == 0 && herbivore.getDy() == 0) {
                if(cycle < MAX_CYCLE){
                    cycle++;
                    return new Vector(0, 0);
                } 
                cycle = 0;
                double randomAngle = random.nextDouble() * 360; 
                double rad = Math.toRadians(randomAngle);
                return new Vector(Math.cos(rad), Math.sin(rad));
            }
            double randomAngle = (random.nextDouble() * 180) - 90;
            double rotateAngle = (Math.PI / 180) * randomAngle;
            double dx = herbivore.getDx()*Math.cos(rotateAngle) - herbivore.getDy()*Math.sin(rotateAngle);
            double dy = herbivore.getDx()*Math.sin(rotateAngle) + herbivore.getDy()*Math.cos(rotateAngle);
            herbivore.setInnerDirectionTime(herbivore.getInnerDirectionTime() - Constants.DIRECTION_UPDATE_INTERVAL);
            return (new Vector(dx, dy));
        } else
            return (new Vector(herbivore.getDx(), herbivore.getDy()));
        // herbivore.setX(herbivore.getX() + dx * speed * delta);
        // herbivore.setY(herbivore.getY() + dy * speed * delta);
    }

    public double getDistance(double x1, double y1, double x2, double y2){
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }


    private Vector findWaterVector(Passive herbivore, WorldMap map) {
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
            
            // Nếu khoảng cách <= TILE_SIZE + 5 pixel (sai số), nghĩa là con vật đang đứng sát mép nước
            if (distToWater <= Constants.TILE_SIZE + 5) {
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
}
