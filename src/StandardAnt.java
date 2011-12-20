import java.util.Random;

public class StandardAnt implements Ant {
    private static final Random random = new Random(System.currentTimeMillis());

    private boolean food = false;
    private double direction;
    private World world;
    private Zone zone;
    private Location location;

    private int maxAnts = 10;
    private double dropDecay = 0.001;
    private double threshold = .01;
    private double wanderProbability = 0.1;

    public StandardAnt(World world, Location location) {
        this.world = world;
        setLocation(location);
        setRandomDirection();
    }

    private void setRandomDirection() {
        this.direction = random.nextInt(8) * Math.PI / 4.0;
    }

    @Override
    public PheromoneType getDepositedPheromone() {
        return food ? PheromoneType.FOOD : PheromoneType.NEST;
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
        if (random.nextDouble() < wanderProbability) {
            attractor = selectRandom();
        } else {
            // check front first
            attractor = selectFromAngularRange(-1, 1);
            // check sides second
            if (attractor == null)
                attractor = selectFromAngularRange(2, 6);
            if (attractor == null)
                attractor = selectRandom();
        }
        if (attractor != null) {
            setLocation(attractor.getLocation());
            attractor.interact(this);
            depositPheromone(attractor);
        }
    }

    private Zone selectRandom() {
        Zone zone;
        do {
            int x = location.getX();
            int y = location.getY();
            double direction = this.direction + random.nextInt(8) * Math.PI / 4;
            int dx = (int) Math.signum(Math.round(Math.cos(direction) * 100000));
            int dy = (int) Math.signum(Math.round(Math.sin(direction) * 100000));
            zone = world.getZone(x + dx, y + dy);
        } while (zone == null || !zone.isTraversable());
        return zone;
    }

    private Zone selectFromAngularRange(int start, int stop) {
        int x = location.getX();
        int y = location.getY();
        PheromoneType type = getPursuedPheromone();

        Zone zones[] = new Zone[stop - start + 1];
        double levels[] = new double[zones.length];
        double total = 0;

        for (int i = start; i <= stop; i++) {
            double direction = this.direction + i * Math.PI / 4;
            if (direction < 0) direction += 2 * Math.PI;
            if (direction > 2 * Math.PI) direction -= 2 * Math.PI;
            int dx = (int) Math.signum(Math.round(Math.cos(direction) * 100000));
            int dy = (int) Math.signum(Math.round(Math.sin(direction) * 100000));
            zones[i - start] = world.getZone(x + dx, y + dy);
            if (zones[i - start] != null && zones[i - start].getAnts().size() <= maxAnts && zones[i - start].isTraversable()) {
                levels[i - start] = zones[i - start].getPheromoneLevel(type);
                total += levels[i - start];
            }
        }

        if (total < zones.length * threshold)
            return null;

        for (int i = 0; i < zones.length; i++)
            levels[i] /= total;

        double max = 0;
        int maxIndex = -1;
        for (int i = 0; i < zones.length; i++) {
            if (levels[i] > max) {
                max = levels[i];
                maxIndex = i;
            }
        }

        if (maxIndex != -1)
            return zones[maxIndex];

        return null;
    }

    private void depositPheromone(Zone zone) {
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
        zone.setPheromoneLevel(type, level + deposit - dropDecay);
    }
}
