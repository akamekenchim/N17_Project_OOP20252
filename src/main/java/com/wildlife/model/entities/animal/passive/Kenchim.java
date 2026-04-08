package com.wildlife.model.entities.animal.passive;
import com.wildlife.model.abstracts.*;
import com.wildlife.view.SpriteManager;
import com.wildlife.worldmap.WorldMap;
import com.wildlife.model.strategy.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import com.wildlife.core.*;
public class Kenchim extends Passive {
    private AggressiveStrategy_Test brain = new AggressiveStrategy_Test();
    private Image img = SpriteManager.loadImage("wolf1.png");
    public Kenchim(double x, double y){
        super(x, y);
        this.setDx(0.9);
        this.setDy(0.01);
    }
    @Override
    public void update(double delta, WorldMap mp) {
        this.setInnerTime(this.getInnerTime() + Constants.SIM_SPEED);
        this.setInnerDirectionTime(this.getInnerDirectionTime() + Constants.SIM_SPEED);
        Vector direction = new Vector(this.getDx(), this.getDy());
        if(this.getInnerTime() > 10) {
            direction = brain.execute(this, mp, delta, Constants.RABBIT_SPEED);
            this.setInnerTime(this.getInnerTime() -10);

        }
        this.setDx(direction.getDx());
        this.setDy(direction.getDy());
        this.setX(Math.min(Constants.SCREEN_WIDTH - 64, Math.max(0, this.getX() + this.getDx() * delta * Constants.FOX_SPEED)));
        this.setY(Math.min(Constants.SCREEN_HEIGHT - 64, Math.max(0, this.getY() + this.getDy() * delta * Constants.FOX_SPEED)));
    }

    @Override
    // Đéo hiểu, mai đọc
    public void render(GraphicsContext gc, boolean isGraphic) {
        gc.save();
        gc.translate(getX()+32, getY()+32);
        double rotateAngle = Math.toDegrees(Math.atan2(this.getDy(), this.getDx()));
        if(this.getDx() < 0){
            gc.scale(-1, 1);
            gc.rotate(180-rotateAngle);
        }
        else{
            gc.rotate(rotateAngle);
        }
        gc.drawImage(img, -32, -32, 64, 64);
        gc.restore();
    }
}