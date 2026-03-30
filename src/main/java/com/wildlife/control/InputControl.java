package com.wildlife.control;

//import javafx.event.*;
import com.wildlife.core.Constants;
import com.wildlife.model.entities.enviroment.Grass;
import com.wildlife.worldmap.WorldMap;

import javafx.scene.*;

public class InputControl {
    public static void StartListening(Scene scene, WorldMap map) {
        scene.setOnKeyPressed(event2 -> {
            switch(event2.getCode()){
                case UP:
                    System.out.println("Pressed UP ARROW");
                    Constants.SIM_SPEED += 0.25;
                    break;
                case DOWN:
                    System.out.println("Pressed DOWN ARROW");
                    Constants.SIM_SPEED = Math.max(0.25, Constants.SIM_SPEED - 0.25);
                    break;
                default:
                    break;
            }
        });
        scene.setOnMouseClicked(event -> {
            double rawX = event.getX();
            double rawY = event.getY();
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
                if (event.isShiftDown()) {
                    // Wolf w = new Wolf(parsed_X, parsed_Y);
                    // map.addEntity(w);
                } else {
                    Grass g = new Grass(parsed_X, parsed_Y, 1);
                    map.addEntity(g);
                }
            } else
                System.out.println("There's already something here!");
            System.out.printf("Omae ga kono tile wo sawatteita: %.2f -- %.2f\n", rawX, rawY);    
        });  
    }
}
