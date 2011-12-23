import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.EnumMap;

public class AntSimRenderer extends Thread {
    private static final EnumMap<PheromoneType, Color> pheromoneColors;
    private boolean running = false;
    private Canvas canvas;
    private BufferStrategy strategy;
    private BufferedImage background;
    private Graphics2D graphics;
    private final int width, height;
    private final World world;
    private final int cellWidth, cellHeight;

    static {
        pheromoneColors = new EnumMap<PheromoneType, Color>(PheromoneType.class);
        pheromoneColors.put(PheromoneType.FOOD, new Color(0, 125, 0));
        pheromoneColors.put(PheromoneType.NEST, new Color(0, 0, 125));
    }

    public AntSimRenderer(Canvas canvas, int width, int height, World world) {
        this.canvas = canvas;
        this.width = width;
        this.height = height;
        this.world = world;
        this.cellWidth = width / world.getWidth();
        this.cellHeight = height / world.getHeight();
        this.background = create(width, height, false);
        this.canvas.createBufferStrategy(2);
        do {
            strategy = canvas.getBufferStrategy();
        } while (strategy == null);
    }

    // create a hardware accelerated image
    public final BufferedImage create(final int width, final int height, final boolean alpha) {
        return canvas.getGraphicsConfiguration().createCompatibleImage(width, height, alpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE);
    }

    // Screen and buffer stuff
    private Graphics2D getBuffer() {
        if (graphics == null) {
            try {
                graphics = (Graphics2D) strategy.getDrawGraphics();
            } catch (IllegalStateException e) {
                return null;
            }
        }
        return graphics;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void run() {
        Graphics2D backgroundGraphics = (Graphics2D) background.getGraphics();
        long fpsWait = (long) (1.0 / 30 * 1000);
        main:
        while (running) {
            long renderStart = System.nanoTime();

            // Update Graphics
            do {
                Graphics2D bg = getBuffer();
                if (!running) {
                    break main;
                }
                renderGame(backgroundGraphics);
                bg.drawImage(background, 0, 0, null);
                bg.dispose();
            } while (!updateScreen());

            // Better do some FPS limiting here
            long renderTime = (System.nanoTime() - renderStart) / 1000000;
            try {
                Thread.sleep(Math.max(0, fpsWait - renderTime));
            } catch (InterruptedException e) {
                Thread.interrupted();
                break;
            }
        }
    }

    public void renderGame(Graphics2D g) {
        int wHeight = world.getHeight() - 1;

        // background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        // zones
        for (int x = 0; x < world.getWidth(); x++) {
            for (int y = 0; y < world.getHeight(); y++) {
                Zone zone = world.getZone(new Location(x, y));
                if (zone.getType() == ZoneType.FOOD) {
                    g.setColor(Color.BLUE);
                    g.fillRect(x * cellWidth, (wHeight - y) * cellHeight, cellWidth, cellHeight);
                } else if (zone.getType() == ZoneType.NEST) {
                    g.setColor(Color.GREEN);
                    g.fillRect(x * cellWidth, (wHeight - y) * cellHeight, cellWidth, cellHeight);
                } else if (zone.getType() == ZoneType.WALL) {
                    g.setColor(Color.WHITE);
                    g.fillRect(x * cellWidth, (wHeight - y) * cellHeight, cellWidth, cellHeight);
                } else {
                    int red = 0, green = 0, blue = 0;
                    for (PheromoneType type : PheromoneType.values()) {
                        double level = zone.getPheromoneLevel(type);
                        if (level > 0) {
                            Color color = pheromoneColors.get(type);
                            red += color.getRed() * level;
                            green += color.getGreen() * level;
                            blue += color.getBlue() * level;
                        }
                    }
                    g.setColor(new Color(red, green, blue));
                    g.fillRect(x * cellWidth, (wHeight - y) * cellHeight, cellWidth, cellHeight);
                }
            }
        }

        // ants
        for (Ant ant : world.getAnts()) {
            if (ant.hasFood())
                g.setColor(Color.GREEN);
            else
                g.setColor(Color.BLUE);
            Location location = ant.getLocation();
            int w = cellWidth / 5;
            int cW = cellWidth / 2;
            int oW = cellWidth / 10;
            int h = cellHeight / 5;
            int cH = cellHeight / 2;
            int oH = cellHeight / 10;
            g.fillRect(location.getX() * cellWidth + cW - oW, (wHeight - location.getY()) * cellHeight + cH - oH, w, h);
        }

        // food count
        int food = 0;
        for (Nest nest : world.getNests())
            food += nest.getFood();
        g.setColor(Color.WHITE);
        g.drawString("Collected " + food + " food.", 10, 20);
    }

    private boolean updateScreen() {
        graphics.dispose();
        graphics = null;
        try {
            strategy.show();
            Toolkit.getDefaultToolkit().sync();
            return (!strategy.contentsLost());
        } catch (NullPointerException e) {
            return true;
        } catch (IllegalStateException e) {
            return true;
        }
    }

    @Override
    public void start() {
        super.start();
        running = true;
    }
}