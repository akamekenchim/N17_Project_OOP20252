package com.wildlife.model.entity;
//lớp cha cho các loại cây, ae tạo các class cây trong folder species
public abstract class Tree extends BaseEntity{
    public Tree(double hoanhdo, double tungdo){
        super(hoanhdo, tungdo);
    }
}
