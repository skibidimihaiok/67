package com.suschunkfinder;

import net.minecraft.client.Minecraft;

/** Holds jump while enabled, allowing vanilla auto-jump timing to handle repeated jumps. */
public final class AutoJumpModule extends ClientModule {
    public AutoJumpModule() {
        super("Auto Jump", "Automatically jumps while the module is enabled.", Category.MOVEMENT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player != null && client.screen == null) {
            client.options.keyJump.setDown(true);
        }
    }

    @Override
    protected void onDisable() {
        Minecraft client = Minecraft.getInstance();
        if (client.options != null) client.options.keyJump.setDown(false);
    }
}
