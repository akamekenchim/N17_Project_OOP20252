package com.wildlife.model.strategy;

import com.wildlife.constant.*;
import com.wildlife.model.BaseEntity;
import com.wildlife.model.animals.passive.Passive;
import com.wildlife.model.animals.predator.Predator;
import com.wildlife.model.worldmap.TerrainType;
import com.wildlife.model.worldmap.WorldMap;

import java.util.List;
import java.util.Random;

public class HunterStrategy_Test {
    private static final double SCAN_RADIUS = 200.0;
    private static final double MAX_WATER_SCAN = 300.0;
    private static final int MAX_CYCLE = 15;
    private int cycle = 0;
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
                if (dist < 25.0) {
                    hunter.setHunger(Math.min(100, hunter.getHunger() + 40));
                   // System.out.println("New hunger: " + hunter.getHunger());
                    entity.setAlive(false);
                    return (new Vector(hunter.getDx(), hunter.getDy())); // Chết thì dừng hành động
                }
            }
        }
        if (hunter.getThirst() < 40 && hunter.getAvoidanceTimer() <= 0) {
            Vector waterDir = findWaterVector(hunter, map);
            if (waterDir != null) return waterDir;
        }
        if (closestPrey != null) {
            // Đuổi theo con mồi
            double dx = closestPrey.getX() - hunter.getX();
            double dy = closestPrey.getY() - hunter.getY();
            double length = Math.sqrt(dx * dx + dy * dy);
            // System.out.println("Chase prey, distance: " + length + ", Prey pos: (" +
            // closestPrey.getX() + ", " + closestPrey.getY() + ")");
            if (length > 0) {
                // Di chuyển theo vector hướng về con mồi
                // hunter.setX(hunter.getX() + (dx / length) * speed * delta);
                // hunter.setY(hunter.getY() + (dy / length) * speed * delta);
                return (new Vector(dx / length, dy / length));
            } else {
                return (new Vector(hunter.getDx(), hunter.getDy()));
            }
        } else if (hunter.getInnerDirectionTime() > Constants.DIRECTION_UPDATE_INTERVAL) {
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
            return (new Vector(hunter.getDx(), hunter.getDy()));
        }
    }

    private double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private Vector findWaterVector(Predator hunter, WorldMap map) {
        double curX = hunter.getX();
        double curY = hunter.getY();
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
                hunter.setThirst(Math.min(100, hunter.getThirst() + 70));
                System.out.println("Thirst: " + hunter.getThirst());
                return new Vector(0, 0);
            }
            return new Vector((targetX - curX) / realDist, (targetY - curY) / realDist);
        }
        return null;
    }
}