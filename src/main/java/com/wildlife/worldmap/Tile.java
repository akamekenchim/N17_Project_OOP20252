package com.wildlife.worldmap;

import java.util.Objects;

import com.wildlife.model.abstracts.BaseEntity;
import com.wildlife.model.entities.enviroment.Grass;
import com.wildlife.core.Constants;

public class Tile {
    private final int x;
    private final int y;
    private final int id;

    private TerrainType type;
    private BaseEntity occupant;
    private Grass grassEntity;
    
    // Logic mọc cỏ
    private long startGrowingTime;
    private boolean isGrowingActive;
    public static final long GROW_DELAY_MS = 10000;

    public Tile(int x, int y, TerrainType type) {
        this.x = x;
        this.y = y;
        this.id = y * Constants.MAP_WIDTH + x;
        
        this.type = type;
        this.isGrowingActive = false;
        this.startGrowingTime = 0;
        this.occupant = null;
        this.grassEntity = null;
    }
    
    public void activateGrowth() {
        if (type == TerrainType.DIRT && grassEntity == null && !isGrowingActive) {
            this.startGrowingTime = System.currentTimeMillis();
            this.isGrowingActive = true;
        }
    }

    public boolean updateGrowth(long currentTime) {
        if (isGrowingActive && (currentTime - startGrowingTime) >= GROW_DELAY_MS) {
            tryToGrowGrass();
            isGrowingActive = false;
            return true;
        }
        return false;
    }
    
    private void tryToGrowGrass() {
        if (type == TerrainType.DIRT && grassEntity == null) {
            double growthChance = 0.3; 
            
            if (Math.random() < growthChance) {
                this.grassEntity = new Grass(x, y, 1);
            }
        }
    }
    
    public void onEntityStepped() {
        activateGrowth();
    }
    
    public boolean eatGrass() {
        if (grassEntity != null && grassEntity.isAlive()) {
            //grassEntity.beEaten();
            grassEntity = null;

            return true;
        }
        return false;
    }

    public void setOccupant(BaseEntity entity) { 
        if (entity != null && isPassable()) {
            return; 
        }
        this.occupant = entity; 
        if (entity != null) onEntityStepped();
    }
    
    public void removeOccupant() { 
        this.occupant = null; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return x == tile.x && y == tile.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public boolean hasGrass() { return grassEntity != null && grassEntity.isAlive(); }
    public Grass getGrassEntity() { return grassEntity; }
    public boolean hasOccupant() { return occupant != null; }
    public BaseEntity getOccupant() { return occupant; }
    
    public double getOpacity() { return type.getOpacity(); }
    public double getSpeedModifier() { return type.getSpeedModifier(); }
    public boolean isPassable() { return type.isPassable(); }
    
    public TerrainType getType() { return type; }
    public void setType(TerrainType type) { this.type = type; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getId() { return id; }
    public boolean isGrowingActive() { return isGrowingActive; }
}