package com.wildlife.model.species;
import com.wildlife.model.entity.*;
public abstract class Passive extends Animal {
    //ae tạo mấy con cute (mèo, thỏ,..) thì kế thừa class này
    public Passive(double Hoanhdo, double Tungdo){
        super(Hoanhdo, Tungdo);
    }
}