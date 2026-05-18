package com.wildlife.model.plants;

import com.wildlife.constant.Constants;
import com.wildlife.model.BaseEntity;
import com.wildlife.model.worldmap.*;
import com.wildlife.view.SpriteManager;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;;

public class Rock extends BaseEntity {
    private Image img = SpriteManager.loadImage("rock.png");
    public Rock(double hd, double td) {
        super(hd, td);
    }

    @Override
    public void update(double delta, WorldMap wm) {

    }

    @Override
    public void render(GraphicsContext gc, boolean isGraphic) {
        gc.drawImage(img, getX(), getY(), Constants.TILE_SIZE, Constants.TILE_SIZE);
    }
}
