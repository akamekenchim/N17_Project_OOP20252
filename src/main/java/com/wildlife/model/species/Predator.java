package com.wildlife.model.species;
import com.wildlife.model.entity.*;
public abstract class Predator extends Animal {
    //Ae tạo mấy con ăn thịt (hổ, sói, sư tử) thì kế thừa class này
    public Predator(double Hoanhdo, double Tungdo){
        super(Hoanhdo, Tungdo);
    }
}
