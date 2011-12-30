package com.dthielke.ants;

public class GreedyAntFactory implements AntFactory<GreedyAnt> {
    private World world;
    private Location location;
    private double pheromoneDecay;
    private double wanderProbability;
    private int crowdThreshold;
    private double detectionThreshold;

    public GreedyAntFactory(World world,
                            Location location,
                            double pheromoneDecay,
                            double wanderProbability,
                            int crowdThreshold,
                            double detectionThreshold) {
        this.world = world;
        this.location = location;
        this.pheromoneDecay = pheromoneDecay;
        this.wanderProbability = wanderProbability;
        this.crowdThreshold = crowdThreshold;
        this.detectionThreshold = detectionThreshold;
    }

    @Override
    public GreedyAnt create() {
        return new GreedyAnt(world, location, pheromoneDecay, wanderProbability, crowdThreshold, detectionThreshold);
    }
}
