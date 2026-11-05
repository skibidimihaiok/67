package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.Level;

/** DonutSMP's 9x9 regional map with a live position indicator, styled in ION's HUD. */
public final class RegionMapModule extends HudModule {
    private final NumberSetting cellSize = add(NumberSetting.of("Cell Size", 22, 10, 50, 1));
    private final NumberSetting cellGap = add(NumberSetting.of("Cell Gap", 2, 0, 10, 1));
    private final NumberSetting playerScale = add(NumberSetting.of("Player Scale", 1, 0.5, 3, 0.1));
    private final NumberSetting textScale = add(NumberSetting.of("Text Scale", 1, 0.1, 2, 0.1));
    private final NumberSetting opacity = add(NumberSetting.of("Opacity", 1, 0.1, 1, 0.05));
    private final BoolSetting shadow = add(BoolSetting.of("Shadow", true));
    private final BoolSetting showRegionIds = add(BoolSetting.of("Show Region IDs", true));
    private final BoolSetting showLegend = add(BoolSetting.of("Show Legend", true));
    private final BoolSetting showCoordinates = add(BoolSetting.of("Show Coordinates", true));
    private final ColorSetting playerIndicatorColor = add(new ColorSetting("Player Indicator Color", -52686));
    // Kept for users of ION's earlier rectangle-based map settings.
    private final BoolSetting showMap = add(BoolSetting.of("Show Map", true));
    private final NumberSetting mapSize = add(NumberSetting.of("Map Size", 220, 90, 450, 10));

    public RegionMapModule() {
        super("Region Map", "Displays the DonutSMP 9x9 region grid and your position.", Category.RENDER, 5, 60);
    }

    @Override protected void draw(GuiGraphics g, int x, int y) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) return;
        int px = client.player.getBlockX();
        int pz = client.player.getBlockZ();
        boolean overworld = client.level.dimension() == Level.OVERWORLD;
        int regionId = overworld ? DonutRegionMapData.regionAt(px, pz) : -1;
        String regionType = overworld ? DonutRegionMapData.regionTypeName(px, pz) : "Unknown";
        String heading = !overworld ? "Region: unavailable outside Overworld"
            : regionId < 0 ? "Region: outside Donut map"
            : "Region: #" + regionId + "  " + regionType;
        int headingColor = regionId < 0 ? Hud.WHITE : DonutRegionMapData.regionColor(DonutRegionMapData.regionInfo(
            DonutRegionMapData.worldToGrid(px, pz)[1] * DonutRegionMapData.MAP_SIZE
            + DonutRegionMapData.worldToGrid(px, pz)[0]).regionType());
        String coordinates = "X: " + px + "  Z: " + pz;
        int infoHeight = showCoordinates.get() ? Hud.LINE * 2 : Hud.LINE;
        int width = Math.max(Hud.width(heading), showCoordinates.get() ? Hud.width(coordinates) : 0);
        if (shadow.get()) g.fill(x - 5, y - 5, x + width + 7, y + infoHeight + 5, 0x66000000);
        g.fill(x - 3, y - 3, x + width + 4, y + infoHeight + 2, Hud.PANEL);
        g.fill(x - 3, y - 3, x - 1, y + infoHeight + 2, headingColor);
        Hud.text(g, heading, x, y, headingColor);
        if (showCoordinates.get()) Hud.text(g, coordinates, x, y + Hud.LINE, Hud.WHITE);
        if (!showMap.get() || !overworld) return;
        drawMap(g, x, y + infoHeight + 8, px, pz);
    }

    private void drawMap(GuiGraphics g, int x, int y, int px, int pz) {
        int cell = Math.max(8, Math.min(cellSize.getInt(), mapSize.getInt() / 9));
        int gap = cellGap.getInt();
        int total = DonutRegionMapData.MAP_SIZE * cell + (DonutRegionMapData.MAP_SIZE - 1) * gap;
        if (shadow.get()) g.fill(x - 5, y - 5, x + total + 5, y + total + 5, 0x77000000);
        g.fill(x - 2, y - 2, x + total + 2, y + total + 2, 0xFF080D15);
        double opacityValue = opacity.get();
        for (int row = 0; row < DonutRegionMapData.MAP_SIZE; row++) {
            for (int col = 0; col < DonutRegionMapData.MAP_SIZE; col++) {
                DonutRegionMapData.RegionInfo info = DonutRegionMapData.regionInfo(row * DonutRegionMapData.MAP_SIZE + col);
                if (info == null) continue;
                int color = DonutRegionMapData.regionColor(info.regionType());
                int sx = x + col * (cell + gap), sy = y + row * (cell + gap);
                int alpha = (int)(opacityValue * 255.0);
                int fill = (alpha << 24) | (color & 0x00FFFFFF);
                g.fill(sx, sy, sx + cell, sy + cell, fill);
                g.fill(sx, sy, sx + cell, sy + 1, color);
                g.fill(sx, sy + cell - 1, sx + cell, sy + cell, color);
                g.fill(sx, sy, sx + 1, sy + cell, color);
                g.fill(sx + cell - 1, sy, sx + cell, sy + cell, color);
                if (showRegionIds.get() && cell >= 15) {
                    float scale = (float)textScale.get();
                    g.pose().pushPose();
                    int cx = sx + cell / 2, cy = sy + cell / 2;
                    g.pose().translate(cx, cy, 0);
                    g.pose().scale(scale, scale, 1.0f);
                    g.drawCenteredString(clientFont(), Integer.toString(info.regionId()), 0, -4, 0xFFFFFFFF);
                    g.pose().popPose();
                }
            }
        }

        int[] grid = DonutRegionMapData.worldToGrid(px, pz);
        if (grid[0] >= 0 && grid[0] < 9 && grid[1] >= 0 && grid[1] < 9) {
            double[] local = DonutRegionMapData.worldToCellPosition(px, pz);
            int dotX = x + grid[0] * (cell + gap) + (int)(local[0] * cell);
            int dotY = y + grid[1] * (cell + gap) + (int)(local[1] * cell);
            int radius = Math.max(2, (int)Math.round(2.0 * playerScale.get()));
            g.fill(dotX - radius - 1, dotY - radius - 1, dotX + radius + 2, dotY + radius + 2, 0xFF000000);
            g.fill(dotX - radius, dotY - radius, dotX + radius + 1, dotY + radius + 1, playerIndicatorColor.argb());
        }
        if (showLegend.get()) {
            String[] names = DonutRegionMapData.regionTypeNames();
            int[] colors = DonutRegionMapData.regionTypeColors();
            int legendY = y + total + 5;
            int colW = Math.max(90, total / 2);
            for (int i = 0; i < names.length; i++) {
                int col = i % 2, row = i / 2;
                int lx = x + col * colW, ly = legendY + row * Hud.LINE;
                g.fill(lx, ly + 2, lx + 6, ly + 8, colors[i]);
                Hud.text(g, names[i], lx + 10, ly, Hud.WHITE);
            }
        }
    }

    private static net.minecraft.client.gui.Font clientFont() {
        return Minecraft.getInstance().font;
    }
}
