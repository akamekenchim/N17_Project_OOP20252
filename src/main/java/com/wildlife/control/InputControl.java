package com.wildlife.control;

import javafx.event.*;
import com.wildlife.core.Constants;
import com.wildlife.core.SimEngine;
import com.wildlife.model.entities.enviroment.*;
import com.wildlife.model.entities.animal.passive.*;
import com.wildlife.model.entities.animal.predator.*;
import com.wildlife.worldmap.MatrixManager;
//import com.wildlife.model.entities.animal.priority.*;
import com.wildlife.worldmap.WorldMap;

import javafx.scene.*;

public class InputControl {
    public static boolean isZoomed = false; // Kiểm tra xem màn hình có bị zoom ko -> nếu có, thì cho panning
    public static int hoverx = 0; //Tọa độ chuột hover
    public static int hovery = 0; //Tọa độ chuột hover
    public static int typeAnimal = -2; // Mặc định -2; khi click sẽ không ra cái gì
    public static double temp = 0;
    public static boolean isPaused = false; //Kiểm tra xem program có đang bị tạm dừng ko
    public static double lastMouseX = 0.0;
    public static double lastMouseY = 0.0;
    public static void StartListening(Scene scene, WorldMap map) {
        scene.setOnKeyPressed(event -> {
            switch(event.getCode()){
                case UP:
                    System.out.println("Pressed UP ARROW");
                    if(!isPaused) Constants.SIM_SPEED += 0.25;
                    break;
                case DOWN:
                    System.out.println("Pressed DOWN ARROW");
                    if(!isPaused) Constants.SIM_SPEED = Math.max(0.25, Constants.SIM_SPEED - 0.25);
                    break;
                case DIGIT0:
                    typeAnimal = 0;
                    break;
                case DIGIT1:
                    typeAnimal = 1;
                    break;
                case DIGIT2:
                    typeAnimal = 2;
                    break;
                case DIGIT3:
                    typeAnimal = 3;
                    break;
                case DIGIT4:
                    typeAnimal = 4;
                    break;
                case DIGIT5:
                    typeAnimal = 5;
                    break;
                case DIGIT6:
                    typeAnimal = 6;
                    break;
                case SPACE:
                    if(Constants.SIM_SPEED != 0 ){
                        temp = Constants.SIM_SPEED;
                        Constants.SIM_SPEED = 0;
                        isPaused = true;
                    }
                    else if(Constants.SIM_SPEED == 0){
                        Constants.SIM_SPEED = temp;
                        isPaused = false;
                    }
                    break;
                case R:
                    SimEngine.camX = 0;
                    SimEngine.camY = 0;
                    SimEngine.zoomLevel = 1.0;
                    Constants.SIM_SPEED = 1.0;
                    isZoomed = false;
                default:
                    break;
            }
        });
        scene.setOnMouseClicked(event -> {
            double rawX = SimEngine.screenToWorldX(event.getX());
            double rawY = SimEngine.screenToWorldY(event.getY());
            int MAP_WIDTH_PIXELS = 37 * Constants.TILE_SIZE;
            int MAP_HEIGHT_PIXELS = 26 * Constants.TILE_SIZE;
            if (rawX < 0 || rawY < 0 || rawX >= MAP_WIDTH_PIXELS || rawY >= MAP_HEIGHT_PIXELS) {
                System.out.println("Ngoài phạm vi bản đồ!");
                return;
            }
            int parsed_X = (int) (rawX / Constants.TILE_SIZE) * Constants.TILE_SIZE;
            int parsed_Y = (int) (rawY / Constants.TILE_SIZE) * Constants.TILE_SIZE;

            int snappedTileX = (int) (rawX / Constants.TILE_SIZE);
            int snappedTileY = (int) (rawY / Constants.TILE_SIZE);
            // nhóm WorldMap cần thêm 1 hàm isOccupied trong WorldMap để kiểm tra xem tile
            // này đã
            // có thực thể nào chưa, nếu có rồi thì không được đặt nữa
            if (!(map.isOccupied(parsed_X, parsed_Y))) {
                if(typeAnimal == 1){
                    Grass g = new Grass(parsed_X, parsed_Y, 0);
                    if(MatrixManager.MAP_LAYOUT[snappedTileY][snappedTileX] == 0) {
                        map.addEntity(g);
                    }
                }
                if(typeAnimal == 0){
                    Rock g = new Rock(parsed_X, parsed_Y);
                    map.addEntity(g);
                }
                if(typeAnimal == 2){
                    Kenchim g = new Kenchim(parsed_X, parsed_Y);
                    map.addEntity(g);
                }
                if(typeAnimal == 3){
                    Kohane g = new Kohane(parsed_X, parsed_Y);
                    map.addEntity(g);
                }
                if(typeAnimal == -1){
                    Grass g = new Grass(parsed_X, parsed_Y, 0);
                    map.addEntity(g);
                }
            } else
                System.out.println("There's already something here!");
            System.out.printf("Omae ga kono tile wo sawatteita: %.2f -- %.2f\n", rawX, rawY);    
        });  
        scene.setOnMouseMoved(event->{
            double rawX = SimEngine.screenToWorldX(event.getX());
            double rawY = SimEngine.screenToWorldY(event.getY());

            int snapped_X = ((int)(rawX) / Constants.TILE_SIZE) * Constants.TILE_SIZE;
            int snapped_Y = ((int)(rawY) / Constants.TILE_SIZE) * Constants.TILE_SIZE;

            hoverx = snapped_X;
            hovery = snapped_Y;
        });
        scene.setOnScroll(event -> {
            double scroll_y = event.getDeltaY();
            double mouse_positionX = event.getSceneX();
            double mouse_positionY = event.getSceneY();
            double worldX_Before = (mouse_positionX - SimEngine.camX) / SimEngine.zoomLevel;     
            double worldY_Before = (mouse_positionY - SimEngine.camY) / SimEngine.zoomLevel;    
            double oldzoom = SimEngine.zoomLevel;
            if(scroll_y > 0){
                SimEngine.zoomLevel = Math.min(5.0, SimEngine.zoomLevel + 0.1);
            }
            if(scroll_y < 0){
                SimEngine.zoomLevel = Math.max(1.0, SimEngine.zoomLevel - 0.1);
                SimEngine.camX = 0;
                SimEngine.camY = 0;

                double nextCamX = SimEngine.camX ;
                double nextCamY = SimEngine.camY ;//+ (deltaY / SimEngine.zoomLevel);

                double mapDisplayWidth = Constants.SCREEN_WIDTH * SimEngine.zoomLevel;
                double mapDisplayHeight = Constants.SCREEN_HEIGHT * SimEngine.zoomLevel;

                double minCamX = Constants.SCREEN_WIDTH - mapDisplayWidth;
                double minCamY = Constants.SCREEN_HEIGHT - mapDisplayHeight;
                if(mapDisplayWidth > Constants.SCREEN_WIDTH){
                    SimEngine.camX = Math.max(minCamX,Math.min(0, nextCamX));
                } else {
                    SimEngine.camX = 0;
                }
                if (mapDisplayHeight > Constants.SCREEN_HEIGHT) {
                    //double minY = Constants.SCREEN_HEIGHT - mapDisplayHeight;
                    SimEngine.camY = Math.max(minCamY, Math.min(0, nextCamY));
                } else {
                    SimEngine.camY = 0;
                }
            }
            if(oldzoom != SimEngine.zoomLevel){
                SimEngine.camX = mouse_positionX - (worldX_Before * SimEngine.zoomLevel);
                SimEngine.camY = mouse_positionY - (worldY_Before * SimEngine.zoomLevel);
                isZoomed = SimEngine.zoomLevel > 1 ? true:false;
            }
        });
        scene.setOnMousePressed(event -> {
            lastMouseX = event.getSceneX();
            lastMouseY = event.getSceneY();
        });
        scene.setOnMouseDragged(event -> {
            if(isZoomed){
               // double cur_x = SimEngine.screenToWorldX(event.getSceneX());
               // double cur_y = SimEngine.screenToWorldY(event.getSceneY());
                double deltaX = (event.getSceneX() - lastMouseX) * Constants.PANNING_SPEED;
                double deltaY = (event.getSceneY() - lastMouseY) * Constants.PANNING_SPEED;
                double nextCamX = SimEngine.camX + (deltaX / SimEngine.zoomLevel);
                double nextCamY = SimEngine.camY + (deltaY / SimEngine.zoomLevel);

                double mapDisplayWidth = Constants.SCREEN_WIDTH * SimEngine.zoomLevel;
                double mapDisplayHeight = Constants.SCREEN_HEIGHT * SimEngine.zoomLevel;

                double minCamX = Constants.SCREEN_WIDTH - mapDisplayWidth;
                double minCamY = Constants.SCREEN_HEIGHT - mapDisplayHeight;
                if(mapDisplayWidth > Constants.SCREEN_WIDTH){
                    SimEngine.camX = Math.max(minCamX,Math.min(0, nextCamX));
                } else {
                    SimEngine.camX = 0;
                }
                if (mapDisplayHeight > Constants.SCREEN_HEIGHT) {
                    //double minY = Constants.SCREEN_HEIGHT - mapDisplayHeight;
                    SimEngine.camY = Math.max(minCamY, Math.min(0, nextCamY));
                } else {
                    SimEngine.camY = 0;
                }
                /*double temp1 = SimEngine.camX;
                double temp2 = SimEngine.camY;
                //if(SimEngine.camX + (deltaX / SimEngine.zoomLevel) > 1184 || SimEngine.camX + (deltaX / SimEngine.zoomLevel) < 0 ) SimEngine.camX = 0;
                SimEngine.camX += (deltaX / SimEngine.zoomLevel);
                System.out.println("Hien tai dang o: "+SimEngine.camX); 
                if(SimEngine.camX > 0 || SimEngine.camX < Constants.SCREEN_WIDTH - Constants.SCREEN_WIDTH * SimEngine.zoomLevel) SimEngine.camX = temp1;
                //if(SimEngine.camY + (deltaY / SimEngine.zoomLevel) > 832 || SimEngine.camY + (deltaY / SimEngine.zoomLevel) < 0) SimEngine.camY = 0;
                SimEngine.camY += (deltaY/ SimEngine.zoomLevel);
                if(SimEngine.camY > 0 || SimEngine.camY < Constants.SCREEN_HEIGHT - Constants.SCREEN_HEIGHT * SimEngine.zoomLevel) SimEngine.camY = temp2;
                */
                lastMouseX = event.getSceneX();
                lastMouseY = event.getSceneY();
            }
        });
    }
}
