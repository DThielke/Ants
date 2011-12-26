package com.dthielke.ants;

import java.awt.*;

public class WorldRenderer {
    private World world;

    public WorldRenderer(World world) {
        this.world = world;
    }

    public void render(Graphics2D graphics) {
        graphics.setPaint(Color.WHITE);
        graphics.fill(graphics.getClipBounds());
    }
}
