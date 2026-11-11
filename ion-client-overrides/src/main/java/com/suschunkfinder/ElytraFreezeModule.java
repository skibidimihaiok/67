package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

/** Holds local elytra velocity still while enabled. */
public final class ElytraFreezeModule extends ClientModule {
    public ElytraFreezeModule() {
        super("Elytra Freeze", "Stops local movement while elytra-gliding; disables cleanly when you stop gliding.", Category.COMBAT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player != null && client.player.isFallFlying()) {
            client.player.setDeltaMovement(Vec3.ZERO);
        }
    }
}
