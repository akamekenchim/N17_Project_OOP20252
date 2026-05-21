package com.wildlife.model.animals.predator;

import com.wildlife.constant.*;
import com.wildlife.view.SpriteManager;
import com.wildlife.model.worldmap.WorldMap;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Tiger extends Predator {
    public Tiger(double x, double y) {
        super(x, y);
        this.setDx(0.9);
        this.setDy(0.01);
        this.speed = Constants.TIGER_SPEED;
        this.img = SpriteManager.loadImage("tigerSpriteSheet.png");
    }
   @Override
    public void update(double delta, WorldMap mp) {
        this.speed = Constants.WOLF_SPEED;
        super.update(delta, mp);
    }
    @Override
    public void render(GraphicsContext gc, boolean isGraphic) {
        super.render(gc, isGraphic);
        if(Constants.BASIC_VIEW){
            gc.setFill(Color.HOTPINK);
            gc.fillRect(getX(), getY(), Constants.TILE_SIZE, Constants.TILE_SIZE);
        }
    }
}