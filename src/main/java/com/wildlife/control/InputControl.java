package com.wildlife.control;
import javafx.event.*;
import com.wildlife.core.Constants;
import com.wildlife.core.WorldMap;
import com.wildlife.model.entity.Grass;
import javafx.scene.*;
public class InputControl{
    public static void StartListening(Scene scene, WorldMap map){
        scene.setOnMouseClicked(event -> {
            double rawX = event.getX();
            double rawY = event.getY();
            if(rawX <= 20 || rawY <= 15 || rawX >= Constants.SCREEN_WIDTH - 20 || rawY >= Constants.SCREEN_HEIGHT - 15){
                System.out.println("Khong duoc dat o day!");
                return;
            }
            int parsed_X = (int)(rawX / Constants.TILE_SIZE)*Constants.TILE_SIZE;
            int parsed_Y = (int)(rawY / Constants.TILE_SIZE)*Constants.TILE_SIZE;

            int snappedTileX = (int)(rawX / Constants.TILE_SIZE);
            int snappedTileY = (int)(rawY / Constants.TILE_SIZE);
            //nhóm WorldMap cần thêm 1 hàm trong WorldMap để kiểm tra xem tile này đã 
            //có thực thể nào chưa, nếu có rồi thì không được đặt nữa
            int tp = event.isAltDown() ? 1:0;
            Grass g = new Grass(parsed_X, parsed_Y, tp);
            map.addEntity(g);
            System.out.printf("Omae ga kono tile wo sawatteita: %.2f -- %.2f\n", rawX, rawY);
            
        });
    }
}
