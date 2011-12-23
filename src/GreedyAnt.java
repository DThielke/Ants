public class GreedyAnt extends StandardAnt {
    private int crowdThreshold;
    private double pheromoneThreshold;

    public GreedyAnt(World world,
                     Location location,
                     double pheromoneDecay,
                     double wanderProbability,
                     int crowdThreshold,
                     double pheromoneThreshold) {
        super(world, location, pheromoneDecay, wanderProbability);
        this.crowdThreshold = crowdThreshold;
        this.pheromoneThreshold = pheromoneThreshold;
    }

    @Override
    protected Zone selectNeighboringZone(int start, int stop) {
        Location location = this.getLocation();
        int x = location.getX();
        int y = location.getY();
        PheromoneType type = getPursuedPheromone();

        Zone zones[] = new Zone[stop - start + 1];
        double levels[] = new double[zones.length];

        // sweep through angular increments of PI / 4
        for (int i = start; i <= stop; i++) {
            // get the new angle
            double direction = this.getDirection() + i * Math.PI / 4;
            // make sure the angle is between 0 and 2*PI
            if (direction < 0) direction += 2 * Math.PI;
            if (direction > 2 * Math.PI) direction -= 2 * Math.PI;
            // get the movement associated with the angle
            int dx = (int) Math.signum(Math.round(Math.cos(direction) * 100000));
            int dy = (int) Math.signum(Math.round(Math.sin(direction) * 100000));
            // get the zone at the destination
            zones[i - start] = this.getWorld().getZone(x + dx, y + dy);
            // make sure the zone exists, isn't crowded, and is traversable
            if (zones[i - start] != null && zones[i - start].getAnts().size() <= crowdThreshold && zones[i - start].isTraversable()) {
                // store the zone's pheromone level
                levels[i - start] = zones[i - start].getPheromoneLevel(type);
                // ignore anything below our threshold
                if (levels[i - start] < pheromoneThreshold)
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
}
