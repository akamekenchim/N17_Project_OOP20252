package com.wildlife.model.animals.passive;

import com.wildlife.constant.*;
import com.wildlife.view.SpriteManager;
import com.wildlife.model.strategy.*;
import com.wildlife.model.worldmap.TerrainType;
import com.wildlife.model.worldmap.WorldMap;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Kenchim extends Passive {
    private AggressiveStrategy_Test brain = new AggressiveStrategy_Test();
    private Image img = SpriteManager.loadImage("wolf1.png");

    public Kenchim(double x, double y) {
        super(x, y);
        this.setDx(0.9);
        this.setDy(0.01);
    }

    @Override
    public void update(double delta, WorldMap mp) {
        this.setInnerTime(this.getInnerTime() + Constants.SIM_SPEED);
        this.setInnerDirectionTime(this.getInnerDirectionTime() + Constants.SIM_SPEED);
        Vector direction = new Vector(this.getDx(), this.getDy());
        if (this.getInnerTime() > Constants.UPDATE_INTERVAL) {
            direction = brain.execute(this, mp, delta, Constants.RABBIT_SPEED);
            this.setInnerTime(this.getInnerTime() - Constants.UPDATE_INTERVAL);

        }
        this.setDx(direction.getDx());
        this.setDy(direction.getDy());
        int testX = (int) (Math.min(Constants.SCREEN_WIDTH - 32,
                Math.max(0, this.getX() + this.getDx() * delta * Constants.FOX_SPEED * 0.2)) / Constants.TILE_SIZE);
        int testY = (int) (Math.min(Constants.SCREEN_HEIGHT - 32,
                Math.max(0, this.getY() + this.getDy() * delta * Constants.FOX_SPEED * 0.2)) / Constants.TILE_SIZE);
        double prevDx = this.getDx();
        double prevDy = this.getDy();
        int safety = 0;
        while((mp.getTile(testX, testY)).getType() == TerrainType.WATER){
            this.setDx(prevDx*Math.cos(Constants.ROTATION) - prevDy*Math.sin(Constants.ROTATION));
            this.setDy(prevDx*Math.sin(Constants.ROTATION) + prevDy*Math.cos(Constants.ROTATION));
            prevDx = this.getDx();
            prevDy = this.getDy();
            testX = (int) (Math.min(Constants.SCREEN_WIDTH - 32,
                Math.max(0, this.getX() + this.getDx() * delta * Constants.FOX_SPEED)) / Constants.TILE_SIZE);
            testY = (int) (Math.min(Constants.SCREEN_HEIGHT - 32,
                Math.max(0, this.getY() + this.getDy() * delta * Constants.FOX_SPEED)) / Constants.TILE_SIZE); 
            safety++;
            if (safety >= 36) {
                this.setDx(-this.getDx());
                this.setDy(-this.getDy());
                // Đừng quên cập nhật lastAngle để con thú quay đầu nhìn về hướng mới
                //this.lastAngle = Math.toDegrees(Math.atan2(this.getDy(), this.getDx()));
                break;
            }      
        }
        this.setX(Math.min(Constants.SCREEN_WIDTH - 32,
                Math.max(0, this.getX() + this.getDx() * delta * Constants.FOX_SPEED)));
        this.setY(Math.min(Constants.SCREEN_HEIGHT - 32,
                Math.max(0, this.getY() + this.getDy() * delta * Constants.FOX_SPEED)));
    }

    @Override
    // Đéo hiểu, mai đọc
    public void render(GraphicsContext gc, boolean isGraphic) {
        /*
         * gc.save();
         * gc.translate(getX()+32, getY()+32);
         * double rotateAngle = Math.toDegrees(Math.atan2(this.getDy(), this.getDx()));
         * if(this.getDx() < 0){
         * gc.scale(-1, 1);
         * gc.rotate(180-rotateAngle);
         * }
         * else{
         * gc.rotate(rotateAngle);
         * }
         */
        gc.drawImage(img, getX(), getY(), 32, 32);
    }
}