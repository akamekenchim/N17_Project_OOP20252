package com.wildlife.model.strategy;
import java.util.List;
import java.util.Random;

import com.wildlife.constant.Constants;

import com.wildlife.model.BaseEntity;
import com.wildlife.model.animals.passive.Passive;
import com.wildlife.model.animals.predator.Predator;
import com.wildlife.model.plants.Grass;
import com.wildlife.model.worldmap.WorldMap;
public class PassiveStrategy_Test {
    public static final double SCAN_RADIUS = 200.0;
    private Random random = new Random();
    public Vector execute(Passive herbivore, WorldMap map){
        if(!herbivore.isAlive()){
            return new Vector(herbivore.getDx(), herbivore.getDy());
        }
        List<BaseEntity> allEntities = map.getEntity();

        BaseEntity closestGrass = null;
        BaseEntity closestPredator = null;
        double closestDist = 10000000;
        double closestPredDist = 10000000;
        for(BaseEntity e : allEntities){
            if(e instanceof Predator && e.isAlive()){
                double dist = getDistance(herbivore.getX(), herbivore.getY(), e.getX(), e.getY());
                if(dist < 25){
                    herbivore.setAlive(false);
                    return new Vector(herbivore.getDx(), herbivore.getDy());
                }
                if(dist < closestPredDist && dist < SCAN_RADIUS){
                    closestPredator = e;
                    closestPredDist = dist;
                }
            }
            else if(e instanceof Grass && e.isAlive()){
                double dist = getDistance(herbivore.getX(), herbivore.getY(), e.getX(), e.getY());
                if(dist < 25){
                    e.setAlive(false);
                    return new Vector(herbivore.getDx(), herbivore.getDy());
                }
                if(dist < closestDist && dist < SCAN_RADIUS){
                    closestGrass = e;
                    closestDist = dist;
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
            // eat
            double dx = (closestGrass.getX() - herbivore.getX());
            double dy = (closestGrass.getY() - herbivore.getY());
            double length = Math.sqrt(dx * dx + dy * dy);

            if (length > 0) {
                return (new Vector(dx / length, dy / length));
            }
            return (new Vector(herbivore.getDx(), herbivore.getDy()));
        }
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


        //return (new Vector(herbivore.getDx(), herbivore.getDy()));
    }


    public double getDistance(double x1, double y1, double x2, double y2){
        return Math.sqrt((x2-x1) * (x2-x1) + (y2-y1) * (y2-y1));
    }
}
