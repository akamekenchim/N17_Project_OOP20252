package com.wildlife;

import com.wildlife.constant.Constants;
import com.wildlife.controller.InputController;
import com.wildlife.controller.SimulationController;
import com.wildlife.model.worldmap.WorldMap;
import com.wildlife.view.MapRenderer;
import com.wildlife.view.SpriteManager; 
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import javafx.application.Application;
import javafx.geometry.Insets; // THÊM MỚI: Để set Margin cho nút
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane; // THÊM MỚI: Layout xếp chồng (bánh kẹp)
import javafx.stage.Stage;

public class AppRunner extends Application {
    private MediaPlayer bgmPlayer;
    @Override
    public void start(Stage primaryStage) {

        try {
            // Lấy đường dẫn file nhạc từ thư mục resources/sounds/
            URL bgmUrl = getClass().getResource("/sounds/" + Constants.BGM_WELCOME);
            if (bgmUrl != null) {
                Media bgmMedia = new Media(bgmUrl.toString());
                bgmPlayer = new MediaPlayer(bgmMedia);
                
                // Lặp nhạc vô hạn giống HSR
                bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE); 
                bgmPlayer.setVolume(0.1); // Chỉnh âm lượng vừa phải (50%)
                bgmPlayer.play(); // Bắt đầu phát ngay khi mở app
            }
        } catch (Exception e) {
            System.out.println("Không thể phát nhạc nền Welcome: " + e.getMessage());
        }
        
        // ==========================================
        // 1. SETUP MÀN HÌNH CHÍNH (MAIN SCENE - CHƯA CHẠY)
        // ==========================================
        Canvas cv = new Canvas(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT); 
        GraphicsContext gc = cv.getGraphicsContext2D(); 

        WorldMap map = new WorldMap();
        MapRenderer WR = new MapRenderer();
        WR.generateMapCache();
        SimulationController GenG = new SimulationController(map, gc, WR);
        
