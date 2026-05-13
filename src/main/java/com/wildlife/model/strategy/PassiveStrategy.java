package com.wildlife.model.strategy;

import com.wildlife.constant.Constants;

import com.wildlife.model.BaseEntity;
import com.wildlife.model.animals.passive.Passive;
import com.wildlife.model.animals.predator.Predator;
import com.wildlife.model.plants.Grass;
import com.wildlife.model.worldmap.TerrainType;
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
        double targetX = -1, targetY = -1;

        for (double x = curX - MAX_WATER_SCAN; x <= curX + MAX_WATER_SCAN; x += Constants.TILE_SIZE) {
            for (double y = curY - MAX_WATER_SCAN; y <= curY + MAX_WATER_SCAN; y += Constants.TILE_SIZE) {
                // Kiểm tra biên an toàn
                if (x >= 0 && x < Constants.SCREEN_WIDTH && y >= 0 && y < Constants.SCREEN_HEIGHT) {
                    if (map.getTile(x, y).getType() == TerrainType.WATER) {
                        double d2 = (x - curX) * (x - curX) + (y - curY) * (y - curY);
                        if (d2 < minDist) {
                            minDist = d2;
                            targetX = x; targetY = y;
                        }
                    }
                }
            }
        }

        if (targetX != -1) {
            double realDist = Math.sqrt(minDist);
            if (realDist < 20) {
                herbivore.setThirst(Math.min(100, herbivore.getThirst() + 10));
                System.out.println("Thirst: " + herbivore.getThirst());
                return new Vector(0, 0);
            }
            return new Vector((targetX - curX) / realDist, (targetY - curY) / realDist);
        }
        return null;
    }
}
