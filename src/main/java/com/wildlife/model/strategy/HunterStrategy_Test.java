package com.wildlife.model.strategy;

import com.wildlife.model.abstracts.BaseEntity;
import com.wildlife.model.abstracts.Passive;
import com.wildlife.model.abstracts.Predator;
import com.wildlife.worldmap.WorldMap;
import com.wildlife.core.*;
import java.util.List;
import java.util.Random;

public class HunterStrategy_Test {
    private static final double SCAN_RADIUS = 300.0;
    private Random random = new Random();

    public Vector execute(Predator hunter, WorldMap map, double delta, double speed) {
        List<BaseEntity> entities = map.getEntity();
        Passive closestPrey = null;
        double minDistance = SCAN_RADIUS;

        // Quét bán kính xung quanh tìm động vật ăn cỏ (Passive)
        for (BaseEntity entity : entities) {
            if (entity instanceof Passive && entity.isAlive()) {
                double dist = getDistance(hunter.getX(), hunter.getY(), entity.getX(), entity.getY());
                if (dist < minDistance && dist > 20) {
                    minDistance = dist;
                    closestPrey = (Passive) entity;
                }
                if (dist < 20.0) {
                    entity.setAlive(false);
                    return (new Vector(hunter.getDx(), hunter.getDy())); // Chết thì dừng hành động
                }
            }
        }
        if (closestPrey != null) {
            // Đuổi theo con mồi
            double dx = closestPrey.getX() - hunter.getX();
            double dy = closestPrey.getY() - hunter.getY();
            double length = Math.sqrt(dx * dx + dy * dy);
            //System.out.println("Chase prey, distance: " + length + ", Prey pos: (" + closestPrey.getX() + ", " + closestPrey.getY() + ")");
            if (length > 0) {
                // Di chuyển theo vector hướng về con mồi
                //hunter.setX(hunter.getX() + (dx / length) * speed * delta);
                //hunter.setY(hunter.getY() + (dy / length) * speed * delta);
                return (new Vector(dx/length, dy/length));
            }
            else{
                return (new Vector(hunter.getDx(), hunter.getDy()));
            }
        } else if(hunter.getInnerDirectionTime() > 120) {
            // Di chuyển bừa (Random wander)
            double randomAngle = random.nextDouble() * 2 * Math.PI;
            double dx = Math.cos(randomAngle);
            double dy = Math.sin(randomAngle);
            hunter.setInnerDirectionTime(hunter.getInnerDirectionTime() - 120);
            //hunter.setX(hunter.getX() + dx * speed * delta);
            //hunter.setY(hunter.getY() + dy * speed * delta);
            return (new Vector(dx, dy));

        }
        else{
            return (new Vector(hunter.getDx(), hunter.getDy()));
        }
    }

    private double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
}