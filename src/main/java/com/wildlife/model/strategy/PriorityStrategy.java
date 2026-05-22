package com.wildlife.model.strategy;

import com.wildlife.model.animals.priority.Priority;
import com.wildlife.model.worldmap.WorldMap;
import com.wildlife.constant.Constants;
import java.util.Random;

public class PriorityStrategy {
    private Random random = new Random();
    public Vector execute(Priority entity, WorldMap map) {
        // Logic đi dạo thong dong
        if (entity.getInnerDirectionTime() > Constants.DIRECTION_UPDATE_INTERVAL) {
            entity.setInnerDirectionTime(0);
            double angle = random.nextDouble() * 2 * Math.PI;
            // Trả về Vector hướng đi ngẫu nhiên
            return new Vector(Math.cos(angle), Math.sin(angle));
        }
        return new Vector(entity.getDx(), entity.getDy());
    }
}