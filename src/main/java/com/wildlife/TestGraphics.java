package com.wildlife;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class TestGraphics extends Application {
    private double x = 0; // Tọa độ X của con vật

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 1. Nạp ảnh từ thư mục resources
        // Lưu ý: Dấu "/" ở đầu cực kỳ quan trọng để Maven tìm đúng folder resources
        Image sprite = new Image(getClass().getResourceAsStream("/images/akamesc.jpg"));

        // 2. Tạo vòng lặp Simulation (60 khung hình/giây)
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Xóa màn hình cũ để vẽ khung hình mới
                gc.clearRect(0, 0, 800, 600);

                // Cập nhật logic di chuyển (BioLogic đơn giản)
                x += 2; 
                if (x > 800) x = -50; // Chạy hết màn hình thì quay lại

                // Vẽ Sprite lên màn hình (ViewLogic)
                gc.drawImage(sprite, x, 250, 50, 50); // Vẽ tại x, y với size 50x50
            }
        }.start();

        primaryStage.setScene(new Scene(new Group(canvas)));
        primaryStage.setTitle("Test Sprite Movement");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}