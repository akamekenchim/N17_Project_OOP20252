package com.wildlife.model.animals.aggressive;

import com.wildlife.constant.*;
import com.wildlife.view.SpriteManager;
import com.wildlife.model.worldmap.WorldMap;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Fox extends Aggressive {
    public Fox(double x, double y) {
        super(x, y);
        this.setDx(0.9);
        this.setDy(0.01);
        this.speed = Constants.FOX_SPEED;
        this.img = SpriteManager.loadImage("foxSpriteSheet.png");
    }
   @Override
    public void update(double delta, WorldMap mp) {
        super.update(delta, mp);
    }
    @Override
    public void render(GraphicsContext gc, boolean isGraphic) {
        // 7. Vẽ thanh máu / Giọt nước (kế thừa từ lớp cha)
        super.render(gc, isGraphic);
        if(Constants.BASIC_VIEW){
            gc.setFill(Color.DARKORANGE);
            gc.fillRect(getX(), getY(), Constants.TILE_SIZE, Constants.TILE_SIZE);
        }
    }
}