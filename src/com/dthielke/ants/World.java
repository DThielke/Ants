package com.dthielke.ants;

import java.util.Set;

public interface World {
    public void addAnt(Ant ant);

    public Set<Ant> getAnts();

    public int getHeight();

    public int getWidth();

    public Zone getZone(Location location);

    public Zone getZone(int x, int y);

    public Set<Nest> getNests();

    public Set<FoodSource> getFoodSources();

    public void removeAnt(Ant ant);

    public void setZone(Location location, Zone zone);

    public void setZone(int x, int y, Zone zone);

    public void diffusePheromones(double delta);

    public void evaporatePheromones(double evaporation);
}
