import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
