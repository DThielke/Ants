public class AntSim extends Thread {
    private static final double pheromoneDecay = 0.001;
    private static final double diffusion = 0.002;
    private static final int FPS = 500;
    private boolean running = false;
    private World world;

    public AntSim(int width, int height) {
        int mode = 1;

        if (mode == 0) {
            world = new StandardWorld(5, 5);
            Location nestLoc = new Location(0, 0);
            Location foodLoc = new Location(4, 4);
            world.setZone(nestLoc, new StandardNest(nestLoc, 0));
            world.setZone(foodLoc, new FiniteFoodSource(foodLoc, 10));

            for (int i = 0; i < 5; i++) {
                Zone zone = world.getZone(new Location(i, i));
                zone.setPheromoneLevel(PheromoneType.FOOD, i / 5.0);
            }

            Ant ant = new StandardAnt(world, nestLoc);
            ant.setDirection(7 * Math.PI / 4);
            world.addAnt(ant);
        } else if (mode == 1) {
            world = new StandardWorld(width, height);

            Location nestCenter = new Location(width / 10, height / 10);
            Location foodCenter = new Location(width * 9 / 10, height * 9 / 10);
            int poiSize = 5;

            double str = 1.0;
            for (int y = height / 10; y <= height * 9 / 10; y++) {
                world.getZone(width / 10, y).setPheromoneLevel(PheromoneType.FOOD, str);
                str *= 0.99;
            }

            for (int x = width / 10; x <= width * 9 / 10; x++) {
                world.getZone(x, height * 9 / 10).setPheromoneLevel(PheromoneType.FOOD, str);
                str *= 0.99;
            }

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

            Location wallCenter = new Location(width / 2, height / 2);
            for (int i = -30; i <= 30; i++) {
                Location wallLoc = new Location(wallCenter.getX() + i, wallCenter.getY());
                Wall wall = new Wall(wallLoc);
                world.setZone(wallLoc, wall);
            }

            Location wallLeft = new Location(0, height / 2);
            for (int i = 0; i <= 15; i++) {
                Location wallLoc = new Location(wallLeft.getX() + i, wallLeft.getY());
                Wall wall = new Wall(wallLoc);
                world.setZone(wallLoc, wall);
            }

            Location wallRight = new Location(width - 1, height / 2 + height / 5);
            for (int i = -30; i <= 0; i++) {
                Location wallLoc = new Location(wallRight.getX() + i, wallRight.getY());
                Wall wall = new Wall(wallLoc);
                world.setZone(wallLoc, wall);
            }

            Location wallCenter2 = new Location(width / 2 - width / 5, height / 2 + height / 5);
            for (int i = -15; i <= 15; i++) {
                Location wallLoc = new Location(wallCenter2.getX() + i, wallCenter2.getY());
                Wall wall = new Wall(wallLoc);
                world.setZone(wallLoc, wall);
            }

            for (int i = 0; i < 50; i++) {
                Ant ant = new StandardAnt(world, nestCenter);
                world.addAnt(ant);
            }
        }
    }

    public World getWorld() {
        return world;
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
                        zone.setPheromoneLevel(type, level * (1 - pheromoneDecay));
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
