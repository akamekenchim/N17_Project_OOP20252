package com.wildlife.core;
import java.util.*;
//phần này ae để t múa
import com.wildlife.model.entity.BaseEntity;
public class WorldMap {
    private List<BaseEntity> listEntity = new ArrayList<>();

    public void addEntity(BaseEntity k){
        listEntity.add(k);
    }
    public List<BaseEntity> getEntity(){
        return listEntity;
    }
}
