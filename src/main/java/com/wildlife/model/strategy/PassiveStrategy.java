package com.wildlife.model.strategy;

import com.wildlife.constant.Constants;

import com.wildlife.model.BaseEntity;
import com.wildlife.model.animals.passive.Passive;
import com.wildlife.model.animals.predator.Predator;
import com.wildlife.model.plants.Grass;
import com.wildlife.model.worldmap.TerrainType;
import com.wildlife.model.worldmap.Tile;
import com.wildlife.model.worldmap.WorldMap;
//import com.wildlife.view.SoundManager;

import java.util.*;
public class PassiveStrategy {
    public static final double MAX_SCAN = 220.0;
    public static final double MAX_WATER_SCAN = 200.0;
    public static final int MAX_CYCLE = 20;
    public static final int THIRST_THRESHOLD = 50;
    private int cycle = 0;
    private Random random = new Random();
    public Vector execute(Passive herbivore, WorldMap map){
        if(isInWater(herbivore.getX(), herbivore.getY(), map)){
            herbivore.setAlive(false);
            return (new Vector(herbivore.getDx(), herbivore.getDy())); 
        }
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
                if(dist < 20 && e.getHunger() < 80){
                    //SoundManager.playSound("predatorExecute.wav");
                    herbivore.setAlive(false);
                    ((Predator) e).setHunger(Math.min(100, ((Predator) e).getHunger() + 40));
                    herbivore.setDrinking(false);
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
                    //SoundManager.playSound("grassEaten.wav");
                    herbivore.setHunger(Math.min(100, herbivore.getHunger() + 30));
                    //System.out.println("New hunger: " + herbivore.getHunger());
                    return new Vector(herbivore.getDx(), herbivore.getDy());
                }
                if(dist < minDistGrass && dist <= MAX_SCAN){
                    minDistGrass = dist;
                    closestGrass = e;
                }
            }
        }
        com.wildlife.model.BaseEntity closestPriority = null;
        double minPriorityDist = 120.0; // Bán kính nhận diện sự hiện diện của con người (120 pixel)

        for (com.wildlife.model.BaseEntity e : map.getEntity()) {
            if (e instanceof com.wildlife.model.animals.priority.Priority && e.isAlive()) {
                double dist = getDistance(herbivore.getX(), herbivore.getY(), e.getX(), e.getY());
                if (dist < minPriorityDist) {
                    minPriorityDist = dist;
                    closestPriority = e;
                }
            }
        }

        // Nếu thấy con người lởn vởn gần đó
        if (closestPriority != null) {
            // Toán học: Tính Vector dạt ra (Tọa độ của mình TRỪ ĐI tọa độ con người)
            double dx = herbivore.getX() - closestPriority.getX();
            double dy = herbivore.getY() - closestPriority.getY();
            double length = Math.sqrt(dx * dx + dy * dy);
            
            if (length > 0) {
                // Chuẩn hóa vector và rẽ ngang ra để né. 
                // Không nhân 1.5 tốc độ (vì đây là dạt ra nhường đường chứ không phải hoảng loạn bỏ chạy)
                return new Vector(dx / length, dy / length);
            }
        }
        if (closestPredator != null) {
            // 1. Kiểm tra xem con vật ĐÃ CHẠY VÀO TRONG RỪNG CHƯA?
            if (isInForest(herbivore.getX(), herbivore.getY())) {
                // Đã an toàn trong rừng -> Đứng im nấp, không tạo tiếng động
                return new Vector(0, 0); 
            } else {
                // 2. TÍNH TOÁN KHOẢNG CÁCH TỚI TÂM KHU RỪNG
                double targetForestX = 150.0;
                double targetForestY = Constants.SCREEN_HEIGHT - 100.0;
                
                double dxForest = targetForestX - herbivore.getX();
                double dyForest = targetForestY - herbivore.getY();
                double distToForest = Math.sqrt(dxForest * dxForest + dyForest * dyForest);
                
                // 3. RỪNG Ở GẦN (Trong bán kính 200) -> LAO VÀO RỪNG
                if (distToForest <= MAX_SCAN) {
                    if (distToForest > 0) {
                        // Trả về Vector chạy bứt tốc về phía rừng
                        return new Vector((dxForest / distToForest) * 1.3, (dyForest / distToForest) * 1.3);
                    }
                } 
                // 4. RỪNG QUÁ XA -> CHẠY NGƯỢC HƯỚNG KẺ THÙ (BẢN NĂNG)
                else {
                    double escapeX = herbivore.getX() - closestPredator.getX();
                    double escapeY = herbivore.getY() - closestPredator.getY();
                    double escapeDist = Math.sqrt(escapeX * escapeX + escapeY * escapeY);
                    
                    if (escapeDist > 0) {
                        // Trả về Vector chạy bứt tốc ngược lại hướng con thú săn mồi
                        return new Vector((escapeX / escapeDist) * 1.2, (escapeY / escapeDist) * 1.2);
                    }
                }
            }
        }
        // Ưu tiên 2: GIẢI KHÁT (Chỉ đi tìm khi an toàn)
        if (herbivore.getThirst() < THIRST_THRESHOLD && herbivore.getAvoidanceTimer() <= 0) {
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
            if (herbivore.getDx() == 0 && herbivore.getDy() == 0 && closestPredator == null) {
                if(cycle < MAX_CYCLE){
                    herbivore.setDrinking(true);
                    cycle++;
                    return new Vector(0, 0);
                } 
                herbivore.setDrinking(false);
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
        boolean block1 = (x >= 0 && x <= 280) && (y >= h - 180 && y <= h);
        boolean block2 = (x >= 0 && x <= 250) && (y >= h - 230 && y <= h - 180);
        boolean block3 = (x >= 280 && x <= 325) && (y >= h - 180 && y <= h - 15); // h - 200 + 180 = h - 20

        return block1 || block2 || block3;
    }

    public boolean isInWater(double x, double y, WorldMap m) {
        return ((m.getTile(x, y)).getType() == TerrainType.WATER);
    }
}
