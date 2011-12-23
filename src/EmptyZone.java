import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EmptyZone implements Zone {
    private final Location location;
    private final Map<PheromoneType, Double> pheromones = new EnumMap<PheromoneType, Double>(PheromoneType.class);
    private final Set<Ant> ants = new HashSet<Ant>();

    public EmptyZone(Location location) {
        this.location = location;
        for (PheromoneType type : PheromoneType.values())
            pheromones.put(type, 0.0);
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public Set<Ant> getAnts() {
        return ants;
    }

    @Override
    public void addAnt(Ant ant) {
        ants.add(ant);
    }

    @Override
    public boolean isTraversable() {
        return true;
    }

    @Override
    public void removeAnt(Ant ant) {
        ants.remove(ant);
    }

    @Override
    public ZoneType getType() {
        return ZoneType.EMPTY;
    }

    @Override
    public double getPheromoneLevel(PheromoneType type) {
        return pheromones.get(type);
    }

    @Override
    public void interact(Ant ant) {}

    @Override
    public void setPheromoneLevel(PheromoneType type, double level) {
        pheromones.put(type, level < 0 ? 0 : level);
        //pheromones.put(type, level > 1 ? 1 : level < 0 ? 0 : level);
    }
}
