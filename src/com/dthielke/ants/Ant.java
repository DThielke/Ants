package com.dthielke.ants;

public interface Ant {
    public int getCrowdThreshold();

    public double getDepositDecay();

    public PheromoneType getDepositedPheromone();

    public double getDetectionThreshold();

    public double getDirection();

    public Location getLocation();

    public PheromoneType getPursuedPheromone();

    public double getWanderProbability();

    public World getWorld();

    public boolean hasFood();

    public void setCrowdThreshold(int crowdThreshold);

    public void setDepositDecay(double depositDecay);

    public void setDetectionThreshold(double detectionThreshold);

    public void setDirection(double direction);

    public void setFood(boolean food);

    public void setLocation(Location location);

    public void setWanderProbability(double wanderProbability);

    public void step();
}
