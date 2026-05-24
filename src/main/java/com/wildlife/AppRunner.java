package com.wildlife;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        SimulationController GenG = new SimulationController(map, gc, WR);

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
            Image welcomeImg = SpriteManager.loadImage("welcome_screen.png"); 
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

        Button startButton = new Button("Play");
        String buttonNormalStyle = "-fx-background-color: #c8c4c4; -fx-background-radius: 30; -fx-border-radius: 30; -fx-border-color: #b0b0b0; -fx-border-width: 1.5; -fx-text-fill: #333333; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 8 60 8 60; -fx-cursor: hand;";
        String buttonHoverStyle = "-fx-background-color: #ffffff; -fx-background-radius: 30; -fx-border-radius: 30; -fx-border-color: #535151; -fx-border-width: 1.5; -fx-text-fill: #000000; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 8 60 8 60; -fx-cursor: hand;";

        startButton.setStyle(buttonNormalStyle);
        startButton.setOnMouseEntered(e -> startButton.setStyle(buttonHoverStyle));
        startButton.setOnMouseExited(e -> startButton.setStyle(buttonNormalStyle));

        startButton.setOnAction(event -> {
            if (bgmPlayer != null) {
                bgmPlayer.stop();
                bgmPlayer.dispose();
            }
            primaryStage.setScene(mainScene);
            GenG.Start(); 
        });

        welcomeLayout.getChildren().add(startButton);
        StackPane.setAlignment(startButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(startButton, new Insets(0, 0, 150, 0));

        Scene welcomeScene = new Scene(welcomeLayout, Constants.SCREEN_WIDTH + 300, Constants.SCREEN_HEIGHT);

        // ==========================================
        // 3. HIỂN THỊ CỬA SỔ
        // ==========================================
        primaryStage.iconifiedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { 
                System.out.println("[System] The window has been minimized. Pausing simulation to save resources...");
                GenG.stop(); 
            } else { 
                System.out.println("[System] The window has been restored. Resuming simulation...");
                GenG.Start(); 
            }
        });

        primaryStage.setScene(welcomeScene);
        primaryStage.setTitle("KénChim đáng yêu - Wildlife Eco Simulator");
        primaryStage.show();
    }

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

        VBox contentContainer = new VBox(10); // Giảm khoảng cách giữa các phần để nhét đủ control
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

        // ==========================================================
        // 🌟 [BỔ SUNG] TỰ ĐỘNG KHỞI TẠO FILE LOG EXCEL (.CSV)
        // ==========================================================
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String logFilePath = "logs/simulation_" + timeStamp + ".csv";
        File logDir = new File("logs");
        if (!logDir.exists()) {
            logDir.mkdirs(); // Tự động tạo thư mục "logs" nếu máy chưa có
        }
        
        // Ghi tiêu đề các cột (Header) cho file CSV
        try (PrintWriter pw = new PrintWriter(new FileWriter(logFilePath, true))) {
            pw.println("ThoiGian_Giay,SoCo,Tho_AnCo,Cao_DocHanh,Soi_SanMoi,Ca");
        } catch (Exception e) {
            System.out.println("[Log System] Không thể tạo file CSV: " + e.getMessage());
        }

        // Biến mảng 1 phần tử để lưu thời gian đã trôi qua (bắt buộc dùng mảng để update trong lambda)
        int[] elapsedSeconds = {0}; 
        // ==========================================================

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
            
            // Cập nhật text lên màn hình
            statsLabel.setText(
                "🌿 Tổng số Cỏ: " + countGrass + "\n" +
                "🐇 Động vật ăn cỏ: " + countPassive + "\n" +
                "🦊 Thú dữ độc hành: " + countAggressive + "\n" +
                "🐺 Thú săn mồi bầy: " + countPredator + "\n" + 
                "🐟 Số lượng Cá: " + countFish
            );

            // ==========================================================
            // 🌟 [BỔ SUNG] GHI SỐ LIỆU VÀO FILE EXCEL MỖI 10 GIÂY
            // ==========================================================
            elapsedSeconds[0] += 10;
            try (PrintWriter pw = new PrintWriter(new FileWriter(logFilePath, true))) {
                pw.println(elapsedSeconds[0] + "," + countGrass + "," + countPassive + "," + countAggressive + "," + countPredator + "," + countFish);
            } catch (Exception e) {
                // Im lặng bỏ qua nếu lỗi để không gián đoạn game
            }
        }));
        telemetryTimeline.setCycleCount(Timeline.INDEFINITE);
        telemetryTimeline.play();

        // KHU VỰC 2: CÔNG CỤ CLICK CHUỘT (THAY PHÍM BẤM)
        Label mouseTitle = new Label("🖱️ CÔNG CỤ ĐẶT CHUỘT");
        mouseTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        Label currentToolLabel = new Label("Đang chọn: ❌ Hủy (Không đặt)");
        currentToolLabel.setStyle("-fx-text-fill: #ffd700; -fx-font-size: 12px; -fx-font-style: italic;");

        FlowPane toolPane = new FlowPane(5, 5); // Tạo lưới linh hoạt cho các nút nhỏ
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

        // Slider Tốc độ
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

        // Slider Zoom
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

        // Nút Đổi Mùa
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

    // ====================================================================
    // HÀM BỔ TRỢ: TẠO NÚT NHỎ CHO CÔNG CỤ CLICK CHUỘT
    // ====================================================================
    private Button createToolButton(String text, int animalType, Label statusLabel) {
        Button btn = new Button(text);
        btn.setFocusTraversable(false);
        
        String normalStyle = "-fx-background-color: #4a4a6a; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 6 10; -fx-background-radius: 5;";
        String hoverStyle = "-fx-background-color: #6a6a8a; -fx-text-fill: #ffd700; -fx-font-size: 11px; -fx-padding: 6 10; -fx-background-radius: 5; -fx-cursor: hand;";
        
        btn.setStyle(normalStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(normalStyle));
        
        // Cập nhật loại con vật trực tiếp vào InputController khi bấm nút
        btn.setOnAction(e -> {
            InputController.typeAnimal = animalType;
            statusLabel.setText("Đang chọn: " + text + " (Click để đặt)");
            System.out.println("[Tool] Đã chuyển công cụ sang: " + text + " (ID: " + animalType + ")");
        });
        
        return btn;
    }

    // ====================================================================
    // HÀM BỔ TRỢ: TẠO STYLE CHO NÚT BẤM LỚN
    // ====================================================================
    private Button createStyledButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setFocusTraversable(false); // Tránh cướp focus phím tắt
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