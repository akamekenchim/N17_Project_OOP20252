/*package com.wildlife.worldmap;

import java.util.*;

//import com.wildlife.core.Constants;
import com.wildlife.model.abstracts.BaseEntity;

public class WorldMap3 {
    private List<BaseEntity> listEntity = new ArrayList<>();

    public void addEntity(BaseEntity k) {
        listEntity.add(k);
    }

    public List<BaseEntity> getEntity() {
        return listEntity;
    }

    // mấy con mà teo r thì xóa đi
    public void cleaning() {
        for (int i = listEntity.size() - 1; i >= 0; i--) {
            if ((listEntity.get(i)).isAlive() == false) {
                listEntity.remove(i);
            }
        }
    }

    public void Update(double Delta) {
        cleaning();
        for (BaseEntity e : listEntity) {
            e.update(Delta, this);
        }
    }

    public boolean isOccupied(double x, double y) {
        return false;
    }
}
/*
package com.wildlife.worldmap;

import java.util.*;
import com.wildlife.model.abstracts.BaseEntity;

public class WorldMap {
    private final int width = 37;
    private final int height = 26;
    private final Tile[][] tiles;
    private final List<BaseEntity> listEntity = new ArrayList<>();

    private final Queue<Tile> growingQueue = new LinkedList<>();

    public WorldMap() {
        tiles = new Tile[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[y][x] = new Tile(x, y, TerrainType.DIRT);
            }
        }
    }

    public boolean addEntity(BaseEntity entity) {
        int tx = (int) entity.getX() / 32;
        int ty = (int) entity.getY() / 32;
        Tile tile = getTile(tx, ty);
        if (tile == null || !tile.isWalkable() || tile.hasOccupant()) {
            return false;
        }
        tile.setOccupant(entity);
        listEntity.add(entity);

        if (tile.getType() == TerrainType.DIRT && !tile.hasGrass()) {
            activateGrowthAt(tx, ty);
        }
        return true;
    }

    public boolean moveEntity(BaseEntity entity, int newTileX, int newTileY) {
        int oldTileX = (int) entity.getX() / 32;
        int oldTileY = (int) entity.getY() / 32;
        Tile oldTile = getTile(oldTileX, oldTileY);
        Tile newTile = getTile(newTileX, newTileY);

        if (newTile == null || !newTile.isWalkable() || newTile.hasOccupant()) return false;

        if (oldTile != null && oldTile.getOccupant() == entity) {
            oldTile.removeOccupant();
        }
        newTile.setOccupant(entity);
        entity.setPosition(newTileX * 32, newTileY * 32);

        if (newTile.getType() == TerrainType.DIRT && !newTile.hasGrass()) {
            activateGrowthAt(newTileX, newTileY);
        }

        return true;
    }

    public void cleaning() {
        Iterator<BaseEntity> it = listEntity.iterator();
        while (it.hasNext()) {
            BaseEntity e = it.next();
            if (!e.isAlive()) {
                Tile t = getTile((int) e.getX() / 32, (int) e.getY() / 32);
                if (t != null && t.getOccupant() == e) {
                    t.removeOccupant();
                }
                it.remove();
            }
        }
    }

    public void activateGrowthAt(int x, int y) {
        Tile tile = getTile(x, y);
        if (tile != null && !tile.isGrowingActive()) {
            tile.activateGrowth();
            if (tile.isGrowingActive()) {
                growingQueue.offer(tile);
            }
        }
    }

    public void updateGrassGrowth() {
        long currentTime = System.currentTimeMillis();
        int size = growingQueue.size();
        for (int i = 0; i < size; i++) {
            Tile tile = growingQueue.poll();
            if (tile == null) continue;

            boolean finished = tile.updateGrowth(currentTime);
            if (!finished) {
                growingQueue.offer(tile);
            }
        }
    }

    public void update(double delta) {

        updateGrassGrowth();

        for (BaseEntity entity : listEntity) {
            entity.update(delta, this);
        }

        cleaning();
    }

    public Tile getTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return tiles[y][x];
        }
        return null;
    }

    public boolean isOccupied(int x, int y) {
        Tile t = getTile(x, y);
        return t != null && t.hasOccupant();
    }

    public List<BaseEntity> getEntity() {
        return listEntity;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getGrowingQueueSize() { return growingQueue.size(); }
} */