import java.util.Random;

public class StandardAnt implements Ant {
    private static final Random random = new Random(System.currentTimeMillis());

    private boolean food = false;
    private double direction;
    private World world;
    private Zone zone;
    private Location location;
    private int steps = 0;

    private int maxAnts = 1;
    private double maxPheromone = 1.0;
    private double dropDecay = 0.005;
    private double threshold = .01;
    private double wanderProbability = 0.1;

    private double alpha = 100.0;
    private double beta = 1.0;

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
            // randomly wander
            attractor = selectRandom();
        } else {
            // check front first
            attractor = selectStochasticallyFromAngularRange(-1, 1);
            // check sides second
            if (attractor == null)
                attractor = selectStochasticallyFromAngularRange(2, 6);
            // if all else fails, move randomly
            if (attractor == null)
                attractor = selectRandom();
        }
        if (attractor != null) {
            // actually move
            setLocation(attractor.getLocation());
            // interact with the new zone
            attractor.interact(this);
            // increment ant age
            if (attractor.getType() == ZoneType.NEST || attractor.getType() == ZoneType.FOOD)
                steps = 0;
            else
                steps++;
            // deposit pheromones on the new zone
            depositPheromone(attractor);
        } else {
            steps++;
        }
    }

    private Zone selectStochasticallyFromAngularRange(int start, int stop) {
        int x = location.getX();
        int y = location.getY();
        PheromoneType type = getPursuedPheromone();

        Zone zones[] = new Zone[stop - start + 1];
        double probabilities[] = new double[zones.length];
        double sum = 0;

        // sweep through angular increments of PI / 4
        for (int i = start; i <= stop; i++) {
            // get the new angle
            double direction = this.direction + i * Math.PI / 4;
            // make sure the angle is between 0 and 2*PI
            if (direction < 0) direction += 2 * Math.PI;
            if (direction > 2 * Math.PI) direction -= 2 * Math.PI;
            // get the movement associated with the angle
            int dx = (int) Math.signum(Math.round(Math.cos(direction) * 100000));
            int dy = (int) Math.signum(Math.round(Math.sin(direction) * 100000));
            // get the zone at the destination
            zones[i - start] = world.getZone(x + dx, y + dy);
            // make sure the zone exists, isn't crowded, and is traversable
            if (zones[i - start] != null && zones[i - start].getAnts().size() <= maxAnts && zones[i - start].isTraversable()) {
                // calculate and store the probabilities
                double attractiveness = (direction / Math.PI * 4) % 2 == 0 ? 1 : 0.70710678118654752440084436;
                double level = zones[i - start].getPheromoneLevel(type);
                // ignore anything below our threshold
                if (level < threshold)
                    level = 0;
                probabilities[i - start] = Math.pow(level, alpha) * Math.pow(attractiveness, beta);
                sum += probabilities[i - start];
            }
        }

        // normalize probabilities
        for (int i = 0; i < probabilities.length; i++)
            probabilities[i] /= sum;

        // randomly select a zone according to the probabilities
        double rand = random.nextDouble();
        double min = 0;
        for (int i = 0; i < zones.length; i++) {
            if (rand < probabilities[i] + min)
                return zones[i];
            min += probabilities[i];
        }

        return null;
    }

    private Zone selectMaxFromAngularRange(int start, int stop) {
        int x = location.getX();
        int y = location.getY();
        PheromoneType type = getPursuedPheromone();

        Zone zones[] = new Zone[stop - start + 1];
        double levels[] = new double[zones.length];

        // sweep through angular increments of PI / 4
        for (int i = start; i <= stop; i++) {
            // get the new angle
            double direction = this.direction + i * Math.PI / 4;
            // make sure the angle is between 0 and 2*PI
            if (direction < 0) direction += 2 * Math.PI;
            if (direction > 2 * Math.PI) direction -= 2 * Math.PI;
            // get the movement associated with the angle
            int dx = (int) Math.signum(Math.round(Math.cos(direction) * 100000));
            int dy = (int) Math.signum(Math.round(Math.sin(direction) * 100000));
            // get the zone at the destination
            zones[i - start] = world.getZone(x + dx, y + dy);
            // make sure the zone exists, isn't crowded, and is traversable
            if (zones[i - start] != null && zones[i - start].getAnts().size() <= maxAnts && zones[i - start].isTraversable()) {
                // store the zone's pheromone level
                levels[i - start] = zones[i - start].getPheromoneLevel(type);
                // ignore anything below our threshold
                if (levels[i - start] < threshold)
                    levels[i - start] = 0;
            }
        }

        // find the maximum
        double max = 0;
        int maxIndex = -1;
        for (int i = 0; i < zones.length; i++) {
            if (levels[i] > max) {
                max = levels[i];
                maxIndex = i;
            }
        }

        // return the strongest neighbor
        if (maxIndex != -1)
            return zones[maxIndex];

        return null;
    }

    private Zone selectRandom() {
        Zone zone;
        boolean front = true;
        do {
            int x = location.getX();
            int y = location.getY();
            double direction;
            if (front)
                direction = this.direction + (random.nextInt(3) - 1) * Math.PI / 4;
            else
                direction = this.direction + random.nextInt(8) * Math.PI / 4;
            int dx = (int) Math.signum(Math.round(Math.cos(direction) * 100000));
            int dy = (int) Math.signum(Math.round(Math.sin(direction) * 100000));
            zone = world.getZone(x + dx, y + dy);
            front = false;
        } while (zone == null || !zone.isTraversable());
        return zone;
    }

    private void depositPheromone(Zone zone) {
        depositPheromoneTopOff(zone);
    }

    private void depositPheromoneTopOff(Zone zone) {
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

    private void depositPheromoneFixed(Zone zone) {
        PheromoneType type = getDepositedPheromone();
        double level = zone.getPheromoneLevel(type);
        zone.setPheromoneLevel(type, Math.max(level, level + maxPheromone * Math.pow(1 - dropDecay, steps)));
    }
}
