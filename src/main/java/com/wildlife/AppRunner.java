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