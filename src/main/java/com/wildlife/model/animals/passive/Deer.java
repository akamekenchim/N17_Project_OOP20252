package com.wildlife.model.animals.passive;

import com.wildlife.constant.*;
import com.wildlife.view.SpriteManager;
import com.wildlife.model.worldmap.WorldMap;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Deer extends Passive {
    public Deer(double x, double y) {
        super(x, y);
        this.setDx(0.9);
        this.setDy(0.01);
        this.speed = Constants.UMA_SPEED;
        this.img = SpriteManager.loadImage("deerSpriteSheet.png");
    }
    @Override
    public void update(double delta, WorldMap mp) {
        super.update(delta, mp);
    }
    @Override
    public void render(GraphicsContext gc, boolean isGraphic) {
        super.render(gc, isGraphic);
        if(Constants.BASIC_VIEW){
            gc.setFill(Color.LIGHTBLUE);
            gc.fillRect(getX(), getY(), Constants.TILE_SIZE, Constants.TILE_SIZE);
        }
    }
}