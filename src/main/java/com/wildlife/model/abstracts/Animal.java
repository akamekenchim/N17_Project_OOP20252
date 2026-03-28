package com.wildlife.model.abstracts;

public abstract class Animal extends BaseEntity {
    private int hunger = 100;

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
