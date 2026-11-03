package com.suschunkfinder;

import net.minecraft.client.Minecraft;

/** Holds sprint while the forward movement key is pressed. */
public final class SprintModule extends ClientModule {
    public SprintModule() {
        super("Sprint", "Automatically sprints while moving forward.", Category.MOVEMENT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.screen != null) return;
        client.options.keySprint.setDown(client.options.keyUp.isDown());
    }

    @Override
    protected void onDisable() {
        Minecraft client = Minecraft.getInstance();
        if (client.options != null) client.options.keySprint.setDown(false);
    }
}
