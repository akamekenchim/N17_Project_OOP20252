package com.wildlife.model.abstracts;

public abstract class Animal extends BaseEntity {
    private int hunger = 100;
    private double dx = 0;
    private double dy = 0;
    private double innerTime = 0;
    private double innerDirectionTime = 0;
    public void setInnerTime(double innerTime) {
        this.innerTime = innerTime;
    }
    public void setInnerDirectionTime(double innerDirectionTime) {
        this.innerDirectionTime = innerDirectionTime;
    }
    public double getInnerTime() {
        return innerTime;
    }
    public double getInnerDirectionTime(){
        return this.innerDirectionTime;
    }
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

    public int getHunger() {
        return hunger;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public Animal(double x, double y) {
        super(x, y);
    }

}
