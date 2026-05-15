package com.wildlife.model.animals;

import com.wildlife.constant.Constants;
import com.wildlife.model.BaseEntity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Animal extends BaseEntity {
    private double hunger = 100;
    private double thirst = 100;
    private double tiredness = 100;
    private double dx = 0;
    private double dy = 0;
    private double innerTime = 0;
    private double innerDirectionTime = 0;
    protected int avoidanceTimer = 0;
    protected double speed = 0;
    public void setInnerTime(double innerTime) {
        this.innerTime = innerTime;
    }

    public void setInnerDirectionTime(double innerDirectionTime) {
        this.innerDirectionTime = innerDirectionTime;
    }

    public double getInnerTime() {
        return innerTime;
    }

    public double getInnerDirectionTime() {
        return this.innerDirectionTime;
    }

    public double getDx() {
        return dx;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    public double getHunger() {
        return hunger;
    }

    public void setHunger(double hunger) {
        this.hunger = hunger;
    }

    public Animal(double x, double y) {
        super(x, y);
    }
    public int getAvoidanceTimer() {
        return avoidanceTimer;
    }
    public void setAvoidanceTimer(int avoidanceTimer) {
        this.avoidanceTimer = avoidanceTimer;
    }
    public double getThirst() {
        return thirst;
    }

    public void setThirst(double thirst) {
        this.thirst = thirst;
    }

    public double getTiredness() {
        return tiredness;
    }

    public void setTiredness(double tiredness) {
        this.tiredness = tiredness;
    }
    @Override
    public void render(GraphicsContext gc, boolean isGraphic){
        // ==========================================
        // 2. VẼ THANH HUNGER (KIỂU LIÊN MINH HUYỀN THOẠI)
        // ==========================================
        double maxHunger = 100.0; 
        double currentHunger = this.getHunger(); // Lấy từ lớp cha Animal
        
        // Cấu hình kích thước thanh (Rộng bằng con vật, cao 6 pixel)
        double barWidth = Constants.TILE_SIZE; 
        double barHeight = 6.0; 
        
        // Đặt tọa độ (Ngay trên đầu con vật 10 pixel)
        double barX = getX();
        double barY = getY() - 10; 

        // Bước 2.1: Vẽ nền thanh (Background - Màu xám tối)
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(barX, barY, barWidth, barHeight);

        // Bước 2.2: Tính toán độ dài của thanh lõi hiện tại
        double currentWidth = (currentHunger / maxHunger) * barWidth;
        currentWidth = Math.max(0, Math.min(barWidth, currentWidth)); // Tránh bị vẽ âm hoặc tràn viền

        // Bước 2.3: Đổi màu cảnh báo theo mức độ đói
        if (currentHunger > 60) {
            gc.setFill(Color.LIGHTGREEN); // No: Xanh lá
        } else if (currentHunger > 30) {
            gc.setFill(Color.ORANGE);     // Hơi đói: Vàng cam
        } else {
            gc.setFill(Color.RED);        // Sắp chết đói: Đỏ
        }

        // Vẽ phần lõi đè lên nền
        gc.fillRect(barX, barY, currentWidth, barHeight);

        // Bước 2.4: Vẽ viền đen bên ngoài cho sắc nét
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);
        gc.strokeRect(barX, barY, barWidth, barHeight);

        // Bước 2.5: Vẽ các vạch chia (Segments) giống LMHT (Ví dụ chia làm 4 nấc, mỗi nấc 25 điểm)
        int segments = 8;
        double segmentWidth = barWidth / segments;
        for (int i = 1; i < segments; i++) {
            double lineX = barX + (segmentWidth * i);
            gc.strokeLine(lineX, barY, lineX, barY + barHeight);
        }


        double maxThirst = 100.0; 
        double currentThirst = this.getThirst(); // Lấy từ lớp cha Animal
        
        // Cấu hình kích thước thanh (Rộng bằng con vật, cao 6 pixel)
        double barHeight2 = 3.0; 
        
        // Đặt tọa độ (Ngay trên đầu con vật 10 pixel)
        double thirstBarX = getX();
        double thirstBarY = getY() - 5; 

        // Bước 2.1: Vẽ nền thanh (Background - Màu xám tối)
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(thirstBarX, thirstBarY, barWidth, barHeight2);

        // Bước 2.2: Tính toán độ dài của thanh lõi hiện tại
        double currentThirstWidth = (currentThirst / maxThirst) * barWidth;
        currentThirstWidth = Math.max(0, Math.min(barWidth, currentThirstWidth)); // Tránh bị vẽ âm hoặc tràn viền

        // Bước 2.3: Đổi màu cảnh báo theo mức độ đói
        if (currentThirst > 60) {
            gc.setFill(Color.LIGHTBLUE); // No: Xanh lá
        } else if (currentThirst > 30) {
            gc.setFill(Color.BLUE);     // Hơi đói: Vàng cam
        } else {
            gc.setFill(Color.BLUE);        // Sắp chết đói: Đỏ
        }

        // Vẽ phần lõi đè lên nền
        gc.fillRect(thirstBarX, thirstBarY, currentThirstWidth, barHeight2);

        // Bước 2.4: Vẽ viền đen bên ngoài cho sắc nét
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);
        gc.strokeRect(thirstBarX, thirstBarY, barWidth, barHeight2);

        // Bước 2.5: Vẽ các vạch chia (Segments) giống LMHT (Ví dụ chia làm 4 nấc, mỗi nấc 25 điểm)
        segments = 1;
        segmentWidth = barWidth / segments;
        for (int i = 1; i < segments; i++) {
            double lineX = thirstBarX + (segmentWidth * i);
            gc.strokeLine(lineX, thirstBarX, lineX, thirstBarY + barHeight2);
        }
    }
}
