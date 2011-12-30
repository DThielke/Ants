package com.dthielke.ants;

public class StochasticAnt extends StandardAnt {
    private double alpha;
    private double beta;

    public StochasticAnt(World world,
                         Location location,
                         double pheromoneDecay,
                         double wanderProbability,
                         int crowdThreshold,
                         double detectionThreshold,
                         double pheromoneFactor,
                         double distanceFactor) {
        super(world, location, pheromoneDecay, wanderProbability, crowdThreshold, detectionThreshold);
        this.alpha = pheromoneFactor;
        this.beta = distanceFactor;
    }

    @Override
    protected Zone selectNeighboringZone(int start, int stop) {
        Location location = this.getLocation();
        int x = location.getX();
        int y = location.getY();
        PheromoneType type = getPursuedPheromone();

        Zone zones[] = new Zone[stop - start + 1];
        double probabilities[] = new double[zones.length];
        double sum = 0;

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
            if (zones[i - start] != null && zones[i - start].getAnts().size() <= getCrowdThreshold() && zones[i - start].isTraversable()) {
                // calculate and store the probabilities
                double attractiveness = (direction / Math.PI * 4) % 2 == 0 ? 1 : 0.70710678118654752440084436;
                double level = zones[i - start].getPheromoneLevel(type);
                // ignore anything below our threshold
                if (level < getDetectionThreshold())
                    level = 0;
                probabilities[i - start] = Math.pow(level, alpha) * Math.pow(attractiveness, beta);
                sum += probabilities[i - start];
            }
        }

        // normalize probabilities
        for (int i = 0; i < probabilities.length; i++)
            probabilities[i] /= sum;

        // randomly select a zone according to the probabilities
        double rand = AntSim.random.nextDouble();
        double min = 0;
        for (int i = 0; i < zones.length; i++) {
            if (rand < probabilities[i] + min)
                return zones[i];
            min += probabilities[i];
        }

        return null;
    }
}
