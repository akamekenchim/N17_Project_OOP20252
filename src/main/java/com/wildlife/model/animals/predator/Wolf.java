package com.wildlife.model.animals.predator;

import com.wildlife.constant.*;
import com.wildlife.view.SpriteManager;
import com.wildlife.model.worldmap.WorldMap;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Wolf extends Predator {
    public Wolf(double x, double y) {
        super(x, y);
        this.setDx(0.9);
        this.setDy(0.01);
        this.speed = Constants.WOLF_SPEED;
        this.img = SpriteManager.loadImage("wolfSpriteSheet.png");
    }
    @Override
    public void update(double delta, WorldMap mp) {
        this.speed = Constants.WOLF_SPEED;
        super.update(delta, mp);
    }
    @Override
    public void render(GraphicsContext gc, boolean isGraphic) {
        // 7. Vẽ thanh máu / Giọt nước (kế thừa từ lớp cha)
        super.render(gc, isGraphic);
        if(Constants.BASIC_VIEW){
            gc.setFill(Color.DARKRED);
            gc.fillRect(getX(), getY(), Constants.TILE_SIZE, Constants.TILE_SIZE);
        }
    }
}

// gc.drawImage(img, getX(), getY(), 64, 64);
/*
 * double rotateAngle = Math.toDegrees(Math.atan2(this.getDy(), this.getDx()));
 * gc.save();
 * gc.translate(getX()+32, getY()+32);
 * if(this.getDx() < 0){
 * gc.scale(-1, 1);
 * gc.rotate(180 - rotateAngle);
 * }
 * else{
 * gc.rotate(rotateAngle);
 * }
 */
