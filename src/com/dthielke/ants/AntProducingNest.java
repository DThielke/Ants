package com.dthielke.ants;

public class AntProducingNest extends StandardNest {
    private World world;
    private AntFactory factory;
    private int antProductionCost;

    public AntProducingNest(World world, Location location, int food, int antProductionCost, AntFactory factory) {
        super(location, food);
        this.world = world;
        this.antProductionCost = antProductionCost;
        this.factory = factory;
    }

    @Override
    public void setFood(int food) {
        super.setFood(food);
        if (getFood() >= antProductionCost) {
            setFood(food - antProductionCost);
            Ant ant = factory.create();
            ant.setLocation(this.getLocation());
            world.addAnt(ant);
        }
    }
}
