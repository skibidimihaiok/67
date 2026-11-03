package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/** Displays a warning when vanilla reports that the player is inside a solid block. */
public final class AntiPhaseModule extends ClientModule {
    private boolean insideBlock;

    public AntiPhaseModule() {
        super("Anti Phase", "Warns when your player intersects a block; it does not alter collision or bypass server checks.", Category.COMBAT);
    }

    @Override
    public void tick(Minecraft client) {
        insideBlock = client.player != null && client.player.isInWall();
    }

    @Override
    public void renderHud(GuiGraphics graphics) {
        if (insideBlock) {
            graphics.drawString(Minecraft.getInstance().font, "ION WARNING: INSIDE BLOCK", 8, 58, 0xFFFF5555);
        }
    }
}
