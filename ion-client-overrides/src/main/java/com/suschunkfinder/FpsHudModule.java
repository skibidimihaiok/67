package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/** Shows the current client frame rate. */
public final class FpsHudModule extends ClientModule {
    public FpsHudModule() {
        super("FPS Counter", "Displays the current frames per second.", Category.RENDER);
    }
    @Override public void renderHud(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        String text = "FPS " + mc.getFps();
        graphics.fill(6, 26, 90, 42, 0x990B111B);
        graphics.fill(6, 26, 8, 42, ThemeModule.color());
        graphics.drawString(mc.font, text, 12, 31, 0xFFF1F7FC, true);
    }
}
