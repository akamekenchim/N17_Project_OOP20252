package com.wildlife.model.animals.passive;

import com.wildlife.constant.*;
import com.wildlife.view.SpriteManager;
import com.wildlife.model.strategy.*;
import com.wildlife.model.worldmap.TerrainType;
import com.wildlife.model.worldmap.WorldMap;
import java.util.Random;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Cat extends Passive {
    private Image img = SpriteManager.loadImage("wolf1.png");
    public Cat(double x, double y) {
        super(x, y);
        this.setDx(0.9);
        this.setDy(0.01);
    }
    @Override
    public void update(double delta, WorldMap mp) {
        super.update(delta, mp);
    }
    @Override
    public void render(GraphicsContext gc, boolean isGraphic) {
        /*
         * gc.save();
         * gc.translate(getX()+Constants.TILE_SIZE, getY()+Constants.TILE_SIZE);
         * double rotateAngle = Math.toDegrees(Math.atan2(this.getDy(), this.getDx()));
         * if(this.getDx() < 0){
         * gc.scale(-1, 1);
         * gc.rotate(180-rotateAngle);
         * }
         * else{
         * gc.rotate(rotateAngle);
         * }
         */
        gc.drawImage(img, getX(), getY(), Constants.TILE_SIZE, Constants.TILE_SIZE);
    }
}