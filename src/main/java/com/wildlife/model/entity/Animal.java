package com.wildlife.model.entity;
//lớp cha của các con vật, ae tạo class con vật ở folder species
public abstract class Animal extends BaseEntity{
    private int hunger = 100;

    public int getHunger() {
        return hunger;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public Animal(double hoanhdo, double tungdo){
        super(hoanhdo, tungdo);
    }
    
}
