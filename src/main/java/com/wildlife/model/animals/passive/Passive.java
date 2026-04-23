package com.wildlife.model.animals.passive;

import com.wildlife.model.animals.Animal;

// Động vật ăn cỏ, kế thừa từ Animal
public abstract class Passive extends Animal {
    public int avoidanceTimer = 0;
    public Passive(double x, double y) {
        super(x, y);
    }
}