        Group gr = new Group(cv);
        Scene mainScene = new Scene(gr, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        InputController.StartListening(mainScene, map);
        
        // LƯU Ý QUAN TRỌNG: Ở ĐÂY TA BỎ GỌI GenG.Start() VÌ CHƯA MUỐN VÀO GAME NGAY


        // ==========================================
        // 2. SETUP MÀN HÌNH CHÀO MỪNG (WELCOME SCENE)
        // ==========================================
        // Dùng StackPane để các thành phần xếp đè lên nhau (Ảnh dưới, nút trên)
        StackPane welcomeLayout = new StackPane(); 

        // 2.1 Load ảnh Welcome làm nền (Lớp dưới cùng)
        try {
            Image welcomeImage = SpriteManager.loadImage("welcome_screen.png"); // Nhớ để ảnh vào assets
            ImageView welcomeImageView = new ImageView(welcomeImage);
            
            // Ép ảnh phủ kín toàn bộ màn hình
            welcomeImageView.setFitWidth(Constants.SCREEN_WIDTH);
            welcomeImageView.setFitHeight(Constants.SCREEN_HEIGHT);
            
            // Thêm ảnh vào layout (Ảnh vào trước -> Nằm dưới)
            welcomeLayout.getChildren().add(welcomeImageView);
        } catch (Exception e) {
            System.out.println("Không tìm thấy ảnh Welcome, nền sẽ trống.");
            welcomeLayout.setStyle("-fx-background-color: #1a1a2e;"); 
        }

        // 2.2 Tạo nút START (Play) với style Honkai Star Rail
        Button startButton = new Button("Play");
        
        // CSS định dạng nút giống ảnh mẫu
        String buttonNormalStyle = 
            "-fx-background-color: #e8e8e8; " +         // Nền màu xám nhạt/trắng đục
            "-fx-background-radius: 30; " +             // BÍ QUYẾT BO TRÒN: Radius lớn tạo hình viên thuốc
            "-fx-border-radius: 30; " +                 // Bo tròn cả đường viền
            "-fx-border-color: #b0b0b0; " +             // Viền xám đậm hơn nền một chút
            "-fx-border-width: 1.5; " +                 // Độ dày viền
            "-fx-text-fill: #333333; " +                // Chữ màu xám đen, dễ đọc trên nền sáng
            "-fx-font-size: 18px; " +                   // Cỡ chữ thu nhỏ lại (bé hơn nút Play cũ)
            "-fx-font-weight: bold; " +                 // Chữ in đậm
            "-fx-padding: 8 60 8 60; " +                // Đệm trên/dưới 8px, trái/phải 60px để nút kéo dài
            "-fx-cursor: hand;";
            
        String buttonHoverStyle = 
            "-fx-background-color: #ffffff; " +         // Khi lia chuột vào: Nền sáng hẳn lên màu trắng
            "-fx-background-radius: 30; " +
            "-fx-border-radius: 30; " +
            "-fx-border-color: #888888; " +             // Viền đậm hơn để nổi bật
            "-fx-border-width: 1.5; " +
            "-fx-text-fill: #000000; " +                // Chữ đen tuyền
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8 60 8 60; " +
            "-fx-cursor: hand;";

        startButton.setStyle(buttonNormalStyle);

        // Hiệu ứng di chuột (Hover) để trải nghiệm giống game xịn
        startButton.setOnMouseEntered(e -> startButton.setStyle(buttonHoverStyle));
        startButton.setOnMouseExited(e -> startButton.setStyle(buttonNormalStyle));

        // 2.3 Gắn sự kiện khi BẤM NÚT START
        startButton.setOnAction(event -> {
            if (bgmPlayer != null) {
                bgmPlayer.stop();
                bgmPlayer.dispose(); // Giải phóng RAM
            }
            primaryStage.setScene(mainScene); // Chuyển cửa sổ sang màn hình game chính
            GenG.Start(); // Kích hoạt logic game
        });

        // 2.4 Thêm nút vào layout (Nút vào sau -> Nằm đè lên trên ảnh)
        welcomeLayout.getChildren().add(startButton);
        
        // Căn chỉnh vị trí của nút (Đẩy xuống nửa dưới màn hình)
        StackPane.setAlignment(startButton, Pos.BOTTOM_CENTER);
        // Cách đáy 150 pixel để chừa khoảng trống bên dưới giống ảnh HSR
        StackPane.setMargin(startButton, new Insets(0, 0, 150, 0)); 

        Scene welcomeScene = new Scene(welcomeLayout, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);


        // ==========================================
        // 3. HIỂN THỊ CỬA SỔ ĐẦU TIÊN (WELCOME)
        // ==========================================
        primaryStage.setScene(welcomeScene); // Ép Stage hiển thị Welcome Scene trước
        primaryStage.setTitle("KénChim đáng yêu");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
/*
package com.wildlife;

import com.wildlife.constant.Constants;
import com.wildlife.controller.InputController;
import com.wildlife.controller.SimulationController;
import com.wildlife.model.worldmap.WorldMap;
import com.wildlife.view.MapRenderer;

//import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.layout.StackPane;
//import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AppRunner extends Application {
    @Override
    public void start(Stage primaryStage) {
        Canvas cv = new Canvas(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT); // tạo canvas hoàn toàn trống
        GraphicsContext gc = cv.getGraphicsContext2D(); // Lấy graphicscontext của canvas vừa tạo

        //Canvas cv_Background = new Canvas(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        // GraphicsContext gc2 = cv_Background.getGraphicsContext2D();

        WorldMap map = new WorldMap();
        MapRenderer WR = new MapRenderer();
        WR.generateMapCache();
        SimulationController GenG = new SimulationController(map, gc, WR);
        // WorldRender.renderAll(gc2);
        GenG.Start();
        // Trong hàm start này có 3 việc: Xóa màn, update trạng thái của WorldMap, rồi
        // render các thực thể
        // StackPane root = new StackPane(cv_Background, cv);
        Group gr = new Group(cv);
        Scene scene = new Scene(gr, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

        primaryStage.setScene(scene);
        primaryStage.setTitle("KénChim đáng yêu");
        InputController.StartListening(scene, map);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
*/