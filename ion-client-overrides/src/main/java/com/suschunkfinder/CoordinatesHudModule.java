package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/** Shows the player's current coordinates in a compact HUD line. */
public final class CoordinatesHudModule extends ClientModule {
    public CoordinatesHudModule() {
        super("Coordinates", "Displays your current XYZ position.", Category.RENDER);
    }
    @Override public void renderHud(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        String text = String.format("XYZ %.1f  %.1f  %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
        graphics.fill(6, 6, 150, 22, 0x990B111B);
        graphics.fill(6, 6, 8, 22, ThemeModule.color());
        graphics.drawString(mc.font, text, 12, 11, 0xFFF1F7FC, true);
    }
}
