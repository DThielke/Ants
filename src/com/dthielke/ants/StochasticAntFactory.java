package com.dthielke.ants;

public class StochasticAntFactory implements AntFactory<StochasticAnt> {
    private World world;
    private Location location;
    private double pheromoneDecay;
    private double wanderProbability;
    private int crowdThreshold;
    private double detectionThreshold;
    private double pheromoneFactor;
    private double distanceFactor;

    public StochasticAntFactory(World world,
                                Location location,
                                double pheromoneDecay,
                                double wanderProbability,
                                int crowdThreshold,
                                double detectionThreshold,
                                double pheromoneFactor,
                                double distanceFactor) {
        this.world = world;
        this.location = location;
        this.pheromoneDecay = pheromoneDecay;
        this.wanderProbability = wanderProbability;
        this.crowdThreshold = crowdThreshold;
        this.detectionThreshold = detectionThreshold;
        this.pheromoneFactor = pheromoneFactor;
        this.distanceFactor = distanceFactor;
    }

    @Override
    public StochasticAnt create() {
        return new StochasticAnt(world, location, pheromoneDecay, wanderProbability, crowdThreshold, detectionThreshold, pheromoneFactor, distanceFactor);
    }
}

