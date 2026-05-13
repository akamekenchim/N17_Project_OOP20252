package com.wildlife.model.animals.predator;

import com.wildlife.constant.Constants;
import com.wildlife.model.animals.Animal;
import com.wildlife.model.worldmap.TerrainType;
import com.wildlife.model.worldmap.WorldMap;
import com.wildlife.constant.*;
import com.wildlife.view.SpriteManager;
import com.wildlife.model.strategy.*;
import com.wildlife.model.worldmap.TerrainType;
import com.wildlife.model.worldmap.WorldMap;
import java.util.Random;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
// Động vật ăn thịt, kế thừa từ Animal
public abstract class Predator extends Animal {
    private Random random = new Random(); 
    protected HunterStrategy_Test brain = new HunterStrategy_Test();
    public Predator(double x, double y) {
        super(x, y);
    }
    @Override
    public void update(double delta, WorldMap mp){
        if(this.getHunger() <= 0 || this.getThirst() <= 0){
            this.setAlive(false);
            return;
        }
        this.setInnerTime(this.getInnerTime() + Constants.SIM_SPEED);
        this.setInnerDirectionTime(this.getInnerDirectionTime() + Constants.SIM_SPEED);
        Vector direction = new Vector(this.getDx(), this.getDy());
        if (this.avoidanceTimer > 0) this.avoidanceTimer--;
        
        if (this.getInnerTime() > Constants.UPDATE_INTERVAL) {
            this.setHunger(this.getHunger() - (random.nextDouble() / 3));
            this.setThirst(this.getThirst() - (random.nextDouble() / 8));
            //System.out.println("Current hunger: " + this.getHunger());
            direction = brain.execute(this, mp, delta, Constants.RABBIT_SPEED);
            this.setInnerTime(this.getInnerTime() - Constants.UPDATE_INTERVAL);
        }
        this.setDx(direction.getDx());
        this.setDy(direction.getDy());
        double testX = (Math.min(Constants.SCREEN_WIDTH - Constants.TILE_SIZE,
                Math.max(0, this.getX() + this.getDx() * delta * Constants.WOLF_SPEED)));
        double testY = (Math.min(Constants.SCREEN_HEIGHT - Constants.TILE_SIZE,
                Math.max(0, this.getY() + this.getDy() * delta * Constants.WOLF_SPEED)));
        double prevDx = this.getDx();
        double prevDy = this.getDy();
        int safety = 0;
        boolean hitBoundary = false;
        while((mp.getTile(testX, testY)).getType() == TerrainType.WATER ||
                (mp.getTile(testX, testY + 30)).getType() == TerrainType.WATER ||
                (mp.getTile(testX + 30, testY)).getType() == TerrainType.WATER ||
                (mp.getTile(testX+30, testY+30)).getType() == TerrainType.WATER ||
                testX - 10 <  0 || testX + 35 > Constants.SCREEN_WIDTH || testY - 10 < 0 || testY + 35 > Constants.SCREEN_HEIGHT){
            hitBoundary = true;
            this.setDx(prevDx*Math.cos(Constants.ROTATION) - prevDy*Math.sin(Constants.ROTATION));
            this.setDy(prevDx*Math.sin(Constants.ROTATION) + prevDy*Math.cos(Constants.ROTATION));
            prevDx = this.getDx();
            prevDy = this.getDy();
            testX = (Math.min(Constants.SCREEN_WIDTH - Constants.TILE_SIZE,
                Math.max(0, this.getX() + this.getDx() * delta * Constants.WOLF_SPEED)));
            testY = (Math.min(Constants.SCREEN_HEIGHT - Constants.TILE_SIZE,
                Math.max(0, this.getY() + this.getDy() * delta * Constants.WOLF_SPEED)));
            safety++;
            if (safety >= Constants.THANH_HOA) {
                this.setDx(-this.getDx());
                this.setDy(-this.getDy());
                // Đừng quên cập nhật lastAngle để con thú quay đầu nhìn về hướng mới
                //this.lastAngle = Math.toDegrees(Math.atan2(this.getDy(), this.getDx()));
                break;
            }      
        }
        if(hitBoundary){
            avoidanceTimer = Constants.THANH_HOA;
        }
        this.setX(Math.min(Constants.SCREEN_WIDTH - Constants.TILE_SIZE,
                Math.max(0, this.getX() + this.getDx() * delta * Constants.WOLF_SPEED)));
        this.setY(Math.min(Constants.SCREEN_HEIGHT - Constants.TILE_SIZE,
                Math.max(0, this.getY() + this.getDy() * delta * Constants.WOLF_SPEED)));
    }
}
