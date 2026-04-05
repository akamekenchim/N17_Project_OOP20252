package com.wildlife;

import com.wildlife.core.Constants;
import com.wildlife.core.SimEngine;
import com.wildlife.worldmap.WorldMap;
import com.wildlife.worldmap.WorldRender;
import com.wildlife.control.InputControl;
//import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
//import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AppRunner extends Application{
    @Override
    public void start(Stage primaryStage){
        Canvas cv = new Canvas(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT); //tạo canvas hoàn toàn trống
        GraphicsContext gc = cv.getGraphicsContext2D(); //Lấy graphicscontext của canvas vừa tạo

        Canvas cv_Background = new Canvas(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        //GraphicsContext gc2 = cv_Background.getGraphicsContext2D();

        WorldMap map = new WorldMap();
        WorldRender WR = new WorldRender();
        WR.generateMapCache();
        SimEngine GenG = new SimEngine(map, gc, WR);
        //WorldRender.renderAll(gc2);
        GenG.Start();
         //Trong hàm start này có 3 việc: Xóa màn, update trạng thái của WorldMap, rồi render các thực thể
        //StackPane root = new StackPane(cv_Background, cv);
        Group gr = new Group(cv);
        Scene scene = new Scene(gr, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

        primaryStage.setScene(scene);
        primaryStage.setTitle("KénChim đáng yêu");
        InputControl.StartListening(scene, map);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}