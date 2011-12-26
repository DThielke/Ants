package com.dthielke.ants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class AntSimMain extends JApplet {
    private final int width = 800;
    private final int height = 800;
    private final int worldWidth = 100;
    private final int worldHeight = 100;
    private AntSimRenderer renderer;
    private AntSim sim;
    private boolean leftMouseDown = false;

    @Override
    public void start() {
        // Sim
        sim = new AntSim(worldWidth, worldHeight);
        int cellWidth = width / worldWidth;
        int cellHeight = height / worldHeight;

        // JApplet
        this.setSize(worldWidth * cellWidth + 6 + 200, worldHeight * cellHeight + 28);
        this.setLayout(new BorderLayout());

        // Control Panel
        this.add(new ControlPanel(sim), BorderLayout.WEST);

        // Renderer
        renderer = new AntSimRenderer(sim.getWorld());
        renderer.setSize(width, height);
        renderer.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (leftMouseDown) {
                    Point cellPoint = renderer.getCellPoint(e.getPoint());
                    if (cellPoint != null) {
                        Location location = new Location(cellPoint.x, cellPoint.y);
                        sim.getWorld().setZone(location, new Wall(location));
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {}
        });
        renderer.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println(e.getButton());
                if (e.getButton() == MouseEvent.BUTTON3) {
                    if (sim.isPaused()) {
                        sim.setPaused(false);
                    } else {
                        sim.setPaused(true);
                    }
                } else if (e.getButton() == MouseEvent.BUTTON1) {
                    Point cellPoint = renderer.getCellPoint(e.getPoint());
                    Location location = new Location(cellPoint.x, cellPoint.y);
                    sim.getWorld().setZone(location, new Wall(location));
                    leftMouseDown = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                leftMouseDown = false;
            }

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });
        this.add(renderer, BorderLayout.CENTER);

        sim.start();
    }

    @Override
    public void stop() {
        renderer.setRunning(false);
        sim.setRunning(false);
    }
}
