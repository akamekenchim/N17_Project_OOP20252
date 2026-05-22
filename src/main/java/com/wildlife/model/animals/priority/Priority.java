package com.wildlife.model.animals.priority;

import java.util.Random;
import com.wildlife.constant.Constants;
import com.wildlife.model.animals.Animal;
import com.wildlife.model.worldmap.TerrainType;
import com.wildlife.model.worldmap.Tile;
import com.wildlife.model.worldmap.WorldMap;
import com.wildlife.model.strategy.*;
import javafx.scene.canvas.GraphicsContext;
import com.wildlife.model.strategy.AggressiveStrategy;

public class Priority extends Animal{
    private Random random = new Random(); 
    protected PriorityStrategy brain = new PriorityStrategy();
    public Priority(double x, double y) {
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
            this.setHunger(this.getHunger() - (random.nextDouble() / 1000));
            this.setThirst(this.getThirst() - (random.nextDouble() / 1000));
            //System.out.println("Current hunger: " + this.getHunger());
            direction = brain.execute(this, mp);
            this.setInnerTime(this.getInnerTime() - Constants.UPDATE_INTERVAL);
        }
        this.setDx(direction.getDx());
        this.setDy(direction.getDy());
        // Trong Passive.java (và cả Predator.java sau này)
        double testX = (Math.min(Constants.SCREEN_WIDTH - Constants.TILE_SIZE,
                Math.max(0, this.getX() + this.getDx() * delta * this.speed)));
        double testY = (Math.min(Constants.SCREEN_HEIGHT - Constants.TILE_SIZE,
                Math.max(0, this.getY() + this.getDy() * delta * this.speed)));
        
        double prevDx = this.getDx();
        double prevDy = this.getDy();
        int safety = 0;
        boolean hitBoundary = false;

        // KIỂM TRA 4 GÓC CỦA CON VẬT QUA HÀM isObstacle
        while(mp.isObstacle(testX, testY, this) ||
              mp.isObstacle(testX, testY + 25, this) ||
              mp.isObstacle(testX + 25, testY, this) ||
              mp.isObstacle(testX + 25, testY + 25, this)
            || testX - 10 <  0 || testX + 35 > Constants.SCREEN_WIDTH || testY - 10 < 0 || testY + 35 > Constants.SCREEN_HEIGHT) {
            
            hitBoundary = true;
            this.setDx(prevDx * Math.cos(Constants.ROTATION) - prevDy * Math.sin(Constants.ROTATION));
            this.setDy(prevDx * Math.sin(Constants.ROTATION) + prevDy * Math.cos(Constants.ROTATION));
            
            prevDx = this.getDx();
            prevDy = this.getDy();
            
            testX = (Math.min(Constants.SCREEN_WIDTH - Constants.TILE_SIZE,
                Math.max(0, this.getX() + this.getDx() * delta * this.speed)));
            testY = (Math.min(Constants.SCREEN_HEIGHT - Constants.TILE_SIZE,
                Math.max(0, this.getY() + this.getDy() * delta * this.speed)));
                
            safety++;
            if (safety >= Constants.THANH_HOA) {
                this.setDx(-this.getDx());
                this.setDy(-this.getDy());
                break;
            }      
        }
        if(hitBoundary){
            avoidanceTimer = Constants.THANH_HOA;
        }
        else {
            // Lấy ô gạch ở TÂM con vật tại vị trí sắp bước tới
            // Nhờ hàm getTile(double, double) Lead viết sẵn trong WorldMap, ta truyền thẳng pixel vào luôn!
            Tile targetTile = mp.getTile(testX + 15, testY + 15);
            boolean isDirt = (targetTile != null && targetTile.getType() == TerrainType.DIRT);

            // ƯU TIÊN 1: LOGIC "BẦY ĐÀN" (Vướng đồng loại thì đi cực chậm - 5% tốc độ)
            if (mp.isCompanion(testX, testY, this)) {
                testX = this.getX() + this.getDx() * delta * (this.speed * 0.05); 
                testY = this.getY() + this.getDy() * delta * (this.speed * 0.05);
            }
            // ƯU TIÊN 2: LOGIC "BÙN ĐẤT" (Lội bùn thì đi chậm vừa - 50% tốc độ)
            else if (isDirt) {
                testX = this.getX() + this.getDx() * delta * (this.speed * 0.5); 
                testY = this.getY() + this.getDy() * delta * (this.speed * 0.5);
            }
            // Trờng hợp còn lại (Đi trên cỏ): Giữ nguyên testX, testY ở tốc độ 100%
        }
        //this.setX(Math.min(Constants.SCREEN_WIDTH - Constants.TILE_SIZE,
          //      Math.max(0, this.getX() + this.getDx() * delta * this.speed)));
        //this.setY(Math.min(Constants.SCREEN_HEIGHT - Constants.TILE_SIZE,
         //       Math.max(0, this.getY() + this.getDy() * delta * this.speed)));
        this.setX(testX);
        this.setY(testY);
    }
    @Override
    public void render(GraphicsContext gc, boolean isGraphic){
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
            double destW = 40; // Hoặc Constants.TILE_SIZE
            double destH = 40;

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
        super.render(gc, isGraphic);
    }
}
