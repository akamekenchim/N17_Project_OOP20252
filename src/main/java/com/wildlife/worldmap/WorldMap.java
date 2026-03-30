package com.wildlife.worldmap;

import java.util.*;

//import com.wildlife.core.Constants;
import com.wildlife.model.abstracts.BaseEntity;

public class WorldMap {
    private List<BaseEntity> listEntity = new ArrayList<>();

    public void addEntity(BaseEntity k) {
        listEntity.add(k);
    }

    public List<BaseEntity> getEntity() {
        return listEntity;
    }

    // mấy con mà teo r thì xóa đi
    public void cleaning() {
        for (int i = listEntity.size() - 1; i >= 0; i--) {
            if ((listEntity.get(i)).isAlive() == false) {
                listEntity.remove(i);
            }
        }
    }

    public void Update(double Delta) {
        cleaning();
        for (BaseEntity e : listEntity) {
            e.update(Delta, this);
        }
    }

    public boolean isOccupied(double x, double y) {
        return false;
    }
}
