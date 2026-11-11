package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/** Displays the player's total armor points. */
public final class ArmorHudModule extends ClientModule {
    public ArmorHudModule() {
        super("Armor HUD", "Shows your current armor points.", Category.RENDER);
    }
    @Override public void renderHud(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        String text = "ARMOR " + mc.player.getArmorValue();
        graphics.fill(6, 46, 102, 62, 0x990B111B);
        graphics.fill(6, 46, 8, 62, ThemeModule.color());
        graphics.drawString(mc.font, text, 12, 51, 0xFFF1F7FC, true);
    }
}
