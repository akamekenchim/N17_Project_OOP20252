package com.wildlife.model.worldmap;

import java.util.*;

import com.wildlife.constant.Constants;
import com.wildlife.model.BaseEntity;

public class WorldMap {
    public final Tile[][] tiles;
    private final List<BaseEntity> listEntity = new ArrayList<>();

    private final Queue<Tile> growingQueue = new LinkedList<>();

    public WorldMap() {
        tiles = new Tile[Constants.MAP_HEIGHT][Constants.MAP_WIDTH];

        for (int y = 0; y < Constants.MAP_HEIGHT; y++) {
            for (int x = 0; x < Constants.MAP_WIDTH; x++) {

                int value = MatrixManager.MAP_LAYOUT[y][x];
                TerrainType type;

                switch (value) {
                    case 0:
                        type = TerrainType.GRASS;
                        break;
                    case 1:
                        type = TerrainType.WATER;
                        break;
                    case 2:
                        type = TerrainType.WATER;
                        break;
                    case 3:
                        type = TerrainType.WATER;
                        break;
                    default:
                        type = TerrainType.DIRT;
                }

                tiles[y][x] = new Tile(x, y, type);
            }
        }
    }

    public boolean addEntity(BaseEntity entity) {
        int tx = (int) entity.getX() / Constants.TILE_SIZE;
        int ty = (int) entity.getY() / Constants.TILE_SIZE;
        Tile tile = getTile(tx, ty);
        if (tile == null || !tile.isPassable() || tile.hasOccupant()) {
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
        int oldTileX = (int) entity.getX() / Constants.TILE_SIZE;
        int oldTileY = (int) entity.getY() / Constants.TILE_SIZE;
        Tile oldTile = getTile(oldTileX, oldTileY);
        Tile newTile = getTile(newTileX, newTileY);

        if (newTile == null || !newTile.isPassable() || newTile.hasOccupant())
            return false;

        if (oldTile != null && oldTile.getOccupant() == entity) {
            oldTile.removeOccupant();
        }
        newTile.setOccupant(entity);
        entity.setX(newTileX * Constants.TILE_SIZE);
        entity.setY(newTileY * Constants.TILE_SIZE);

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
                Tile t = getTile((int) e.getX() / Constants.TILE_SIZE, (int) e.getY() / Constants.TILE_SIZE);
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
            if (tile == null)
                continue;

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
        if (x >= 0 && x < Constants.MAP_WIDTH && y >= 0 && y < Constants.MAP_HEIGHT) {
            return tiles[y][x];
        }
        return null;
    }

    public boolean isOccupied(int x, int y) {
        Tile t = getTile(x, y);
        return t != null && t.hasOccupant();
    }

    // Hàm này chưa lọc theo đối tượng, chỉ lấy tất cả những vật thể trong phạm vi
    public List<BaseEntity> getEntitiesInRange(double x, double y, double range) {
        List<BaseEntity> nearbyEntities = new ArrayList<>();
        for (BaseEntity entity : listEntity) {
            double dx = entity.getX() - x;
            double dy = entity.getY() - y;
            if (dx * dx + dy * dy <= range * range) {
                nearbyEntities.add(entity);
            }
        }
        return nearbyEntities;
    }

    // xem sói có thấy thỏ sau đá/trong bụi cây không
    boolean canSee(BaseEntity observer, BaseEntity target) {
        int observerTileX = (int) observer.getX() / Constants.TILE_SIZE;
        int observerTileY = (int) observer.getY() / Constants.TILE_SIZE;

        int targetTileX = (int) target.getX() / Constants.TILE_SIZE;
        int targetTileY = (int) target.getY() / Constants.TILE_SIZE;

        int dx = Math.abs(targetTileX - observerTileX);
        int dy = Math.abs(targetTileY - observerTileY);

        int stepX = (targetTileX > observerTileX) ? 1 : (targetTileX < observerTileX) ? -1 : 0;
        int stepY = (targetTileY > observerTileY) ? 1 : (targetTileY < observerTileY) ? -1 : 0;

        int err = dx - dy;

        double visibility = 1.0;

        while (true) {
            Tile tile = getTile(observerTileX, observerTileY);

            if (tile != null) {
                visibility *= tile.getOpacity();
                if (visibility < 0.1)
                    return false; // cái này có thể thay đổi tùy vào độ nhạy của mắt các con vật
            }

            if (observerTileX == targetTileX && observerTileY == targetTileY) {
                return true;
            }

            int err2 = err * 2;
            if (err2 > -dy) {
                err -= dy;
                observerTileX += stepX;
            }
            if (err2 < dx) {
                err += dx;
                observerTileY += stepY;
            }
        }
    }

    public List<BaseEntity> getEntity() {
        return listEntity;
    }

    public int getGrowingQueueSize() {
        return growingQueue.size();
    }

}

/*
 * package com.wildlife.worldmap;
 * 
 * import java.util.*;
 * 
 * //import com.wildlife.core.Constants;
 * import com.wildlife.model.abstracts.BaseEntity;
 * 
 * public class WorldMap {
 * private List<BaseEntity> listEntity = new ArrayList<>();
 * 
 * public void addEntity(BaseEntity k) {
 * listEntity.add(k);
 * }
 * 
 * public List<BaseEntity> getEntity() {
 * return listEntity;
 * }
 * 
 * // mấy con mà teo r thì xóa đi
 * public void cleaning() {
 * for (int i = listEntity.size() - 1; i >= 0; i--) {
 * if ((listEntity.get(i)).isAlive() == false) {
 * listEntity.remove(i);
 * }
 * }
 * }
 * 
 * public void Update(double Delta) {
 * cleaning();
 * for (BaseEntity e : listEntity) {
 * e.update(Delta, this);
 * }
 * }
 * 
 * public boolean isOccupied(double x, double y) {
 * return false;
 * }
 * }
 */
