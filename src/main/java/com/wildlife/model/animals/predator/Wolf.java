package com.wildlife.model.animals.predator;

import com.wildlife.constant.*;
import com.wildlife.view.SpriteManager;
import com.wildlife.model.strategy.*;
import com.wildlife.model.worldmap.TerrainType;
import com.wildlife.model.worldmap.WorldMap;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Wolf extends Predator {
    private Image img = SpriteManager.loadImage("wolfSpriteSheet.png");
    public Wolf(double x, double y) {
        super(x, y);
        this.setDx(0.9);
        this.setDy(0.01);
        this.speed = Constants.WOLF_SPEED;
    }
    @Override
    public void update(double delta, WorldMap mp) {
        super.update(delta, mp);
    }
    @Override
    public void render(GraphicsContext gc, boolean isGraphic) {
        if(!Constants.BASIC_VIEW){
            // 1. Lấy kích thước thật của 1 khung hình (Cắt Spritesheet làm 4 cột, 4 hàng)
            double frameWidth = img.getWidth() / 4;
            double frameHeight = img.getHeight() / 4;

            int row = 0; // Mặc định hàng 0 (Đứng im)
            int col = 0; // Mặc định cột 0

            // 2. Kiểm tra xem con vật có đang di chuyển không
            boolean isMoving = (this.getDx() != 0 || this.getDy() != 0);

            if (isMoving) {
                row = 1; // Dùng hàng 1 (Hoạt ảnh chạy)
                
                // BÍ QUYẾT ANIMATION STATELESS: 
                // Cứ mỗi 100ms (0.1s) sẽ nhảy sang cột tiếp theo (0 -> 1 -> 2 -> 3 -> 0)
                col = (int) ((System.currentTimeMillis() / 100) % 4);
            } else {
                row = 0; // Dùng hàng 0 (Đứng im)
                col = 0; // Tạm thời lấy frame đứng im đầu tiên
            }

            // 3. Tính toán tọa độ cái "kéo" sẽ cắt trên spritesheet
            double srcX = col * frameWidth;
            double srcY = row * frameHeight;

            // 4. Tính toán tọa độ và kích thước sẽ vẽ lên màn hình
            double destX = getX();
            double destY = getY();
            double destW = 32; // Hoặc Constants.TILE_SIZE
            double destH = 32;

            // 5. LẬT ẢNH (FLIP) CỰC NHANH KHÔNG CẦN SCALE
            // Spritesheet của bạn đang quay mặt sang TRÁI. 
            // Nếu con vật đang đi sang PHẢI (dx > 0), ta lật ngược ảnh lại.
            if (this.getDx() > 0) { 
                destX = getX() + destW; // Dịch điểm bắt đầu sang mép phải
                destW = -destW;         // Vẽ với chiều rộng ÂM (JavaFX sẽ tự lật ngược ảnh)
            }

            // 6. Vẽ mảnh ảnh đã cắt lên màn hình
            gc.drawImage(img, srcX, srcY, frameWidth, frameHeight, destX, destY, destW, destH);
        }
        else{
            gc.setFill(Color.PALEGREEN);
            gc.fillRect(getX(), getY(), Constants.TILE_SIZE, Constants.TILE_SIZE);
        }
        // 7. Vẽ thanh máu / Giọt nước (kế thừa từ lớp cha)
        super.render(gc, isGraphic);
    }
}

// gc.drawImage(img, getX(), getY(), 64, 64);
/*
 * double rotateAngle = Math.toDegrees(Math.atan2(this.getDy(), this.getDx()));
 * gc.save();
 * gc.translate(getX()+32, getY()+32);
 * if(this.getDx() < 0){
 * gc.scale(-1, 1);
 * gc.rotate(180 - rotateAngle);
 * }
 * else{
 * gc.rotate(rotateAngle);
 * }
 */

/*@Override
    public void update(double delta, WorldMap mp) {
        this.setInnerTime(this.getInnerTime() + Constants.SIM_SPEED);
        this.setInnerDirectionTime(this.getInnerDirectionTime() + Constants.SIM_SPEED);
        Vector direction = new Vector(this.getDx(), this.getDy());
        if (this.getInnerTime() > Constants.UPDATE_INTERVAL) {
            direction = brain.execute(this, mp, delta, Constants.RABBIT_SPEED);
            this.setInnerTime(this.getInnerTime() - 10);

        }
        this.setDx(direction.getDx());
        this.setDy(direction.getDy());

        this.setX(Math.min(Constants.SCREEN_WIDTH - 32,
                Math.max(0, this.getX() + this.getDx() * delta * Constants.WOLF_SPEED)));
        this.setY(Math.min(Constants.SCREEN_HEIGHT - 32,
                Math.max(0, this.getY() + this.getDy() * delta * Constants.WOLF_SPEED)));
    }
    */