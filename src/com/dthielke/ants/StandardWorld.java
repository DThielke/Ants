package com.dthielke.ants;

import java.util.HashSet;
import java.util.Set;

public class StandardWorld implements World {
    private final Set<Ant> ants = new HashSet<Ant>();
    private final Set<Nest> nests = new HashSet<Nest>();
    private final Set<FoodSource> foodSources = new HashSet<FoodSource>();
    private final Zone[][] zones;
    private final int width, height;

    public StandardWorld(int width, int height) {
        this.width = width;
        this.height = height;
        this.zones = new Zone[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                zones[x][y] = new EmptyZone(new Location(x, y));
            }
        }
    }

    @Override
    public Set<Ant> getAnts() {
        return ants;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void addAnt(Ant ant) {
        ants.add(ant);
    }

    @Override
    public Set<Nest> getNests() {
        return nests;
    }

    @Override
    public Set<FoodSource> getFoodSources() {
        return foodSources;
    }

    @Override
    public Zone getZone(Location location) {
        return getZone(location.getX(), location.getY());
    }

    @Override
    public void removeAnt(Ant ant) {
        ants.remove(ant);
    }

    @Override
    public void setZone(Location location, Zone zone) {
        setZone(location.getX(), location.getY(), zone);
    }

    @Override
    public Zone getZone(int x, int y) {
        if (x < 0 || x > width - 1 || y < 0 || y > height - 1)
            return null;
        return zones[x][y];
    }

    @Override
    public void setZone(int x, int y, Zone zone) {
        if (zones[x][y].getType() == ZoneType.NEST)
            nests.remove(zones[x][y]);
        else if (zones[x][y].getType() == ZoneType.FOOD)
            foodSources.remove(zones[x][y]);
        zones[x][y] = zone;
        if (zone.getType() == ZoneType.NEST)
            nests.add((Nest) zone);
        else if (zone.getType() == ZoneType.FOOD)
            foodSources.add((FoodSource) zone);
    }

    @Override
    public void evaporatePheromones(double evaporation) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Zone zone = zones[x][y];
                for (PheromoneType type : PheromoneType.values()) {
                    double level = zone.getPheromoneLevel(type);
                    zone.setPheromoneLevel(type, level - evaporation);
                }
            }
        }
    }

    @Override
    public void diffusePheromones(double delta) {
        Zone diffuseTo[] = new Zone[8];
        for (PheromoneType type : PheromoneType.values()) {
            double changes[][] = new double[width][height];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    double level = zones[x][y].getPheromoneLevel(type);
                    int diffuseCount = 0;
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            if (!(dx == 0 && dy == 0)) {
                                if (!(x + dx < 0 || x + dx > width - 1 || y + dy < 0 || y + dy > height - 1)) {
                                    if (level > zones[x + dx][y + dy].getPheromoneLevel(type)) {
                                        diffuseTo[diffuseCount] = zones[x + dx][y + dy];
                                        diffuseCount++;
                                    }
                                }
                            }
                        }
                    }
                    if (diffuseCount > 0) {
                        double diffuseAmount = level * delta;
                        for (int i = 0; i < diffuseCount; i++) {
                            double otherLevel = diffuseTo[i].getPheromoneLevel(type);
                            double diffusePortion = Math.min(level - otherLevel, diffuseAmount / (diffuseCount - i));
                            changes[diffuseTo[i].getLocation().getX()][diffuseTo[i].getLocation().getY()] += diffusePortion;
                            changes[x][y] -= diffusePortion;
                            diffuseAmount -= diffusePortion;
                        }
                    }
                }
            }

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    zones[x][y].setPheromoneLevel(type, zones[x][y].getPheromoneLevel(type) + changes[x][y]);
                }
            }
        }
    }
}
