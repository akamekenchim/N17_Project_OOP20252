package com.wildlife.model.entity;

import com.wildlife.core.WorldMap;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import com.wildlife.view.SpriteManager;
//lớp cha cho các loại cây, ae tạo các class cây trong folder species
public class Grass extends BaseEntity{
    private int type;
    public Grass(double hoanhdo, double tungdo, int t){
        super(hoanhdo, tungdo);
        this.type = t;
    }
    @Override 
    public void update(double delta, WorldMap mp){

    }
    // thg vanh tham khảo đi
    @Override 
    public void render(GraphicsContext gc, boolean isGraphic){
        if(type == 1){
            Image im = SpriteManager.loadImage("wolf2.png");
            gc.drawImage(im, this.getX(), this.getY(), 70, 70);
        }
        else{
            Image im = SpriteManager.loadImage("uma1.png");
            gc.drawImage(im, this.getX(), this.getY(), 70, 70);
        }
    }
}
