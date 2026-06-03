package com.wildlife.model.worldmap;

import java.util.*;

import com.wildlife.constant.Constants;
import com.wildlife.model.BaseEntity;
import com.wildlife.model.animals.aggressive.Aggressive;
import com.wildlife.model.animals.passive.Passive;
import com.wildlife.model.animals.predator.Predator;
public class WorldMap {
    public final Tile[][] tiles;
    private final List<BaseEntity> listEntity = new ArrayList<>();
    private Random random = new Random();
    
    // --- CÁC BIẾN CHO MÙA ĐÔNG ---
    public boolean isWinter = false;
    public boolean[][] snowMap = new boolean[Constants.MAP_HEIGHT][Constants.MAP_WIDTH];
    
    // THÊM MỚI: Biến đánh dấu để chỉ diệt 50% sinh vật ĐÚNG 1 LẦN khi đông tới
    private boolean isWinterCulled = false;

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

        return true;
    }
    public boolean addFish(BaseEntity entity) {
        int tx = (int) entity.getX() / Constants.TILE_SIZE;
        int ty = (int) entity.getY() / Constants.TILE_SIZE;
        Tile tile = getTile(tx, ty);
        if (tile == null || tile.isPassable() || tile.hasOccupant()) {
            return false;
        }
        tile.setOccupant(entity);
        listEntity.add(entity);

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

    // 1. Thêm vào phần khai báo biến ở đầu class WorldMap
    

    // ... (Kéo xuống hàm update) ...

    public void update(double delta) {
        checkAllTiles();
        
        // ==========================================
        // LOGIC MÙA ĐÔNG (Phủ tuyết & Thanh trừng)
        // ==========================================
        if (isWinter) {
            // 1. Tuyết rơi từ từ (Phủ 5 ô mỗi frame)
            for(int i = 0; i < 5; i++) {
                int rx = random.nextInt(Constants.MAP_WIDTH);
                int ry = random.nextInt(Constants.MAP_HEIGHT);
                
                if (MatrixManager.MAP_LAYOUT[ry][rx] == 0 && !snowMap[ry][rx]) {
                    snowMap[ry][rx] = true;
                }
            }

            // 2. THÊM MỚI: Loại bỏ ngẫu nhiên 1/2 sinh vật
            if (!isWinterCulled) {
                int killTarget = listEntity.size() / 2; // Tính ra 50% dân số
                int killedCount = 0;
                
                // Quét qua danh sách, mỗi con có 50% tỉ lệ bị chọn
                for (BaseEntity e : listEntity) {
                    // Nếu nó còn sống và bị "xui" (random true)
                    if (e.isAlive() && random.nextBoolean()) {
                        e.setAlive(false); // Rút máu về 0 (đánh dấu chết)
                        killedCount++;
                        
                        // Đã giết đủ 50% thì dừng tay
                        if (killedCount >= killTarget) break; 
                    }
                }
                
                // Khóa cờ lại để các frame sau không bị giết thêm nữa
                isWinterCulled = true; 
            }
            
        } else {
            // Mở khóa cờ khi hết mùa đông (để chuẩn bị cho mùa đông năm sau)
            isWinterCulled = false; 
        }
        // ==========================================

        for (BaseEntity entity : listEntity) {
            entity.update(delta, this);
        }

        // Những con bị setAlive(false) ở trên sẽ được hàm này dọn dẹp sạch sẽ khỏi map ngay lập tức
        cleaning(); 
    }

    public void checkAllTiles(){
        for(int i = 0; i<26; i++){
            for(int j = 0; j<37; j++){
                Tile t = getTile(j, i);
                t.setOccupied(false);
            }
        }
        for(BaseEntity e : listEntity){
            int rawX = (int)(e.getX());
            int rawY = (int)(e.getY());
            int snappedTileX = (int) (rawX / Constants.TILE_SIZE);
            int snappedTileY = (int) (rawY / Constants.TILE_SIZE);
            (getTile(snappedTileX, snappedTileY)).setOccupied(true);
        }
    }

    public Tile getTile(int x, int y) {
        if (x >= 0 && x < Constants.MAP_WIDTH && y >= 0 && y < Constants.MAP_HEIGHT) {
            return tiles[y][x];
        }
        return null;
    }
    //Overload
    public Tile getTile(double rawX, double rawY) {
    // 1. Chuyển đổi tọa độ pixel sang chỉ số ô lưới (Snap to Grid)
    // Công thức: index = floor(pixel / TILE_SIZE)
        int tx = (int) (rawX / Constants.TILE_SIZE);
        int ty = (int) (rawY / Constants.TILE_SIZE);
    // 2. Gọi hàm getTile(int, int) để kiểm tra biên và trả về kết quả
        return getTile(tx, ty);
    }

    public boolean isOccupied(int x, int y) {
        Tile t = getTile(x, y);
        return t != null && t.getOccupied();
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

    public List<BaseEntity> getEntity() {
        return listEntity;
    }

    // 1. KIỂM TRA CHƯỚNG NGẠI VẬT CỨNG (Phải xoay đầu)
    public boolean isObstacle(double pixelX, double pixelY, BaseEntity self) {
        if (pixelX < 0 || pixelX >= Constants.SCREEN_WIDTH || pixelY < 0 || pixelY >= Constants.SCREEN_HEIGHT) {
            return true; 
        }

        Tile t = getTile(pixelX, pixelY);
        if (t == null || !t.isPassable()) {
            return true; // Chặn Nước, Rìa map
        }

        // CHỈ CHẶN ĐÁ (Đã xóa logic kiểm tra đồng loại ở đây)
        for (BaseEntity e : listEntity) {
            if (e != self && e instanceof com.wildlife.model.plants.Rock) {
                int rockTileX = (int) (e.getX() / Constants.TILE_SIZE);
                int rockTileY = (int) (e.getY() / Constants.TILE_SIZE);
                if (rockTileX == t.getX() && rockTileY == t.getY()) {
                    return true;
                }
            }
        }
        return false;
    }

    // 2. KIỂM TRA ĐỒNG LOẠI (Va chạm mềm)
    public boolean isCompanion(double nextX, double nextY, BaseEntity self) {
        double centerX = nextX + 15; 
        double centerY = nextY + 15;

        for (BaseEntity e : listEntity) {
            // Chỉ xét đồng loại và đang sống
            if (e != self && e.isAlive() && 
               ((e instanceof Predator && self instanceof Predator) || 
                (e instanceof Passive && self instanceof Passive) || (e instanceof Aggressive && self instanceof Aggressive)) || (e instanceof Aggressive
                    && self instanceof Passive) || (e instanceof Passive && self instanceof Aggressive)
                ) {
                
                double eCenterX = e.getX() + 15;
                double eCenterY = e.getY() + 15;
                double distSq = (centerX - eCenterX) * (centerX - eCenterX) + (centerY - eCenterY) * (centerY - eCenterY);
                
                if (distSq < 400) { 

                    if (System.identityHashCode(self) < System.identityHashCode(e)) {
                        return true; 
                    }
                    
                }
            }
        }
        return false;
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
