package com.dthielke.ants;

public class StandardNest extends EmptyZone implements Nest {
    private final Location location;
    private int food;

    public StandardNest(Location location, int food) {
        super(location);
        this.location = location;
        this.food = food;
    }

    @Override
    public int getFood() {
        return food;
    }

    @Override
    public void setFood(int food) {
        this.food = food;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public ZoneType getType() {
        return ZoneType.NEST;
    }

    @Override
    public double getPheromoneLevel(PheromoneType type) {
        if (type == PheromoneType.NEST)
            return 1.0;
        else
            return super.getPheromoneLevel(type);
    }

    @Override
    public boolean isTraversable() {
        return true;
    }

    @Override
    public void interact(Ant ant) {
        if (ant.hasFood()) {
            ant.setFood(false);
            ant.setDirection(ant.getDirection() - Math.PI);
            food++;
        }
    }
}
