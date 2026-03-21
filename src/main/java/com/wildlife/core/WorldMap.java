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
    //mấy con mà teo r thì xóa đi
    public void cleaning(){
        for(int i = listEntity.size() - 1; i>=0; i--){
            if((listEntity.get(i)).isAlive() == false){
                listEntity.remove(i);
            }
        }
    }
    public void Update(){
        cleaning();
        for(BaseEntity e : listEntity){
            e.update(); //hàm update chưa có j
        }
    }
}
