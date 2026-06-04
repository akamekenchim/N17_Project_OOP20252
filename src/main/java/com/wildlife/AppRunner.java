package com.wildlife;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

// IMPORT MODEL & CONTROLLER
import com.wildlife.constant.Constants;
import com.wildlife.controller.InputController;
import com.wildlife.controller.SimulationController;
import com.wildlife.model.worldmap.WorldMap;
import com.wildlife.view.MapRenderer;
import com.wildlife.view.SpriteManager;
import com.wildlife.model.BaseEntity;

// IMPORT JAVAFX CORE & UI
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

// IMPORT THỜI GIAN & TIỆN ÍCH
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.net.URL;

public class AppRunner extends Application {
    private MediaPlayer bgmPlayer;

    @Override
    public void start(Stage primaryStage) {
        
        // ==========================================
        // 0. KHỞI TẠO NHẠC NỀN (BGM)
        // ==========================================
        try {
            URL bgmUrl = getClass().getResource("/sounds/" + Constants.BGM_WELCOME);
            if (bgmUrl != null) {
                Media bgmMedia = new Media(bgmUrl.toString());
                bgmPlayer = new MediaPlayer(bgmMedia);
                bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                bgmPlayer.setVolume(0.1);
                bgmPlayer.play();
            }
        } catch (Exception e) {
            System.out.println("Không thể phát nhạc nền Welcome: " + e.getMessage());
        }

        // ==========================================
        // 1. SETUP MÀN HÌNH CHÍNH (MAIN SCENE)
        // ==========================================
        Canvas cv = new Canvas(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        GraphicsContext gc = cv.getGraphicsContext2D();

        WorldMap map = new WorldMap();
        MapRenderer WR = new MapRenderer();
        WR.generateMapCache();
        SimulationController gameSimulator = new SimulationController(map, gc, WR);

        StackPane rightPanel = createDashboardPanel(map);

        HBox mainLayout = new HBox(cv, rightPanel);
        mainLayout.setStyle("-fx-background-color: #1a1a2e;");

        Scene mainScene = new Scene(mainLayout, Constants.SCREEN_WIDTH + 300, Constants.SCREEN_HEIGHT);
        InputController.StartListening(mainScene, map);

        // ==========================================
        // 2. SETUP MÀN HÌNH CHÀO MỪNG (WELCOME SCENE)
        // ==========================================
        StackPane welcomeLayout = new StackPane();

        try {
            Image welcomeImg = SpriteManager.loadImage("genshinbg.png"); 
            ImageView welcomeView = new ImageView(welcomeImg);
            welcomeView.setFitWidth(Constants.SCREEN_WIDTH + 300);
            welcomeView.setFitHeight(Constants.SCREEN_HEIGHT);
            welcomeView.setPreserveRatio(false);
            welcomeLayout.getChildren().add(welcomeView);
        } catch (Exception e) {
            welcomeLayout.setStyle("-fx-background-color: #1a1a2e;");
        }

        Region welcomeOverlay = new Region();
        welcomeOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");
        welcomeOverlay.setPrefSize(Constants.SCREEN_WIDTH + 300, Constants.SCREEN_HEIGHT);
        welcomeLayout.getChildren().add(welcomeOverlay);

        // ==========================================
        // 🌟 TẠO 2 NÚT BẤM (TRỐNG VÀ CÓ SẴN THÚ)
        // ==========================================
        String buttonNormalStyle = "-fx-background-color: #e8e8e8; -fx-background-radius: 30; -fx-border-radius: 30; -fx-border-color: #b0b0b0; -fx-border-width: 1.5; -fx-text-fill: #333333; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30 10 30; -fx-cursor: hand;";
        String buttonHoverStyle = "-fx-background-color: #ffffff; -fx-background-radius: 30; -fx-border-radius: 30; -fx-border-color: #888888; -fx-border-width: 1.5; -fx-text-fill: #000000; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30 10 30; -fx-cursor: hand;";

        // Nút 1: Chơi bản đồ trống
        Button startButton = new Button("Play (Bản đồ trống)");
        startButton.setStyle(buttonNormalStyle);
        startButton.setOnMouseEntered(e -> startButton.setStyle(buttonHoverStyle));
        startButton.setOnMouseExited(e -> startButton.setStyle(buttonNormalStyle));
        startButton.setOnAction(event -> {
            if (bgmPlayer != null) { bgmPlayer.stop(); bgmPlayer.dispose(); }
            primaryStage.setScene(mainScene);
            gameSimulator.Start(); 
        });

        // Nút 2: Quick Start (Tạo sẵn hệ sinh thái)
        Button quickStartButton = new Button("🚀 Quick Start (Hệ sinh thái mẫu)");
        String highlightNormal = "-fx-background-color: #ffcc00; -fx-background-radius: 30; -fx-border-radius: 30; -fx-border-color: #b38f00; -fx-border-width: 1.5; -fx-text-fill: #333333; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30 10 30; -fx-cursor: hand;";
        String highlightHover = "-fx-background-color: #ffe680; -fx-background-radius: 30; -fx-border-radius: 30; -fx-border-color: #b38f00; -fx-border-width: 1.5; -fx-text-fill: #000000; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 30 10 30; -fx-cursor: hand;";
        quickStartButton.setStyle(highlightNormal);
        quickStartButton.setOnMouseEntered(e -> quickStartButton.setStyle(highlightHover));
        quickStartButton.setOnMouseExited(e -> quickStartButton.setStyle(highlightNormal));
        quickStartButton.setOnAction(event -> {
            if (bgmPlayer != null) { bgmPlayer.stop(); bgmPlayer.dispose(); }
            
            // Gọi hàm sinh thái mẫu trước khi chạy
            prePopulateMap(map);
            
            primaryStage.setScene(mainScene);
            gameSimulator.Start(); 
        });

        // Nhóm 2 nút vào một HBox (Xếp theo chiều ngang)
        HBox buttonLayout = new HBox(30); // Khoảng cách 30px giữa 2 nút
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.getChildren().addAll(startButton, quickStartButton);

        welcomeLayout.getChildren().add(buttonLayout);
        StackPane.setAlignment(buttonLayout, Pos.BOTTOM_CENTER);
        StackPane.setMargin(buttonLayout, new Insets(0, 0, 150, 0));

        Scene welcomeScene = new Scene(welcomeLayout, Constants.SCREEN_WIDTH + 300, Constants.SCREEN_HEIGHT);

        // ==========================================
        // 3. HIỂN THỊ CỬA SỔ
        // ==========================================
        primaryStage.iconifiedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { 
                System.out.println("[System] The window has been minimized. Pausing simulation...");
                gameSimulator.stop(); 
            } else { 
                System.out.println("[System] The window has been restored. Resuming simulation...");
                gameSimulator.Start(); 
            }
        });

