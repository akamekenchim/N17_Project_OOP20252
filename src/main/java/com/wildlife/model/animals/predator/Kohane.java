package com.wildlife.model.entities.animal.predator;
import com.wildlife.model.abstracts.*;
import com.wildlife.view.SpriteManager;
import com.wildlife.worldmap.WorldMap;
import com.wildlife.model.strategy.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import com.wildlife.core.*;
public class Kohane extends Predator {
    private HunterStrategy_Test brain = new HunterStrategy_Test();
    
    private Image img = SpriteManager.loadImage("wolf2.png");
    public Kohane(double x, double y){
        super(x, y);
        this.setDx(0.9);
        this.setDy(0.01);
    }

    @Override
    public void update(double delta, WorldMap mp) {
        this.setInnerTime(this.getInnerTime() + Constants.SIM_SPEED);
        this.setInnerDirectionTime(this.getInnerDirectionTime() + Constants.SIM_SPEED);
        Vector direction = new Vector(this.getDx(), this.getDy());
        if(this.getInnerTime() > Constants.UPDATE_INTERVAL) {
            direction = brain.execute(this, mp, delta, Constants.RABBIT_SPEED);
            this.setInnerTime(this.getInnerTime() - 10);

        }
        this.setDx(direction.getDx());
        this.setDy(direction.getDy());

        this.setX(Math.min(Constants.SCREEN_WIDTH - 32, Math.max(0, this.getX() + this.getDx() * delta * Constants.WOLF_SPEED)));
        this.setY(Math.min(Constants.SCREEN_HEIGHT - 32, Math.max(0, this.getY() + this.getDy() * delta * Constants.WOLF_SPEED)));
    }
    @Override
    public void render(GraphicsContext gc, boolean isGraphic){
        //gc.drawImage(img, getX(), getY(), 64, 64);
        /*double rotateAngle = Math.toDegrees(Math.atan2(this.getDy(), this.getDx()));
        gc.save();
        gc.translate(getX()+32, getY()+32);
        if(this.getDx() < 0){
            gc.scale(-1, 1);
            gc.rotate(180 - rotateAngle);
        }
        else{
            gc.rotate(rotateAngle);
        }*/
        gc.drawImage(img, getX(), getY(), 32, 32);
    }
}
