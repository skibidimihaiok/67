package com.suschunkfinder;

import net.minecraft.client.Minecraft;

/** Holds sprint while forward movement is requested. */
public final class SprintModule extends ClientModule {
    public SprintModule() {
        super("Sprint", "Automatically sprints while moving forward.", Category.MOVEMENT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.screen != null) return;
        boolean movingForward = client.player.input != null && client.player.input.forwardImpulse > 0.0F;
        client.options.keySprint.setDown(movingForward);
    }

    @Override
    protected void onDisable() {
        Minecraft client = Minecraft.getInstance();
        if (client.options != null) client.options.keySprint.setDown(false);
    }
}
