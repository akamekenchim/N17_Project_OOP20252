package com.wildlife.model.abstracts;

import javafx.scene.canvas.GraphicsContext;
import com.wildlife.core.WorldMap;

public abstract class BaseEntity {
    private double x;
    private double y;
    private boolean isAlive = true;
    private String spritePath; // Đây là biến lưu đường dẫn đến ảnh động của vật thể

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
    public String getSpritePath() {
        return spritePath;
    }

    // Phương thức để cập nhật đường dẫn hình ảnh của vật thể
    public void setSpritePath(String spritePath) {
        this.spritePath = spritePath;
    }
}
