package com.wildlife.model.entity;
import javafx.scene.canvas.GraphicsContext;
import com.wildlife.core.WorldMap;
//đây là lớp cha của mọi "ENTITY" - các đối tượng có trong project.
public abstract class BaseEntity {
    private double x;
    private double y;
    private boolean isAlive = true;
    private String spritePath;
    public BaseEntity(double hoanhdo, double tungdo){
        this.x = hoanhdo;
        this.y = tungdo;
    }
    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }
    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }
    public boolean isAlive() {
        return isAlive;
    }
    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }
    //hàm update: bắt buộc phải có trong các entity thừa kế lớp này (trừ các lớp Animal, Tree vì cũng là lớp abstract.)
    //Giả sử có thêm con bò thì phải @Override lớp update để thực hiện trừ điểm hunger, di chuyển,...
    //Ae nhớ phải học thêm phần javafx, tốc độ của mấy con vật thì ở trong Constant.java, chẳng hạn update vị trí cho con vật bằng 
    //this.setX(this.getX() + Constants.RABBIT_SPEED)
    public abstract void update(double delta, WorldMap map); 
    public abstract void render(GraphicsContext gc, boolean isGraphicMode);
    public String getSpritePath() {
        return spritePath;
    }
    public void setSpritePath(String spritePath) {
        this.spritePath = spritePath;
    }
}
