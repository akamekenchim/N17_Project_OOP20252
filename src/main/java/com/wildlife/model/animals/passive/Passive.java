package com.wildlife.model.animals.passive;

import com.wildlife.model.animals.Animal;
import com.wildlife.constant.*;
import com.wildlife.view.SpriteManager;
import com.wildlife.model.strategy.*;
import com.wildlife.model.worldmap.TerrainType;
import com.wildlife.model.worldmap.WorldMap;
import java.util.Random;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
// Động vật ăn cỏ, kế thừa từ Animal
public abstract class Passive extends Animal {
    protected Random random = new Random();
    protected PassiveStrategy brain = new PassiveStrategy();
    public Passive(double x, double y) {
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
            this.setHunger(this.getHunger() - (random.nextDouble() / 5));
            this.setThirst(this.getThirst() - (random.nextDouble() / 8));
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
              mp.isObstacle(testX, testY + 30, this) ||
              mp.isObstacle(testX + 30, testY, this) ||
              mp.isObstacle(testX + 30, testY + 30, this) || testX - 10 <  0 || testX + 35 > Constants.SCREEN_WIDTH || testY - 10 < 0 || testY + 35 > Constants.SCREEN_HEIGHT) {
            
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
            // 2. LOGIC "BẦY ĐÀN" (Chỉ chạy nếu đường trống trải không có đá/nước)
            // Nếu phát hiện có đồng loại cản đường phía trước
            if (mp.isCompanion(testX, testY, this)) {
                
                // BÍ QUYẾT: KHÔNG đổi hướng (dx, dy giữ nguyên)
                // CHỈ GIẢM TỐC (Đi chậm lại 80% để chờ con phía trước đi qua)
                
                // *Lưu ý: Lead dùng this.speed hoặc Constants.FOX_SPEED tùy theo file nhé
                testX = this.getX() + this.getDx() * delta * (this.speed * 0.05); 
                testY = this.getY() + this.getDy() * delta * (this.speed * 0.05);
            }
        }
        //this.setX(Math.min(Constants.SCREEN_WIDTH - Constants.TILE_SIZE,
                //Math.max(0, this.getX() + this.getDx() * delta * this.speed)));
        //this.setY(Math.min(Constants.SCREEN_HEIGHT - Constants.TILE_SIZE,
                //Math.max(0, this.getY() + this.getDy() * delta * this.speed)));
        this.setX(testX);
        this.setY(testY);
    }
    @Override
    public void render(GraphicsContext gc, boolean isGraphic){
        super.render(gc, isGraphic);
    }
}