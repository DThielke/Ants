package com.dthielke.ants;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class AntSimApplet extends JApplet {
    private final int preferredWidth = 600;
    private final int preferredHeight = 600;
    private final int worldWidth = 100;
    private final int worldHeight = 100;
    private AntSimRenderer renderer;
    private AntSim sim;
    private boolean leftMouseDown = false;

    @Override
    public void start() {
        // Sim
        sim = new AntSim(worldWidth, worldHeight);
        int cellWidth = preferredWidth / worldWidth;
        int cellHeight = preferredHeight / worldHeight;

        // Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // Control Panel
        ControlPanel controlPanel = new ControlPanel(sim);
        controlPanel.setPreferredSize(new Dimension(200, 200));

        // Renderer
        renderer = new AntSimRenderer(sim.getWorld());
        renderer.setSize(new Dimension(cellWidth * worldWidth, cellHeight * worldHeight));
        renderer.setPreferredSize(new Dimension(cellWidth * worldWidth, cellHeight * worldHeight));
        renderer.setMaximumSize(new Dimension(cellWidth * worldWidth, cellHeight * worldHeight));
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

        // JApplet
        JPanel mainPanel = new JPanel();
        this.add(mainPanel);
        FormLayout layout = new FormLayout("4dlu, d:g, 4dlu, d:g", "p:g");
        PanelBuilder builder = new PanelBuilder(layout, mainPanel);
        CellConstraints cc = new CellConstraints();
        builder.add(controlPanel, cc.xy(2, 1, CellConstraints.FILL, CellConstraints.FILL));
        builder.add(renderer, cc.xy(4, 1, CellConstraints.FILL, CellConstraints.FILL));

        this.setSize(controlPanel.getPreferredSize().width + renderer.getPreferredSize().width + 12, controlPanel.getPreferredSize().height + renderer.getPreferredSize().height);

        sim.start();
    }

    @Override
    public void stop() {
        renderer.setRunning(false);
        sim.setRunning(false);
    }
}
