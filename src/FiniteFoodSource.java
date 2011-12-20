public class FiniteFoodSource extends EmptyZone implements FoodSource{
    private int food;

    public FiniteFoodSource(Location location, int food) {
        super(location);
        this.food = food;
    }

    @Override
    public int getFood() {
        return food;
    }

    @Override
    public boolean hasFood() {
        return food > 0;
    }

    @Override
    public ZoneType getType() {
        return ZoneType.FOOD;
    }

    @Override
    public void setFoodCount(int food) {
        this.food = food;
    }

    @Override
    public boolean isTraversable() {
        return true;
    }

    @Override
    public double getPheromoneLevel(PheromoneType type) {
        if (type == PheromoneType.FOOD && hasFood())
            return 1.0;
        else
            return super.getPheromoneLevel(type);
    }

    @Override
    public void interact(Ant ant) {
        if (hasFood() && !ant.hasFood()) {
            ant.setFood(true);
            ant.setDirection(ant.getDirection() - Math.PI);
            food--;
        }
    }
}
