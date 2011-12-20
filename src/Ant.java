public interface Ant {
    public PheromoneType getDepositedPheromone();

    public Location getLocation();

    public PheromoneType getPursuedPheromone();

    public World getWorld();

    public boolean hasFood();

    public void setFood(boolean food);

    public void setLocation(Location location);

    public void step();

    public double getDirection();

    public void setDirection(double direction);
}
