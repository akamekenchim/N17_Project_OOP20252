package com.wildlife.model.strategy;

import com.wildlife.constant.Constants;
import com.wildlife.model.BaseEntity;
import com.wildlife.model.animals.aggressive.Aggressive;
import com.wildlife.model.animals.passive.Passive;
import com.wildlife.model.animals.predator.Predator;
import com.wildlife.model.plants.Grass;
import com.wildlife.model.worldmap.TerrainType;
import com.wildlife.model.worldmap.Tile;
import com.wildlife.model.worldmap.WorldMap;
import java.util.*;

public class AggressiveStrategy {
    // Giữ nguyên các hằng số của PassiveStrategy
    public static final double MAX_SCAN = 120.0;
    public static final double MAX_WATER_SCAN = 200.0;
    public static final int MAX_CYCLE = 20;
    public static final int THIRST_THRESHOLD = 50;
    private int cycle = 0;
    private Random random = new Random();

    public Vector execute(Aggressive herbivore, WorldMap map) {
        if (!herbivore.isAlive()) {
            return new Vector(herbivore.getDx(), herbivore.getDy());
        }

        List<BaseEntity> allEntities = map.getEntity();
        
        // THAY ĐỔI 1: Gom chung Cỏ và Đồng loại vào một biến "Thức ăn" (closestFood)
        double minDistFood = 1000000;
        BaseEntity closestFood = null; 
        
        double minDistPred = 1000000;
        BaseEntity closestPredator = null;

        // THAY ĐỔI 2: Tạo biến cờ hiệu kiểm tra xem con thú đã đủ đói để "Làm liều" chưa
        boolean isAggressive = herbivore.getHunger() < 30; // Dưới 30 điểm đói thì nổi điên

        for (BaseEntity e : allEntities) {
            
            // 1. KẺ THÙ (Sói) - Vẫn sợ sói như bình thường
            if (e instanceof Predator && e.isAlive()) {
                double dist = getDistance(herbivore.getX(), herbivore.getY(), e.getX(), e.getY());
                if (dist < 20 && e.getHunger() < 80) {
                    herbivore.setAlive(false);
                    ((Predator) e).setHunger(Math.min(100, ((Predator) e).getHunger() + 40));
                    return new Vector(0, 0); 
                }
                if (dist < minDistPred && dist <= MAX_SCAN + 50) {
                    minDistPred = dist;
                    closestPredator = e;
                }
            }
            
            // 2. THỨC ĂN BÌNH THƯỜNG (Cỏ)
            if (e instanceof Grass && e.isAlive()) {
                double dist = getDistance(herbivore.getX(), herbivore.getY(), e.getX(), e.getY());
                if (dist < 20 && herbivore.getHunger() < 70) {
                    e.setAlive(false);
                    herbivore.setHunger(Math.min(100, herbivore.getHunger() + 20)); // Cỏ hồi ít
                    //eturn new Vector(0, 0); // Đứng lại ăn (fix lỗi máy hút bụi)
                }
                if (dist < minDistFood && dist <= MAX_SCAN) {
                    minDistFood = dist;
                    closestFood = e;
                }
            }

            // 3. THAY ĐỔI 3 - LOGIC LÀM LIỀU: Coi đồng loại như thức ăn nếu isAggressive == true
            if (isAggressive && e != herbivore && (e instanceof Passive || e instanceof Aggressive) && e.isAlive()) {
                double dist = getDistance(herbivore.getX(), herbivore.getY(), e.getX(), e.getY());
                
                // Tràn vào cắn xé nhau
                if (dist < 20) {
                    int k = random.nextInt((e instanceof Aggressive) ? 2 : 1); // Tỉ lệ 50/50 như Lead thiết kế
                    if (k == 0) {
                        e.setAlive(false); // Giết đồng loại
                        herbivore.setHunger(Math.min(100, herbivore.getHunger() + 40)); // Hồi nhiều no hơn ăn cỏ
                        return new Vector(0, 0); // Đứng lại ăn
                    } else {
                        herbivore.setAlive(false); // Bị giết ngược
                        return new Vector(herbivore.getDx(), herbivore.getDy());
                    }
                }
                
                // Nếu chưa chạm, đưa đồng loại vào danh sách mục tiêu "Thức ăn"
                if (dist < minDistFood && dist <= MAX_SCAN) {
                    minDistFood = dist;
                    closestFood = e; // Ghi đè cỏ nếu đồng loại đứng gần hơn
                }
            }
        }

        // ==========================================
        // CÁC HÀNH ĐỘNG ƯU TIÊN (Giống hệt Passive)
        // ==========================================
        
        // Ưu tiên 1: Chạy trốn kẻ thù
        if (closestPredator != null && herbivore.getAvoidanceTimer() <= 0) {
            double dx = -(closestPredator.getX() - herbivore.getX());
            double dy = -(closestPredator.getY() - herbivore.getY());
            double length = Math.sqrt(dx * dx + dy * dy);
            if (length > 0) return new Vector(dx / length, dy / length);
            return new Vector(herbivore.getDx(), herbivore.getDy());
        }

        // Ưu tiên 2: GIẢI KHÁT
        if (herbivore.getThirst() < THIRST_THRESHOLD && herbivore.getAvoidanceTimer() <= 0) {
            Vector waterDir = findWaterVector(herbivore, map);
            if (waterDir != null) return waterDir;
        }

        // Ưu tiên 3: ĐI TÌM THỨC ĂN (Sẽ tự đuổi theo Cỏ hoặc Đồng loại tùy vào biến closestFood)
        if (closestFood != null && herbivore.getAvoidanceTimer() <= 0 && herbivore.getHunger() < 70) {
            double dx = closestFood.getX() - herbivore.getX();
            double dy = closestFood.getY() - herbivore.getY();
            double length = Math.sqrt(dx * dx + dy * dy);
            if (length > 0) return new Vector(dx / length, dy / length);
            return new Vector(herbivore.getDx(), herbivore.getDy());
        }

        // Ưu tiên 4: ĐI DẠO (Random Wander)
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
            return new Vector(dx, dy);
        } else {
            return new Vector(herbivore.getDx(), herbivore.getDy());
        }
    }

    public double getDistance(double x1, double y1, double x2, double y2){
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }

    // Hàm uống nước đã được cập nhật logic tâm (Center Point) mượt mà nhất
    private Vector findWaterVector(Aggressive herbivore, WorldMap map) {
        double curX = herbivore.getX();
        double curY = herbivore.getY();
        double minDist = Double.MAX_VALUE;
        
        double targetX = -1, targetY = -1;
        double bestWaterX = -1, bestWaterY = -1; 

        int[] dx = {-Constants.TILE_SIZE, Constants.TILE_SIZE, 0, 0};
        int[] dy = {0, 0, -Constants.TILE_SIZE, Constants.TILE_SIZE};

        for (double x = curX - MAX_WATER_SCAN; x <= curX + MAX_WATER_SCAN; x += Constants.TILE_SIZE) {
            for (double y = curY - MAX_WATER_SCAN; y <= curY + MAX_WATER_SCAN; y += Constants.TILE_SIZE) {
                if (x >= 0 && x < Constants.SCREEN_WIDTH && y >= 0 && y < Constants.SCREEN_HEIGHT) {
                    Tile waterTile = map.getTile(x, y);
                    if (waterTile != null && waterTile.getType() == TerrainType.WATER) {
                        for (int i = 0; i < 4; i++) {
                            double nx = x + dx[i];
                            double ny = y + dy[i];

                            if (nx >= 0 && nx < Constants.SCREEN_WIDTH && ny >= 0 && ny < Constants.SCREEN_HEIGHT) {
                                Tile neighborTile = map.getTile(nx, ny);
                                if (neighborTile != null && neighborTile.isPassable()) {
                                    double centerNx = nx + (Constants.TILE_SIZE / 2.0);
                                    double centerNy = ny + (Constants.TILE_SIZE / 2.0);
                                    double animalCenterX = curX + 15; 
                                    double animalCenterY = curY + 15;

                                    double d2 = (centerNx - animalCenterX)*(centerNx - animalCenterX) + (centerNy - animalCenterY)*(centerNy - animalCenterY);
                                    
                                    if (d2 < minDist) {
                                        minDist = d2;
                                        targetX = centerNx;
                                        targetY = centerNy;
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

        if (targetX != -1) {
            double animalCenterX = curX + 15;
            double animalCenterY = curY + 15;
            double distToWater = Math.sqrt((bestWaterX - animalCenterX)*(bestWaterX - animalCenterX) + (bestWaterY - animalCenterY)*(bestWaterY - animalCenterY));
            
            if (distToWater <= Constants.TILE_SIZE + 5) {
                herbivore.setThirst(Math.min(100, herbivore.getThirst() + 70));
                return new Vector(0, 0); 
            }
            
            double moveDx = targetX - animalCenterX;
            double moveDy = targetY - animalCenterY;
            double length = Math.sqrt(moveDx * moveDx + moveDy * moveDy);
            return new Vector(moveDx / length, moveDy / length);
        }
        return null;
    }
}