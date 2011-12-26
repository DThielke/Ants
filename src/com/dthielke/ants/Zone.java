package com.dthielke.ants;

import java.util.Set;

public interface Zone {
    public Location getLocation();

    public double getPheromoneLevel(PheromoneType type);

    public ZoneType getType();

    public void interact(Ant ant);

    public void setPheromoneLevel(PheromoneType type, double level);

    public Set<Ant> getAnts();

    public void addAnt(Ant ant);

    public void removeAnt(Ant ant);

    public boolean isTraversable();
}
