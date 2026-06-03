package com.wildlife.model.plants;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;

import com.wildlife.model.BaseEntity;
import com.wildlife.model.worldmap.WorldMap;
import com.wildlife.view.SpriteManager;

public class Grass extends BaseEntity {
    private int type;
    public static int grassCount = 0;

    public Grass(double x, double y, int type) {
        super(x, y);
        this.type = type;
        grassCount++;
    }

    @Override
    public void update(double delta, WorldMap mp) {

    }

    @Override
    public void render(GraphicsContext gc, boolean isGraphic) {
        if (type == 1) {
            Image im = SpriteManager.loadImage("wolf2.png");
            gc.drawImage(im, this.getX(), this.getY(), 70, 70);
        } else {
            Image im = SpriteManager.loadImage("grassFinal.png");
            gc.drawImage(im, this.getX(), this.getY(), 33, 33);
            gc.setFill(Color.rgb(0, 0, 0, 0.2)); // Màu đen, trong suốt 20%
            gc.fillOval(getX() + 4, getY() + 26, 26, 8); // Hình oval dẹt dưới chân con vật
        }
    }
}
