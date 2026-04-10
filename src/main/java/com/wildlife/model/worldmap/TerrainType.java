package com.wildlife.model.worldmap;

public enum TerrainType {
    // Tên(Encoding, Opacity, Passable, SpeedModifier)
    DIRT(0, 1.0, true, 1.0),
    GRASS(1, 0.8, true, 0.9),
    BUSH(2, 0.5, true, 0.7),
    FOREST(3, 0.2, true, 0.5),
    ROCK(4, 0.0, false, 0.0),
    WATER(5, 1.0, false, 0.2);

    private final int encoding;
    private final double opacity;
    private final boolean passable;
    private final double speedModifier;

    TerrainType(int encoding, double opacity, boolean passable, double speedModifier) {
        this.encoding = encoding;
        this.opacity = opacity;
        this.passable = passable;
        this.speedModifier = speedModifier;
    }

    public int getEncoding() {
        return encoding;
    }

    public double getOpacity() {
        return opacity;
    }

    public boolean isPassable() {
        return passable;
    }

    public double getSpeedModifier() {
        return speedModifier;
    }
}