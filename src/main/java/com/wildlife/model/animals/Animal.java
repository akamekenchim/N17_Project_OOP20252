package com.wildlife.model.animals;

import com.wildlife.model.BaseEntity;

public abstract class Animal extends BaseEntity {
    private double hunger = 100;
    private double thirst = 100;
    private double tiredness = 100;
    private double dx = 0;
    private double dy = 0;
    private double innerTime = 0;
    private double innerDirectionTime = 0;
    protected int avoidanceTimer = 0;
    protected double speed = 0;
    public void setInnerTime(double innerTime) {
        this.innerTime = innerTime;
    }

    public void setInnerDirectionTime(double innerDirectionTime) {
        this.innerDirectionTime = innerDirectionTime;
    }

    public double getInnerTime() {
        return innerTime;
    }

    public double getInnerDirectionTime() {
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

    public double getHunger() {
        return hunger;
    }

    public void setHunger(double hunger) {
        this.hunger = hunger;
    }

    public Animal(double x, double y) {
        super(x, y);
    }
    public int getAvoidanceTimer() {
        return avoidanceTimer;
    }
    public void setAvoidanceTimer(int avoidanceTimer) {
        this.avoidanceTimer = avoidanceTimer;
    }
    public double getThirst() {
        return thirst;
    }

    public void setThirst(double thirst) {
        this.thirst = thirst;
    }

    public double getTiredness() {
        return tiredness;
    }

    public void setTiredness(double tiredness) {
        this.tiredness = tiredness;
    }
}
