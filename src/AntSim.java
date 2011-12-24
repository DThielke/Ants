import java.util.Random;

public class AntSim extends Thread {
    public static final Random random = new Random(System.currentTimeMillis());
    private static final double pheromoneDecay = 0.0005;
    private static final double diffusion = 0.001;
    private static final int FPS = 500;
    private boolean running = false;
    private boolean paused = true;
    private World world;

    public AntSim(int width, int height) {
        world = new StandardWorld(width, height);

        Location nestCenter = new Location(width / 10, height / 10);
        Location foodCenter = new Location(width * 9 / 10, height * 9 / 10);
        int poiSize = 5;

        for (int x = -poiSize / 2; x <= poiSize / 2; x++) {
            for (int y = -poiSize / 2; y <= poiSize / 2; y++) {
                Location nestLoc = new Location(nestCenter.getX() + x, nestCenter.getY() + y);
                Location foodLoc = new Location(foodCenter.getX() + x, foodCenter.getY() + y);
                Nest nest = new StandardNest(nestLoc, 0);
                FoodSource food = new FiniteFoodSource(foodLoc, 1000);
                world.setZone(nestLoc, nest);
                world.setZone(foodLoc, food);
            }
        }

        for (int i = 0; i < 50; i++) {
            Ant ant = new GreedyAnt(world, nestCenter, 0.001, 0.10, 100, 0.001);
            world.addAnt(ant);
        }
    }

    public World getWorld() {
        return world;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        long fpsWait = (long) (1.0 / FPS * 1000);
        long t0 = System.currentTimeMillis();
        long c = 0;
        while (running) {
            long renderStart = System.nanoTime();
            if (c == 1000) {
                long t1 = System.currentTimeMillis();
                System.out.println("FPS: " + (1000 / ((t1 - t0) / 1000.0)));
                c = 0;
                t0 = System.currentTimeMillis();
            } else {
                c++;
            }

            if (!paused) {
                // move the ants
                for (Ant ant : world.getAnts()) {
                    ant.step();
                }

                // diffuse pheromones
                world.diffusePheromones(diffusion);

                // evaporate pheromones
                for (int x = 0; x < world.getWidth(); x++) {
                    for (int y = 0; y < world.getHeight(); y++) {
                        Zone zone = world.getZone(new Location(x, y));
                        for (PheromoneType type : PheromoneType.values()) {
                            double level = zone.getPheromoneLevel(type);
                            zone.setPheromoneLevel(type, level - pheromoneDecay);
                        }
                    }
                }
            }

            // limit FPS
            long renderTime = (System.nanoTime() - renderStart) / 1000000;
            try {
                Thread.sleep(Math.max(0, fpsWait - renderTime));
            } catch (InterruptedException e) {
                Thread.interrupted();
                break;
            }
        }
    }

    @Override
    public void start() {
        super.start();
        running = true;
    }
}
