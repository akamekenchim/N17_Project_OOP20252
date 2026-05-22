package com.wildlife;

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
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
import java.util.Random;

public class AppRunner extends Application {
    private MediaPlayer bgmPlayer; // Giữ lại nhạc nền BGM

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

        // ĐÃ SỬA LỖI: createDashboardPanel giờ trả về chuẩn StackPane
        StackPane rightPanel = createDashboardPanel(map);

        HBox mainLayout = new HBox(cv, rightPanel);
        mainLayout.setStyle("-fx-background-color: #1a1a2e;");

        Scene mainScene = new Scene(mainLayout, Constants.SCREEN_WIDTH + 300, Constants.SCREEN_HEIGHT);
        InputController.StartListening(mainScene, map);

        // ==========================================
        // 2. SETUP MÀN HÌNH CHÀO MỪNG BẰNG ẢNH (KHÔNG DÙNG VIDEO)
        // ==========================================
        StackPane welcomeLayout = new StackPane();

        // 2.1 Load ảnh nền Welcome (Bỏ hoàn toàn Video)
        try {
            // Thay "welcome_bg.png" bằng tên file ảnh nền bạn có trong thư mục resources/images/
            Image welcomeImg = SpriteManager.loadImage("welcome_screen.png"); 
            ImageView welcomeView = new ImageView(welcomeImg);
            welcomeView.setFitWidth(Constants.SCREEN_WIDTH + 300);
            welcomeView.setFitHeight(Constants.SCREEN_HEIGHT);
            welcomeView.setPreserveRatio(false);
            welcomeLayout.getChildren().add(welcomeView);
        } catch (Exception e) {
            System.out.println("Không tìm thấy ảnh welcome_bg.png, tự động dùng nền tối.");
            welcomeLayout.setStyle("-fx-background-color: #1a1a2e;");
        }

        // 2.2 Lớp màng đen mờ
        Region welcomeOverlay = new Region();
        welcomeOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");
        welcomeOverlay.setPrefSize(Constants.SCREEN_WIDTH + 300, Constants.SCREEN_HEIGHT);
        welcomeLayout.getChildren().add(welcomeOverlay);

