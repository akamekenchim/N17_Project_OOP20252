package com.wildlife.model.entities.enviroment;

import com.wildlife.core.WorldMap;
import com.wildlife.model.abstracts.BaseEntity;

import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import com.wildlife.view.SpriteManager;

public class Grass extends BaseEntity {
    private int type;

    public Grass(double x, double y, int type) {
        super(x, y);
        this.type = type;
    }

    @Override
    public void update(double delta, WorldMap mp) {

    }

    @Override
    // Đéo hiểu, mai đọc
    public void render(GraphicsContext gc, boolean isGraphic) {
        if (type == 1) {
            Image im = SpriteManager.loadImage("wolf2.png");
            gc.drawImage(im, this.getX(), this.getY(), 70, 70);
        } else {
            Image im = SpriteManager.loadImage("uma1.png");
            gc.drawImage(im, this.getX(), this.getY(), 70, 70);
        }
    }
}
