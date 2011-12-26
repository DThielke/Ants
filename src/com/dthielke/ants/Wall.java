package com.dthielke.ants;

public class Wall extends EmptyZone {
    public Wall(Location location) {
        super(location);
    }

    @Override
    public ZoneType getType() {
        return ZoneType.WALL;
    }

    @Override
    public boolean isTraversable() {
        return false;
    }

    @Override
    public double getPheromoneLevel(PheromoneType type) {
        return 0;
    }
}
