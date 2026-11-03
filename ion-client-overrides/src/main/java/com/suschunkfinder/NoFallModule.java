package com.suschunkfinder;

import net.minecraft.client.Minecraft;

/** Resets local fall distance to avoid vanilla fall damage when this module is enabled. */
public final class NoFallModule extends ClientModule {
    public NoFallModule() {
        super("No Fall", "Resets fall distance while enabled.", Category.MOVEMENT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player != null) client.player.fallDistance = 0.0F;
    }
}
