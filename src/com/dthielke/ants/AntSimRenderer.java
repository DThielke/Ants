package com.dthielke.ants;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;

public class AntSimRenderer extends JPanel {
    private static final EnumMap<PheromoneType, Color> pheromoneColors;
    private boolean running = true;
    private final World world;
    private int cellWidth, cellHeight;

    static {
        pheromoneColors = new EnumMap<PheromoneType, Color>(PheromoneType.class);
        pheromoneColors.put(PheromoneType.FOOD, new Color(0, 125, 0));
        pheromoneColors.put(PheromoneType.NEST, new Color(0, 0, 125));
    }

    public AntSimRenderer(World world) {
        this.world = world;
        RenderThread thread = new RenderThread();
        thread.start();
    }

    public Point getCellPoint(Point point) {
        int realCanvasWidth = cellWidth * world.getWidth();
        int realCanvasHeight = cellHeight * world.getHeight();
        if (point.x < 0 || point.x > (realCanvasWidth - 1) || point.y < 1 || point.y > (realCanvasHeight - 1))
            return null;
        return new Point(point.x / cellWidth, (realCanvasHeight - point.y) / cellHeight);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        this.cellWidth = getWidth() / world.getWidth();
        this.cellHeight = getHeight() / world.getHeight();
        int wHeight = world.getHeight() - 1;

        // background
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // zones
        for (int x = 0; x < world.getWidth(); x++) {
            for (int y = 0; y < world.getHeight(); y++) {
                Zone zone = world.getZone(new Location(x, y));
                if (zone.getType() == ZoneType.FOOD) {
                    g2d.setColor(Color.BLUE);
                    g2d.fillRect(x * cellWidth, (wHeight - y) * cellHeight, cellWidth, cellHeight);
                } else if (zone.getType() == ZoneType.NEST) {
                    g2d.setColor(Color.GREEN);
                    g2d.fillRect(x * cellWidth, (wHeight - y) * cellHeight, cellWidth, cellHeight);
                } else if (zone.getType() == ZoneType.WALL) {
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(x * cellWidth, (wHeight - y) * cellHeight, cellWidth, cellHeight);
                } else {
                    int red = 0, green = 0, blue = 0;
                    for (PheromoneType type : PheromoneType.values()) {
                        double level = Math.min(1.0, zone.getPheromoneLevel(type) / 1);
                        if (level > 0) {
                            Color color = pheromoneColors.get(type);
                            red += color.getRed() * level;
                            green += color.getGreen() * level;
                            blue += color.getBlue() * level;
                        }
                    }
                    g2d.setColor(new Color(red, green, blue));
                    g2d.fillRect(x * cellWidth, (wHeight - y) * cellHeight, cellWidth, cellHeight);
                }
            }
        }

        // ants
        for (Ant ant : world.getAnts().toArray(new Ant[world.getAnts().size()])) {
            if (ant.hasFood())
                g2d.setColor(Color.GREEN);
            else
                g2d.setColor(Color.BLUE);
            Location location = ant.getLocation();
            int w = cellWidth / 5;
            int cW = cellWidth / 2;
            int oW = cellWidth / 10;
            int h = cellHeight / 5;
            int cH = cellHeight / 2;
            int oH = cellHeight / 10;
            g2d.fillRect(location.getX() * cellWidth + cW - oW, (wHeight - location.getY()) * cellHeight + cH - oH, w, h);
        }

        // controls
        g2d.setColor(Color.WHITE);
        g2d.drawString("Click and drag to draw walls.", 10, 20);
        g2d.drawString("Right click to start and stop.", 10, 35);

        // food count
        int food = 0;
        for (Nest nest : world.getNests())
            food += nest.getFood();
        g2d.setColor(Color.WHITE);
        g2d.drawString("Collected " + food + " food.", 10, 65);

        // ant count
        g2d.setColor(Color.WHITE);
        g2d.drawString("There are " + world.getAnts().size() + " ants.", 10, 80);
    }

    class RenderThread extends Thread {
        public void run() {
            long fpsWait = (long) (1.0 / 30 * 1000);
            while (running) {
                long renderStart = System.nanoTime();
                if (!running) {
                    break;
                }
                repaint();
                long renderTime = (System.nanoTime() - renderStart) / 1000000;
                try {
                    Thread.sleep(Math.max(0, fpsWait - renderTime));
                } catch (InterruptedException e) {
                    Thread.interrupted();
                    break;
                }
            }
        }
    }
}
