package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.Level;

/** Region map using ION's saved custom rectangles, styled and configured from ION's settings panel. */
public final class RegionMapModule extends HudModule {
    private static final int[] PALETTE = {0xFF4C9BE8,0xFFE8A24C,0xFF58C878,0xFFD85C8C,0xFF9C7CE8,0xFFE0D050,0xFF50C8C8,0xFFE07058};
    private final BoolSetting showMap = add(BoolSetting.of("Show Map", true));
    private final NumberSetting mapSize = add(NumberSetting.of("Map Size", 100, 60, 220, 10));
    private final NumberSetting cellSize = add(NumberSetting.of("Cell Size", 22, 10, 50, 1));
    private final NumberSetting cellGap = add(NumberSetting.of("Cell Gap", 2, 0, 10, 1));
    private final NumberSetting playerScale = add(NumberSetting.of("Player Scale", 1, 0.5, 3, 0.1));
    private final NumberSetting textScale = add(NumberSetting.of("Text Scale", 1, 0.1, 2, 0.1));
    private final NumberSetting opacity = add(NumberSetting.of("Opacity", 1, 0.1, 1, 0.05));
    private final BoolSetting shadow = add(BoolSetting.of("Shadow", true));
    private final BoolSetting showIds = add(BoolSetting.of("Show Region IDs", true));
    private final BoolSetting showLegend = add(BoolSetting.of("Show Legend", true));
    private final BoolSetting showCoordinates = add(BoolSetting.of("Show Coordinates", true));
    private final ColorSetting playerIndicatorColor = add(new ColorSetting("Player Indicator Color", -52686));

    public RegionMapModule() {
        super("Region Map", "Shows your saved region boundaries and current position.", Category.RENDER, 5, 60);
    }

    @Override protected void draw(GuiGraphics g, int x, int y) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) return;
        int px = client.player.getBlockX(), pz = client.player.getBlockZ();
        boolean overworld = client.level.dimension() == Level.OVERWORLD;
        Regions.Region current = overworld ? Regions.find(px, pz) : null;
        String line = !overworld ? "Region: unmapped (not Overworld)"
            : Regions.all().isEmpty() ? "Region: none set, use /suschunk region add"
            : current == null ? "Region: outside all regions" : "Region: " + current.name;
        int accent = current == null ? Hud.WHITE : PALETTE[Regions.all().indexOf(current) % PALETTE.length];
        int height = Hud.LINE + (showCoordinates.get() ? Hud.LINE : 0);
        int w = Math.max(Hud.width(line), showCoordinates.get() ? Hud.width("X: " + px + "  Z: " + pz) : 0);
        if (shadow.get()) g.fill(x - 5, y - 5, x + w + 6, y + height + 5, 0x66000000);
        g.fill(x - 3, y - 3, x + w + 3, y + height + 1, Hud.PANEL);
        g.fill(x - 3, y - 3, x - 1, y + height + 1, accent);
        Hud.text(g, line, x, y, accent);
        int nextY = y + Hud.LINE;
        if (showCoordinates.get()) Hud.text(g, "X: " + px + "  Z: " + pz, x, nextY, Hud.WHITE);
        if (!showMap.get() || !overworld || Regions.all().isEmpty()) return;
        drawMap(g, x, y + height + 8, px, pz);
    }

    private void drawMap(GuiGraphics g, int x, int y, int px, int pz) {
        int minX = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (Regions.Region r : Regions.all()) {
            minX = Math.min(minX, r.minX); minZ = Math.min(minZ, r.minZ);
            maxX = Math.max(maxX, r.maxX); maxZ = Math.max(maxZ, r.maxZ);
        }
        double spanX = Math.max(1, maxX - minX), spanZ = Math.max(1, maxZ - minZ);
        double scale = mapSize.get() / Math.max(spanX, spanZ);
        int mapW = Math.max(1, (int)(spanX * scale)), mapH = Math.max(1, (int)(spanZ * scale));
        if (shadow.get()) g.fill(x - 4, y - 4, x + mapW + 6, y + mapH + 6, 0x88000000);
        g.fill(x - 2, y - 2, x + mapW + 2, y + mapH + 2, 0xFF050B12);
        g.fill(x, y, x + mapW, y + mapH, 0x66202830);
        int i = 0;
        for (Regions.Region region : Regions.all()) {
            int x1 = x + (int)((region.minX - minX) * scale);
            int z1 = y + (int)((region.minZ - minZ) * scale);
            int x2 = x + (int)((region.maxX - minX) * scale);
            int z2 = y + (int)((region.maxZ - minZ) * scale);
            int color = PALETTE[i % PALETTE.length];
            int a = (int)(opacity.get() * 136.0);
            g.fill(x1 + (int)cellGap.getInt(), z1 + (int)cellGap.getInt(), Math.max(x1 + 1, x2 - cellGap.getInt()), Math.max(z1 + 1, z2 - cellGap.getInt()), (a << 24) | (color & 0x00FFFFFF));
            g.fill(x1, z1, x2, z1 + 1, color); g.fill(x1, z2 - 1, x2, z2, color);
            g.fill(x1, z1, x1 + 1, z2, color); g.fill(x2 - 1, z1, x2, z2, color);
            if (showIds.get()) {
                String label = "#" + (i + 1) + " " + Hud.fit(region.name, Math.max(18, mapW / 2));
                if (textScale.get() < 0.6) label = "#" + (i + 1);
                g.drawString(Minecraft.getInstance().font, label, x1 + 2, z1 + 2, Hud.WHITE, true);
            }
            i++;
        }
        int dotX = x + (int)Math.max(0, Math.min(mapW - 1, (px - minX) * scale));
        int dotZ = y + (int)Math.max(0, Math.min(mapH - 1, (pz - minZ) * scale));
        int radius = Math.max(2, (int)Math.round(2 * playerScale.get() * cellSize.get() / 22.0));
        g.fill(dotX - radius - 1, dotZ - radius - 1, dotX + radius + 2, dotZ + radius + 2, 0xFF000000);
        g.fill(dotX - radius, dotZ - radius, dotX + radius + 1, dotZ + radius + 1, playerIndicatorColor.argb());
        if (showLegend.get()) {
            int lx = x, ly = y + mapH + 5;
            i = 0;
            for (Regions.Region region : Regions.all()) {
                int color = PALETTE[i % PALETTE.length];
                g.fill(lx, ly + 2, lx + 5, ly + 7, color);
                String label = showIds.get() ? "#" + (i + 1) + " " + region.name : region.name;
                String fitted = Hud.fit(label, Math.max(40, mapSize.getInt()));
                g.drawString(Minecraft.getInstance().font, fitted, lx + 8, ly, Hud.WHITE, true);
                ly += Hud.LINE;
                if (ly > y + mapH + 70) break;
                i++;
            }
        }
    }
}
