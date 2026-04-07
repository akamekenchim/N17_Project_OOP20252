package com.wildlife.model.strategy;

public class Vector {
    private double dx;
    private double dy;
    public double getDx() {
        return dx;
    }
    public void setDx(double dx) {
        this.dx = dx;
    }
    public double getDy() {
        return dy;
    }
    public void setDy(double dy) {
        this.dy = dy;
    }
    
    public Vector(double DX, double DY){
        this.dx = DX;
        this.dy = DY;
    }
    
}
