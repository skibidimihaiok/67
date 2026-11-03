package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

/** Gives a small upward step when moving into a block while grounded. */
public final class StepModule extends ClientModule {
    private final NumberSetting height = add(NumberSetting.of("Step height", 0.45, 0.2, 0.8, 0.05));

    public StepModule() {
        super("Step", "Adds a small upward movement when you meet a low obstacle.", Category.MOVEMENT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.screen != null || !client.player.onGround()) return;
        if (!client.player.horizontalCollision) return;
        Vec3 velocity = client.player.getDeltaMovement();
        client.player.setDeltaMovement(velocity.x, height.get(), velocity.z);
    }
}
