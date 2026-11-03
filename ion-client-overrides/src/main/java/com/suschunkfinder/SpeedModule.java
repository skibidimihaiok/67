package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

/** Applies a configurable horizontal movement multiplier locally. */
public final class SpeedModule extends ClientModule {
    private final NumberSetting multiplier = add(NumberSetting.of("Multiplier", 1.15, 1.05, 1.5, 0.05));

    public SpeedModule() {
        super("Speed", "Adjusts horizontal movement speed while walking on the ground.", Category.MOVEMENT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.screen != null || !client.player.onGround()) return;
        if (!client.options.keyUp.isDown() && !client.options.keyDown.isDown()
                && !client.options.keyLeft.isDown() && !client.options.keyRight.isDown()) return;
        Vec3 velocity = client.player.getDeltaMovement();
        client.player.setDeltaMovement(velocity.x * multiplier.get(), velocity.y, velocity.z * multiplier.get());
    }
}
