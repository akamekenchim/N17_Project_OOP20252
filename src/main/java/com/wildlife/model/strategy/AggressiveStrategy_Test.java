package com.wildlife.model.strategy;

import com.wildlife.constant.Constants;

import com.wildlife.model.BaseEntity;
import com.wildlife.model.animals.passive.Passive;
import com.wildlife.model.animals.predator.Predator;
import com.wildlife.model.plants.Grass;
import com.wildlife.model.worldmap.WorldMap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class AggressiveStrategy_Test {
    private static final double SCAN_RADIUS = 100.0; //bán kính quét để tìm mồi
    private Random random = new Random(); // khởi tạo hàm random

    public Vector execute(Passive herbivore, WorldMap map, double delta, double speed) {
        if (!herbivore.isAlive())
            return (new Vector(herbivore.getDx(), herbivore.getDy())); //Nếu con vật đã chết, ngừng việc update ngay. Sau đó nó sẽ được xóa bỏ

        List<BaseEntity> entities = map.getEntity(); 

        // Vòng lặp quét các thực thể, phân loại rõ ràng - đv ăn thịt, ăn cỏ, cỏ
        for (BaseEntity entity : entities) {
            // Nếu quét mà thấy bị gần con ăn thịt quá thì cho con ăn cỏ chết
            if (entity instanceof Predator && entity.isAlive()) {
                double dist = getDistance(herbivore.getX(), herbivore.getY(), entity.getX(), entity.getY());
                if (dist < 20.0) {
                    herbivore.setAlive(false);
                    return (new Vector(herbivore.getDx(), herbivore.getDy())); // Chết thì dừng hành động
                }
            }
            //Nếu quét được cỏ: Cỏ gần quá thì set alive của cỏ = false, thể hiện đã ăn cỏ
            else if (entity instanceof Grass && entity.isAlive()) {
                double dist = getDistance(herbivore.getX(), herbivore.getY(), entity.getX(), entity.getY());
                if (dist < 30.0) {
                    entity.setAlive(false);
                    return (new Vector(herbivore.getDx(), herbivore.getDy())); // Chết thì dừng hành động
                }
            }
            //Đây là AggressiveStrategy - con vật sẽ ăn cả con ăn cỏ, cho dù nó cũng là ăn cỏ (do đói quá)
            //2 con ăn cỏ lao vào nhau thì 1 con chết và 1 con sống, tỉ lệ 50/50
            else if (entity != herbivore && entity instanceof Passive && entity.isAlive()) {
                double dist = getDistance(herbivore.getX(), herbivore.getY(), entity.getX(), entity.getY());
                if (dist < 20.0) {
                    int k = random.nextInt(2);
                    if (k % 2 == 0) {
                        entity.setAlive(false);
                        return (new Vector(herbivore.getDx(), herbivore.getDy())); // Chết thì dừng hành động
                    } else if (k % 2 > 0) {
                        herbivore.setAlive(false);
                        return (new Vector(herbivore.getDx(), herbivore.getDy())); // Chết thì dừng hành động
                    }
                }
            }
        }

        // Logic Aggressive khi hunger < 30: Quét cả cỏ lẫn con ăn cỏ
        if (herbivore.getHunger() > 0) {
            List<BaseEntity> scannedEntities = new ArrayList<>();
            for (BaseEntity entity : entities) {
                if (entity != herbivore && entity.isAlive() && (entity instanceof Passive || entity instanceof Grass)) {
                    double dist = getDistance(herbivore.getX(), herbivore.getY(), entity.getX(), entity.getY());
                    if (dist <= SCAN_RADIUS) {
                        // System.out.printf("Detected entity at position (%.2f, %.2f), distance:
                        // %.2f\n", entity.getX(), entity.getY(), dist);
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
                    return (new Vector(dx / length, dy / length));
                }
                return (new Vector(herbivore.getDx(), herbivore.getDy()));
            }
        }

        // Nếu không đói hoặc không thấy thực thể nào trong bán kính, di chuyển bừa
        if (herbivore.getInnerDirectionTime() > Constants.DIRECTION_UPDATE_INTERVAL) {
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

    // Hàm SortDist giúp sắp xếp list thực thể theo khoảng cách so với bản thân
    private void sortDist(BaseEntity self, List<BaseEntity> entities) {
        entities.sort(Comparator.comparingDouble(e -> getDistance(self.getX(), self.getY(), e.getX(), e.getY())));
    }

    private double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
}