import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AntSimMain {
    private JFrame frame;
    private final int width = 1000;
    private final int height = 1000;
    private final int worldWidth = 100;
    private final int worldHeight = 100;
    private AntSimRenderer renderer;
    private AntSim sim;

    public AntSimMain() {
        // Sim
        sim = new AntSim(worldWidth, worldHeight);
        int cellWidth = width / worldWidth;
        int cellHeight = height / worldHeight;

        // JFrame
        frame = new JFrame();
        frame.addWindowListener(new FrameClose());
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.setSize(worldWidth * cellWidth + 6, worldHeight * cellHeight + 28);
        frame.setResizable(false);
        frame.setVisible(true);

        // Canvas
        GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        Canvas canvas = new Canvas(config);
        canvas.setSize(width, height);
        frame.add(canvas, 0);
        canvas.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point cellPoint = renderer.getCellPoint(e.getPoint());
                if (cellPoint != null) {
                    Location location = new Location(cellPoint.x, cellPoint.y);
                    sim.getWorld().setZone(location, new Wall(location));
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });
        canvas.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    if (sim.isPaused()) {
                        sim.setPaused(false);
                    } else {
                        sim.setPaused(true);
                    }
                } else if (e.getButton() == MouseEvent.BUTTON1) {
                    Point cellPoint = renderer.getCellPoint(e.getPoint());
                    System.out.println(cellPoint);
                    Location location = new Location(cellPoint.x, cellPoint.y);
                    sim.getWorld().setZone(location, new Wall(location));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        // Renderer
        renderer = new AntSimRenderer(canvas, width, height, sim.getWorld());

        renderer.start();
        sim.start();
    }

    private class FrameClose extends WindowAdapter {
        @Override
        public void windowClosing(final WindowEvent e) {
            renderer.setRunning(false);
            sim.setRunning(false);
            frame.dispose();
        }
    }

    public static void main(final String[] args) {
        new AntSimMain();
    }
}
