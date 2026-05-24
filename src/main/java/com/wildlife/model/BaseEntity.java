package com.wildlife.model;

import com.wildlife.model.worldmap.WorldMap;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class BaseEntity {
    private double x;
    private double y;
    private boolean isAlive = true;
    protected Image img;
    protected double hunger = 50;

    public void setHunger(double hunger) {
        this.hunger = hunger;
    }
    public double getHunger() {
        return hunger;
    }

    public BaseEntity(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    // Phương thức để cập nhật trạng thái vật thể
    public abstract void update(double delta, WorldMap map);

    // Phương thức để render ra giao diện đồ họa
    public abstract void render(GraphicsContext gc, boolean isGraphicMode);

    // Phương thức để lấy đường dẫn hình ảnh của vật thể
    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }
}
