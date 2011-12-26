package com.dthielke.ants;

public abstract class StandardAnt implements Ant {
    private boolean food = false;
    private double direction;
    private World world;
    private Zone zone;
    private Location location;
    private int crowdThreshold;
    private double detectionThreshold;
    private double depositDecay;
    private double wanderProbability;

    public StandardAnt(World world, Location location, double depositDecay, double wanderProbability, int crowdThreshold, double detectionThreshold) {
        this.world = world;
        this.depositDecay = depositDecay;
        this.wanderProbability = wanderProbability;
        this.crowdThreshold = crowdThreshold;
        this.detectionThreshold = detectionThreshold;
        setLocation(location);
        setRandomDirection();
    }

    protected void setRandomDirection() {
        this.direction = AntSim.random.nextInt(8) * Math.PI / 4.0;
    }

    @Override
    public int getCrowdThreshold() {
        return crowdThreshold;
    }

    @Override
    public void setCrowdThreshold(int crowdThreshold) {
        this.crowdThreshold = crowdThreshold;
    }

    @Override
    public double getDepositDecay() {
        return depositDecay;
    }

    @Override
    public void setDepositDecay(double depositDecay) {
        this.depositDecay = depositDecay;
    }

    @Override
    public PheromoneType getDepositedPheromone() {
        return food ? PheromoneType.FOOD : PheromoneType.NEST;
    }

    @Override
    public double getDetectionThreshold() {
        return detectionThreshold;
    }

    @Override
    public void setDetectionThreshold(double detectionThreshold) {
        this.detectionThreshold = detectionThreshold;
    }

    @Override
    public double getDirection() {
        return direction;
    }

    @Override
    public void setDirection(double direction) {
        this.direction = direction;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        if (this.location != null) {
            int dx = location.getX() - this.location.getX();
            int dy = location.getY() - this.location.getY();
            this.direction = Math.atan2(dy, dx);
        }
        this.location = location;
        if (zone != null)
            zone.removeAnt(this);
        zone = world.getZone(location);
        zone.addAnt(this);
    }

    @Override
    public PheromoneType getPursuedPheromone() {
        return food ? PheromoneType.NEST : PheromoneType.FOOD;
    }

    @Override
    public double getWanderProbability() {
        return wanderProbability;
    }

    @Override
    public void setWanderProbability(double wanderProbability) {
        this.wanderProbability = wanderProbability;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public boolean hasFood() {
        return food;
    }

    @Override
    public void setFood(boolean food) {
        this.food = food;
    }

    @Override
    public void step() {
        Zone attractor;
        if (AntSim.random.nextDouble() < wanderProbability) {
            // randomly wander
            attractor = selectRandomNeighboringZone();
        } else {
            // check front first
            attractor = selectNeighboringZone(-1, 1);
            // check sides second
            if (attractor == null)
                attractor = selectNeighboringZone(2, 6);
            // if all else fails, move randomly
            if (attractor == null)
                attractor = selectRandomNeighboringZone();
        }
        if (attractor != null) {
            // actually move
            setLocation(attractor.getLocation());
            // interact with the new zone
            attractor.interact(this);
            // deposit pheromones on the new zone
            depositPheromone(attractor);
        }
    }

    protected abstract Zone selectNeighboringZone(int start, int stop);

    protected Zone selectRandomNeighboringZone() {
        Zone zone;
        boolean front = true;
        do {
            int x = location.getX();
            int y = location.getY();
            double direction;
            if (front)
                direction = this.direction + (AntSim.random.nextInt(3) - 1) * Math.PI / 4;
            else
                direction = this.direction + AntSim.random.nextInt(8) * Math.PI / 4;
            int dx = (int) Math.signum(Math.round(Math.cos(direction) * 100000));
            int dy = (int) Math.signum(Math.round(Math.sin(direction) * 100000));
            zone = world.getZone(x + dx, y + dy);
            front = false;
        } while (zone == null || !zone.isTraversable());
        return zone;
    }

    protected void depositPheromone(Zone zone) {
        PheromoneType type = getDepositedPheromone();

        int x = zone.getLocation().getX();
        int y = zone.getLocation().getY();

        // find the maximum neighboring pheromone level
        double max = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (!(dx == 0 && dy == 0)) {
                    Zone neighbor = world.getZone(x + dx, y + dy);
                    if (neighbor != null) {
                        double level = neighbor.getPheromoneLevel(type);
                        if (level > max)
                            max = level;
                    }
                }
            }
        }

        // calculate the amount needed to top off the current zone
        double level = zone.getPheromoneLevel(type);
        double deposit = Math.max(0, max - level);

        // add the calculated amount of pheromones minus a constant
        zone.setPheromoneLevel(type, Math.max(level, level + deposit - depositDecay));
        //zone.setPheromoneLevel(type, level + depositDecay);
    }
}
