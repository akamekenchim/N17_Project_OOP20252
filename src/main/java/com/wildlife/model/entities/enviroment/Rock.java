package com.wildlife.model.entities.enviroment;

import com.wildlife.model.abstracts.BaseEntity;
import com.wildlife.worldmap.*;

import javafx.scene.canvas.GraphicsContext;;
public class Rock extends BaseEntity{
    public Rock(double td, double hd){
        super(hd, td);
    }
    @Override
    public void update(double delta, WorldMap wm){

    }
    @Override
    public void render(GraphicsContext gc, boolean isGraphic){
        
    }
}
