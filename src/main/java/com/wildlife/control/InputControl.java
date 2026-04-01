package com.wildlife.control;

import javafx.event.*;
import com.wildlife.core.Constants;
import com.wildlife.core.SimEngine;
import com.wildlife.model.entities.enviroment.*;
import com.wildlife.model.entities.animal.passive.*;
import com.wildlife.model.entities.animal.predator.*;
//import com.wildlife.model.entities.animal.priority.*;
import com.wildlife.worldmap.WorldMap;

import javafx.scene.*;

public class InputControl {
    public static boolean isZoomed = false;
    public static int hoverx = 0;
    public static int hovery = 0;
    public static int typeAnimal = -2;
    public static double temp = 0;
    public static boolean isPaused = false;
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
                    isZoomed = false;
                default:
                    break;
            }
        });
        scene.setOnMouseClicked(event -> {
            double rawX = SimEngine.screenToWorldX(event.getX());
            double rawY = SimEngine.screenToWorldY(event.getY());
            if (rawX <= 20 || rawY <= 15 || rawX >= Constants.SCREEN_WIDTH - 20
                    || rawY >= Constants.SCREEN_HEIGHT - 15) {
                System.out.println("Khong duoc dat o day!");
                return;
            }
            int parsed_X = (int) (rawX / Constants.TILE_SIZE) * Constants.TILE_SIZE;
            int parsed_Y = (int) (rawY / Constants.TILE_SIZE) * Constants.TILE_SIZE;

            //int snappedTileX = (int) (rawX / Constants.TILE_SIZE);
            //int snappedTileY = (int) (rawY / Constants.TILE_SIZE);
            // nhóm WorldMap cần thêm 1 hàm isOccupied trong WorldMap để kiểm tra xem tile
            // này đã
            // có thực thể nào chưa, nếu có rồi thì không được đặt nữa
            if (!(map.isOccupied(parsed_X, parsed_Y))) {
                if(typeAnimal == 4){
                    Grass g = new Grass(parsed_X, parsed_Y, 0);
                    map.addEntity(g);
                }
                if(typeAnimal == 0){
                    Rock g = new Rock(parsed_X, parsed_Y);
                    map.addEntity(g);
                }
                if(typeAnimal == -1){
                    Grass g = new Grass(parsed_X, parsed_Y, 0);
                    map.addEntity(g);
                }
                if(typeAnimal == -1){
                    Grass g = new Grass(parsed_X, parsed_Y, 0);
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
        scene.setOnMouseMoved(event -> {
            double worldX = SimEngine.screenToWorldX(event.getX());
            double worldY = SimEngine.screenToWorldY(event.getY());
            
            int snapped_X = ((int)(worldX) / Constants.TILE_SIZE) * Constants.TILE_SIZE;
            int snapped_Y = ((int)(worldY) / Constants.TILE_SIZE) * Constants.TILE_SIZE; 

            hoverx = snapped_X;
            hovery = snapped_Y;

            //System.out.println("Hovering on tile (" + snapped_X + ", " + snapped_Y + ")");
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
            }
            if(oldzoom != SimEngine.zoomLevel){
                SimEngine.camX = mouse_positionX - (worldX_Before * SimEngine.zoomLevel);
                SimEngine.camY = mouse_positionY - (worldY_Before * SimEngine.zoomLevel);
                isZoomed = SimEngine.zoomLevel > 1 ? true:false;
            }
        });
        /*scene.setOnMouseDragged(event -> {
            if(isZoomed){
                double x = event.getSceneX();
                double y = event.getSceneY();
                SimEngine.camX = x - (x - SimEngine.camX);
                SimEngine.camY = y - (y - SimEngine.camY);
            }
        });*/
    }
}