        primaryStage.setScene(welcomeScene);
        primaryStage.setTitle("KénChim đáng yêu - Wildlife Eco Simulator");
        primaryStage.show();
    }

    // ====================================================================
    // 🌟 KHỐI LOGIC: KHỞI TẠO HỆ SINH THÁI MẪU (PRESET SCENARIO)
    // ====================================================================
    private void prePopulateMap(WorldMap map) {
        System.out.println("[System] Đang khởi tạo hệ sinh thái mẫu...");

        // (1) 4 Thỏ ở góc trên bên trái (X: 2->12, Y: 2->10)
        spawnEntitiesInArea(map, "RABBIT", 5, 2, 12, 2, 10);
        
        // (2) 4 Hươu ở vùng rừng góc dưới trái (X: 2->15, Y: 15->24)
        spawnEntitiesInArea(map, "DEER", 5, 2, 15, 15, 24);
        
        // (3) 4 Sói và 2 Hổ ở góc phải bản đồ (X: 22->34, Y: 5->20)
        spawnEntitiesInArea(map, "WOLF", 4, 22, 34, 5, 20);
        spawnEntitiesInArea(map, "TIGER", 2, 22, 34, 5, 20);
        
        // (4) 20 Ngọn cỏ rải rác toàn bản đồ (Nhưng vẫn phải tuân thủ né Nước và mép)
        // Giả sử lưới ma trận rộng khoảng 37x26 (theo InputController)
        spawnEntitiesInArea(map, "GRASS", 20, 2, 34, 2, 23);
        
        System.out.println("[System] Đã tạo xong hệ sinh thái mẫu!");
    }

    /**
     * Hàm sinh thực thể vào một khu vực (Bounding Box) chỉ định
     * Đảm bảo không đè lên Nước và không dính sát viền bản đồ
     */
    private void spawnEntitiesInArea(WorldMap map, String type, int count, int minTileX, int maxTileX, int minTileY, int maxTileY) {
        Random rand = new Random();
        int spawned = 0;
        int attempts = 0; // Tránh vòng lặp vô hạn nếu map quá chật
        
        while(spawned < count && attempts < 500) {
            int tileX = minTileX + rand.nextInt(maxTileX - minTileX + 1);
            int tileY = minTileY + rand.nextInt(maxTileY - minTileY + 1);

            // Kiểm tra an toàn: Không nằm sát mép (mép = 0)
            if (tileX > 1 && tileY > 1) {
                // Kiểm tra loại địa hình: Chỉ đặt nếu là Đất/Cỏ (Mã = 0)
                if (com.wildlife.model.worldmap.MatrixManager.MAP_LAYOUT[tileY][tileX] == 0) {
                    
                    double pixelX = tileX * Constants.TILE_SIZE;
                    double pixelY = tileY * Constants.TILE_SIZE;
                    BaseEntity entity = null;

                    switch(type) {
                        case "RABBIT": entity = new com.wildlife.model.animals.passive.Rabbit(pixelX, pixelY); break;
                        case "DEER":   entity = new com.wildlife.model.animals.passive.Deer(pixelX, pixelY); break;
                        case "WOLF":   entity = new com.wildlife.model.animals.predator.Wolf(pixelX, pixelY); break;
                        case "TIGER":  entity = new com.wildlife.model.animals.predator.Tiger(pixelX, pixelY); break;
                        case "GRASS":  entity = new com.wildlife.model.plants.Grass(pixelX, pixelY, 0); break;
                    }

                    if (entity != null) {
                        map.addEntity(entity);
                        spawned++;
                    }
                }
            }
            attempts++;
        }
    }
    // ====================================================================

    // ====================================================================
    // 🌟 BẢNG ĐIỀU KHIỂN: NỀN ẢNH, SPAWNER, BỘ ĐẾM, SLIDER
    // ====================================================================
    private StackPane createDashboardPanel(WorldMap map) {
        StackPane hudRoot = new StackPane();
        hudRoot.setPrefWidth(300);

        try {
            Image hudBgImage = SpriteManager.loadImage("hud_bg.png");
            ImageView hudBgView = new ImageView(hudBgImage);
            hudBgView.setFitWidth(300);
            hudBgView.setFitHeight(Constants.SCREEN_HEIGHT);
            hudBgView.setPreserveRatio(false);
            hudRoot.getChildren().add(hudBgView);
        } catch (Exception e) {
            hudRoot.setStyle("-fx-background-color: #2a2a40;");
        }

        VBox contentContainer = new VBox(10);
        contentContainer.setPadding(new Insets(20, 20, 20, 20));
        contentContainer.setStyle("-fx-background-color: rgba(30, 30, 45, 0.5);"); 

        Label titleLabel = new Label("HỆ THỐNG GIÁM SÁT");
        titleLabel.setTextFill(javafx.scene.paint.Color.GOLD);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        // KHU VỰC 1: THỐNG KÊ
        VBox statsBox = new VBox(5);
        statsBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6); -fx-padding: 10; -fx-background-radius: 8;");
        Label seasonLabel = new Label("☀️ Mùa hiện tại: Mùa Hè");
        Label statsLabel = new Label("🐾 Đang thu thập dữ liệu...");
        seasonLabel.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 13px; -fx-font-weight: bold;");
        statsLabel.setStyle("-fx-text-fill: #a0ffd0; -fx-font-size: 13px; -fx-font-weight: bold;");
        statsBox.getChildren().addAll(seasonLabel, statsLabel);

        // Ghi Log CSV
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String logFilePath = "logs/simulation_" + timeStamp + ".csv";
        File logDir = new File("logs");
        if (!logDir.exists()) { logDir.mkdirs(); }
        try (PrintWriter pw = new PrintWriter(new FileWriter(logFilePath, true))) {
            pw.println("ThoiGian_Giay,SoCo,Tho_AnCo,Cao_DocHanh,Soi_SanMoi,Ca");
        } catch (Exception e) {}
        int[] elapsedSeconds = {0}; 

        Timeline telemetryTimeline = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
            int countPassive = 0, countPredator = 0, countAggressive = 0, countGrass = 0, countFish = 0;
            for (BaseEntity e : map.getEntity()) {
                if (e.isAlive()) {
                    if (e instanceof com.wildlife.model.animals.passive.Passive) countPassive++;
                    else if (e instanceof com.wildlife.model.animals.predator.Predator) countPredator++;
                    else if (e instanceof com.wildlife.model.animals.aggressive.Aggressive) countAggressive++;
                    else if (e instanceof com.wildlife.model.plants.Grass) countGrass++;
                    else if (e instanceof com.wildlife.model.Fish) countFish++;
                }
            }
            statsLabel.setText(
                "🌿 Tổng số Cỏ: " + countGrass + "\n" +
                "🐇 Động vật ăn cỏ: " + countPassive + "\n" +
                "🦊 Thú dữ độc hành: " + countAggressive + "\n" +
                "🐺 Thú săn mồi bầy: " + countPredator + "\n" + 
                "🐟 Số lượng Cá: " + countFish
            );

            elapsedSeconds[0] += 10;
            try (PrintWriter pw = new PrintWriter(new FileWriter(logFilePath, true))) {
                pw.println(elapsedSeconds[0] + "," + countGrass + "," + countPassive + "," + countAggressive + "," + countPredator + "," + countFish);
            } catch (Exception e) {}
        }));
        telemetryTimeline.setCycleCount(Timeline.INDEFINITE);
        telemetryTimeline.play();

        // KHU VỰC 2: CÔNG CỤ CLICK CHUỘT
        Label mouseTitle = new Label("🖱️ CÔNG CỤ ĐẶT CHUỘT");
        mouseTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label currentToolLabel = new Label("Đang chọn: ❌ Hủy (Không đặt)");
        currentToolLabel.setStyle("-fx-text-fill: #ffd700; -fx-font-size: 12px; -fx-font-style: italic;");
        FlowPane toolPane = new FlowPane(5, 5); 
        toolPane.getChildren().addAll(
            createToolButton("❌ Hủy", -2, currentToolLabel),
            createToolButton("🌱 Cỏ", 1, currentToolLabel),
            createToolButton("🪨 Đá", 0, currentToolLabel),
            createToolButton("🐇 Thỏ", 4, currentToolLabel),
            createToolButton("🦌 Hươu", 2, currentToolLabel),
            createToolButton("🐺 Sói", 3, currentToolLabel),
            createToolButton("🦊 Cáo", 5, currentToolLabel),
            createToolButton("🐅 Hổ", 6, currentToolLabel),
            createToolButton("🧍 Người", 7, currentToolLabel),
            createToolButton("🐟 Cá", 8, currentToolLabel)
        );

        // KHU VỰC 3: SLIDERS ĐIỀU KHIỂN HỆ THỐNG
        Label sysTitle = new Label("⚙️ ĐIỀU KHIỂN HỆ THỐNG");
        sysTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-weight: bold;");
        sysTitle.setPadding(new Insets(10, 0, 0, 0));
        
        Label speedLabel = new Label("⚡ Tốc độ thời gian (1.0x)");
        speedLabel.setStyle("-fx-text-fill: #a0c0ff; -fx-font-size: 12px;");
        Slider speedSlider = new Slider(0.1, 5.0, Constants.SIM_SPEED);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(1.0);
        speedSlider.setFocusTraversable(false);
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            Constants.SIM_SPEED = newVal.doubleValue();
            speedLabel.setText(String.format("⚡ Tốc độ thời gian (%.1fx)", newVal.doubleValue()));
        });

        Label zoomLabel = new Label("🔍 Độ thu phóng (1.0x)");
        zoomLabel.setStyle("-fx-text-fill: #a0c0ff; -fx-font-size: 12px;");
        Slider zoomSlider = new Slider(0.5, 3.0, SimulationController.zoomLevel);
        zoomSlider.setShowTickMarks(true);
        zoomSlider.setMajorTickUnit(0.5);
        zoomSlider.setFocusTraversable(false);
        zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            SimulationController.zoomLevel = newVal.doubleValue();
            zoomLabel.setText(String.format("🔍 Độ thu phóng (%.1fx)", newVal.doubleValue()));
        });

        Button btnSeason = createStyledButton("❄️ Đổi Mùa");
        btnSeason.setOnAction(e -> {
            map.isWinter = !map.isWinter;
            seasonLabel.setText(map.isWinter ? "❄️ Mùa hiện tại: Mùa Đông" : "☀️ Mùa hiện tại: Mùa Hè");
        });

        contentContainer.getChildren().addAll(
            titleLabel, statsBox, 
            mouseTitle, currentToolLabel, toolPane, 
            sysTitle, speedLabel, speedSlider, zoomLabel, zoomSlider, btnSeason
        );
        hudRoot.getChildren().add(contentContainer);

        return hudRoot;
    }

    private Button createToolButton(String text, int animalType, Label statusLabel) {
        Button btn = new Button(text);
        btn.setFocusTraversable(false);
        String normalStyle = "-fx-background-color: #4a4a6a; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 6 10; -fx-background-radius: 5;";
        String hoverStyle = "-fx-background-color: #6a6a8a; -fx-text-fill: #ffd700; -fx-font-size: 11px; -fx-padding: 6 10; -fx-background-radius: 5; -fx-cursor: hand;";
        btn.setStyle(normalStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(normalStyle));
        btn.setOnAction(e -> {
            InputController.typeAnimal = animalType;
            statusLabel.setText("Đang chọn: " + text + " (Click để đặt)");
        });
        return btn;
    }

    private Button createStyledButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setFocusTraversable(false); 
        String normalStyle = "-fx-background-color: #3b3b55; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10;";
        String hoverStyle = "-fx-background-color: #555577; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10; -fx-cursor: hand;";
        btn.setStyle(normalStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(normalStyle));
        return btn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}