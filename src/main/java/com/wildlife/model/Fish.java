package com.wildlife.model;

import java.util.Random;
import com.wildlife.constant.Constants;
import com.wildlife.controller.SimulationController;
import com.wildlife.model.animals.Animal;
import com.wildlife.model.worldmap.WorldMap;
import com.wildlife.view.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Fish extends Animal {
    //private static Image fishImg; 
    private Random random = new Random();
    
    // === THAM SỐ HOẠT ẢNH SPRITESHEET ===
    private double frameTimer = 0;
    private int currentFrame = 0;
    private final int totalFrames = 4; // Giả định chuỗi ảnh động bơi có 4 khung hình theo chiều ngang

    public Fish(double x, double y) {
        super(x, y); // Định dạng chuẩn 2 tham số theo cấu trúc phân cấp của Animal
        
        this.img = SpriteManager.loadImage("fish.png");
        this.speed = 10.0; // Tốc độ bơi chậm hơn so với các loài động vật trên cạn để tạo cảm giác lừ đừ, thư thái dưới nước
        // Thiết lập hướng bơi tuần tra ngẫu nhiên thuở ban đầu
        double angle = random.nextDouble() * 2 * Math.PI;
        this.setDx(Math.cos(angle));
        this.setDy(Math.sin(angle));
    }

    @Override
    public void update(double delta, WorldMap mp) {
        // 1. Bộ đếm thời gian tự động đổi hướng bơi dạo mát
        setInnerDirectionTime(getInnerDirectionTime() + delta);
        if (getInnerDirectionTime() > Constants.DIRECTION_UPDATE_INTERVAL) {
            setInnerDirectionTime(0);
            double angle = random.nextDouble() * 2 * Math.PI;
            setDx(Math.cos(angle));
            setDy(Math.sin(angle));
        }

        // 2. Cập nhật khung hình hoạt ảnh chuyển động cuốn chiếu
        frameTimer += delta;
        if (frameTimer > 0.12) { 
            frameTimer = 0;
            currentFrame = (currentFrame + 1) % totalFrames;
        }

        // 3. Dự toán tọa độ bước di chuyển kế tiếp
        double nextX = getX() + getDx() * getSpeed() * delta;
        double nextY = getY() + getDy() * getSpeed() * delta;

        // 4. Xử lý va chạm thông minh (Trượt dọc bờ thay vì dội ngược)
        boolean canMoveX = isValidWaterPosition(nextX, getY());
        boolean canMoveY = isValidWaterPosition(getX(), nextY);

        if (canMoveX) {
            setX(nextX);
        } else {
            // Húc bờ trục X -> Đảo chiều X để cá trượt dọc theo trục Y
            setDx(-getDx());
            setInnerDirectionTime(Constants.DIRECTION_UPDATE_INTERVAL); 
        }

        if (canMoveY) {
            setY(nextY);
        } else {
            // Húc bờ trục Y -> Đảo chiều Y để cá trượt dọc theo trục X
            setDy(-getDy());
            setInnerDirectionTime(Constants.DIRECTION_UPDATE_INTERVAL);
        }
    }

    // ====================================================================
    // HÀM TIỆN ÍCH: KIỂM TRA ĐỊA HÌNH & RANH GIỚI BẢN ĐỒ
    // ====================================================================

    /**
     * Kiểm tra xem toàn bộ thân cá (cả 4 góc) có nằm an toàn dưới nước không.
     */
    private boolean isValidWaterPosition(double x, double y) {
        double fishWidth = 24.0; // Chiều rộng của cá (destW)
        double fishHeight = 24.0; // Chiều cao của cá (destH)

        // Phải đảm bảo không có góc nào của cá chạm vào bờ
        return isWaterTile(x, y) &&                             // Góc trên-trái
               isWaterTile(x + fishWidth, y) &&                 // Góc trên-phải
               isWaterTile(x, y + fishHeight) &&                // Góc dưới-trái
               isWaterTile(x + fishWidth, y + fishHeight);      // Góc dưới-phải
    }

    /**
     * Kiểm tra 1 điểm tọa độ cụ thể có nằm trong ranh giới map và là ô nước không.
     */
    private boolean isWaterTile(double px, double py) {
        int rx = (int) (px / Constants.TILE_SIZE);
        int ry = (int) (py / Constants.TILE_SIZE);
        
        // 1. Out of Bounds Check: Ngăn cá bơi ra khỏi màn hình / mảng dữ liệu
        if (rx <= 0 || rx >= Constants.MAP_WIDTH || ry <= 0 || ry >= Constants.MAP_HEIGHT ) {
            return false; 
        }
        
        // 2. Out of Water Check: Xác nhận ô lưới hiện tại là Nước (Mã ID: 1)
        return com.wildlife.model.worldmap.MatrixManager.MAP_LAYOUT[ry][rx] == 1;
    }

    @Override
    public void render(GraphicsContext gc, boolean isGraphic) {
        if (SimulationController.zoomLevel < 1.3){
            return;
        }
        if (Constants.BASIC_VIEW) {
            // Dự phòng chế độ dựng hình thô (Đồ họa hình khối cơ bản)
            gc.setFill(javafx.scene.paint.Color.ORANGE);
            gc.fillOval(getX(), getY(), 18, 18);
            return;
        }

        // Vẽ bóng đổ trong suốt nhẹ phía dưới bụng cá tạo độ chìm sâu dưới mặt nước
        gc.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.15));
        gc.fillOval(getX() + 4, getY() + 18, 18, 5);

        // Khai báo số hàng để tính toán chiều cao chính xác
        int totalRows = 4; 

        // Tính toán kích thước của 1 frame đơn lẻ trên spritesheet 4x4
        int frameWidth = (int) (img.getWidth() / totalFrames);
        int frameHeight = (int) (img.getHeight() / totalRows); 
        
        // Toạ độ điểm cắt trên Spritesheet
        int srcX = currentFrame * frameWidth;
        // Sử dụng hàng 0 (trên cùng) cho hoạt ảnh bơi bình thường
        // (Có thể đổi thành 1 * frameHeight cho bơi nhanh, 3 * frameHeight cho ngủ, v.v.)
        int srcY = 0 * frameHeight; 

        // Kích thước co giãn hiển thị thực tế trên lưới bản đồ game
        int destW = 24; 
        int destH = 24;

        if (getDx() < 0) {
            // Đảo chiều trục ngang vẽ gương (Mirror Flip) để cá quay đầu sang trái khi bơi ngược dòng
            gc.drawImage(img, 
                srcX, srcY, frameWidth, frameHeight, 
                getX() + destW, getY(), -destW, destH
            );
        } else {
            // Cá bơi sang phải -> Vẽ ảnh thuận chiều gốc
            gc.drawImage(img, 
                srcX, srcY, frameWidth, frameHeight, 
                getX(), getY(), destW, destH
            );
        }
    }

    // ====================================================================
    // HÀM TIỆN ÍCH: TRÍCH XUẤT MA TRẬN ĐỊA HÌNH
    // ====================================================================
}