        // 2.3 Nút Start
        Button startButton = new Button("Play");
        String buttonNormalStyle = "-fx-background-color: #e8e8e8; -fx-background-radius: 30; -fx-border-radius: 30; -fx-border-color: #b0b0b0; -fx-border-width: 1.5; -fx-text-fill: #333333; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 8 60 8 60; -fx-cursor: hand;";
        String buttonHoverStyle = "-fx-background-color: #ffffff; -fx-background-radius: 30; -fx-border-radius: 30; -fx-border-color: #888888; -fx-border-width: 1.5; -fx-text-fill: #000000; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 8 60 8 60; -fx-cursor: hand;";

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
            if (newValue) { // newValue == true nghĩa là cửa sổ VỪA BỊ THU NHỎ (Minimize)
                System.out.println("[System] The window has been minimized. Pausing simulation to save resources...");
                
                // 1. Gọi lệnh Pause vòng lặp game chính của bạn để cứu GPU
                GenG.stop(); // Giả sử SimulationController của bạn có hàm stop() / pause()
                
            } else { // newValue == false nghĩa là cửa sổ VỪA ĐƯỢC PHÓNG TO LẠI (Restore)
                System.out.println("[System] The window has been restored. Resuming simulation...");
                
                // 2. Kích hoạt cho vòng lặp game chạy tiếp bình thường
                GenG.Start(); // Gọi lại hàm chạy tiếp
            }
        });
        // ====================================================================

        // Trước đây bạn chỉ để đơn thuần như thế này:
        primaryStage.setScene(welcomeScene);
        primaryStage.setTitle("KénChim đáng yêu - Wildlife Eco Simulator");
        primaryStage.show();;
    }

    // ====================================================================
    // 🌟 BẢNG ĐIỀU KHIỂN: NỀN ẢNH, SPAWNER THỦ CÔNG, BỘ ĐẾM 10S
    // ====================================================================
    private StackPane createDashboardPanel(WorldMap map) {
        StackPane hudRoot = new StackPane();
        hudRoot.setPrefWidth(300);

        // 1. Ảnh nền HUD (Bảo đảm không che các nút)
        try {
            // Thay "hud_bg.png" bằng ảnh nền dọc cho bảng điều khiển
            Image hudBgImage = SpriteManager.loadImage("hud_bg.png");
            ImageView hudBgView = new ImageView(hudBgImage);
            hudBgView.setFitWidth(300);
            hudBgView.setFitHeight(Constants.SCREEN_HEIGHT);
            hudBgView.setPreserveRatio(false);
            hudRoot.getChildren().add(hudBgView);
        } catch (Exception e) {
            hudRoot.setStyle("-fx-background-color: #2a2a40;");
        }

        // 2. Container chứa nội dung
        VBox contentContainer = new VBox(15);
        contentContainer.setPadding(new Insets(25, 20, 20, 20));
        contentContainer.setStyle("-fx-background-color: rgba(30, 30, 45, 0.4);"); 

        Label titleLabel = new Label("HỆ THỐNG GIÁM SÁT");
        titleLabel.setTextFill(javafx.scene.paint.Color.GOLD);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        // Khu vực hiển thị Thống kê
        VBox statsBox = new VBox(8);
        statsBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6); -fx-padding: 12; -fx-background-radius: 8;");

        Label seasonLabel = new Label("☀️ Mùa hiện tại: Mùa Hè");
        Label statsLabel = new Label("🐾 Đang thu thập dữ liệu...");

        seasonLabel.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 14px; -fx-font-weight: bold;");
        statsLabel.setStyle("-fx-text-fill: #a0ffd0; -fx-font-size: 13px; -fx-font-weight: bold;");
        statsBox.getChildren().addAll(seasonLabel, statsLabel);

        // 3. Timeline Đếm thực thể (chu kỳ 10s)
        Timeline telemetryTimeline = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
            int countPassive = 0;
            int countPredator = 0;
            int countAggressive = 0;
            int countGrass = 0;
            int countFish = 0;

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
        }));
        telemetryTimeline.setCycleCount(Timeline.INDEFINITE);
        telemetryTimeline.play();

        // 4. Các nút tạo thủ công (Manual Spawn)
        Label spawnTitle = new Label("⚡ TRIỆU HỒI THỦ CÔNG");
        spawnTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-weight: bold;");
        spawnTitle.setPadding(new Insets(10, 0, 0, 0));

        Button btnSpawnGrass = createStyledButton("🌱 Tạo Cỏ Ngẫu Nhiên");
        Button btnSpawnRabbit = createStyledButton("🐇 Thả Thỏ (Passive)");
        Button btnSpawnWolf = createStyledButton("🐺 Thả Sói (Predator)");
        Button btnSeason = createStyledButton("❄️ Đổi Mùa Đông");

        btnSpawnGrass.setOnAction(e -> manualSpawn(map, "GRASS"));
        btnSpawnRabbit.setOnAction(e -> manualSpawn(map, "RABBIT"));
        btnSpawnWolf.setOnAction(e -> manualSpawn(map, "WOLF"));

        btnSeason.setOnAction(e -> {
            map.isWinter = !map.isWinter;
            seasonLabel.setText(map.isWinter ? "❄️ Mùa hiện tại: Mùa Đông" : "☀️ Mùa hiện tại: Mùa Hè");
            btnSeason.setText(map.isWinter ? "☀️ Đổi Mùa Hè" : "❄️ Đổi Mùa Đông");
        });

        contentContainer.getChildren().addAll(titleLabel, statsBox, spawnTitle, btnSpawnGrass, btnSpawnRabbit, btnSpawnWolf, btnSeason);
        hudRoot.getChildren().add(contentContainer);

        return hudRoot;
    }

    // ====================================================================
    // HÀM BỔ TRỢ: TÌM Ô CỎ TRỐNG VÀ SPAWN
    // ====================================================================
    private void manualSpawn(WorldMap map, String entityType) {
        Random rand = new Random();
        int maxAttempts = 200;

        for (int i = 0; i < maxAttempts; i++) {
            int rx = rand.nextInt(Constants.MAP_WIDTH);
            int ry = rand.nextInt(Constants.MAP_HEIGHT);

            com.wildlife.model.worldmap.Tile tile = map.getTile(rx, ry);

            // 🎯 ĐIỀU KIỆN CHẶT CHẼ: Ô Đất gốc là Cỏ (0) VÀ chưa bị con nào đứng đè lên
            if (com.wildlife.model.worldmap.MatrixManager.MAP_LAYOUT[ry][rx] == 0 && tile != null && !tile.hasOccupant() &&
             ry > 0 && ry < Constants.MAP_HEIGHT - 1 && rx > 0 && rx < Constants.MAP_WIDTH - 1) {
                double pixelX = rx * Constants.TILE_SIZE;
                double pixelY = ry * Constants.TILE_SIZE;

                BaseEntity newSpawn = null;

                switch (entityType) {
                    case "GRASS":
                        newSpawn = new com.wildlife.model.plants.Grass(pixelX, pixelY, 0);
                        break;
                    case "RABBIT":
                        // NOTE: Bạn sửa dòng này thành Class Động vật ăn cỏ của bạn (VD: new Rabbit)
                        newSpawn = new com.wildlife.model.animals.passive.Rabbit(pixelX, pixelY);
                        //System.out.println("Hãy bỏ comment dòng 245 và điền class Rabbit của bạn!");
                        break;
                    case "WOLF":
                        // NOTE: Bạn sửa dòng này thành Class Động vật ăn thịt của bạn (VD: new Wolf)
                        newSpawn = new com.wildlife.model.animals.predator.Wolf(pixelX, pixelY);
                        //System.out.println("Hãy bỏ comment dòng 250 và điền class Wolf của bạn!");
                        break;
                }

                if (newSpawn != null) {
                    map.addEntity(newSpawn);
                    System.out.println("Spawned " + entityType + " at tile [" + rx + ", " + ry + "]");
                    break;
                } else {
                    break;
                }
            }
        }
    }

    // ====================================================================
    // HÀM BỔ TRỢ: TẠO STYLE CHO NÚT BẤM
    // ====================================================================
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