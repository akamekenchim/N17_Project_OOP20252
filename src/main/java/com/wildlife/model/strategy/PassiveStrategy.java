package com.wildlife.model.strategy;

import com.wildlife.constant.Constants;

import com.wildlife.model.BaseEntity;
import com.wildlife.model.animals.passive.Passive;
import com.wildlife.model.animals.predator.Predator;
import com.wildlife.model.plants.Grass;
import com.wildlife.model.worldmap.WorldMap;
import java.util.*;
public class PassiveStrategy {
    public static final double MAX_SCAN = 120.0;
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
                if(dist < 20){
                    e.setAlive(false);
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
        else if (closestGrass != null && herbivore.getAvoidanceTimer() <= 0) {
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
}
