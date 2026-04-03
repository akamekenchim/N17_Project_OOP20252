package com.wildlife.model.strategy;

import com.wildlife.model.abstracts.BaseEntity;
import com.wildlife.model.abstracts.Passive;
import com.wildlife.model.abstracts.Predator;
import com.wildlife.worldmap.WorldMap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class AggressiveStrategy {
    private static final double SCAN_RADIUS = 200.0;
    private Random random = new Random();

    public void execute(Passive herbivore, WorldMap map, double delta, double speed) {
        if (!herbivore.isAlive()) return;

        List<BaseEntity> entities = map.getEntity();

        // Kiểm tra khoảng cách với động vật ăn thịt, nếu < 10 thì con ăn cỏ chết
        for (BaseEntity entity : entities) {
            if (entity instanceof Predator && entity.isAlive()) {
                double dist = getDistance(herbivore.getX(), herbivore.getY(), entity.getX(), entity.getY());
                if (dist < 10.0) {
                    herbivore.setAlive(false);
                    return; // Chết thì dừng hành động
                }
            }
        }

        // Logic Aggressive khi hunger < 30
        if (herbivore.getHunger() < 30) {
            List<BaseEntity> scannedEntities = new ArrayList<>();
            for (BaseEntity entity : entities) {
                if (entity != herbivore && entity.isAlive()) {
                    double dist = getDistance(herbivore.getX(), herbivore.getY(), entity.getX(), entity.getY());
                    if (dist <= SCAN_RADIUS) {
                        scannedEntities.add(entity);
                    }
                }
            }

            if (!scannedEntities.isEmpty()) {
                // Ưu tiên chọn con mồi gần nhất bằng hàm SortDist
                sortDist(herbivore, scannedEntities);
                BaseEntity target = scannedEntities.get(0);

                // Lao đến ăn
                double dx = target.getX() - herbivore.getX();
                double dy = target.getY() - herbivore.getY();
                double length = Math.sqrt(dx * dx + dy * dy);

                if (length > 0) {
                    herbivore.setX(herbivore.getX() + (dx / length) * speed * delta);
                    herbivore.setY(herbivore.getY() + (dy / length) * speed * delta);
                }
                return;
            }
        }

        // Nếu không đói hoặc không thấy thực thể nào trong bán kính, di chuyển bừa
        double randomAngle = random.nextDouble() * 2 * Math.PI;
        double dx = Math.cos(randomAngle);
        double dy = Math.sin(randomAngle);
        
        herbivore.setX(herbivore.getX() + dx * speed * delta);
        herbivore.setY(herbivore.getY() + dy * speed * delta);
    }

    // Hàm SortDist giúp sắp xếp list thực thể theo khoảng cách so với bản thân
    private void sortDist(BaseEntity self, List<BaseEntity> entities) {
        entities.sort(Comparator.comparingDouble(e -> 
            getDistance(self.getX(), self.getY(), e.getX(), e.getY())
        ));
    }

    private double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
}