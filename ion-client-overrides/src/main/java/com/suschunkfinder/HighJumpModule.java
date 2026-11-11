package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

/** Adjustable jump boost activated by the normal jump key. */
public final class HighJumpModule extends ClientModule {
    private final NumberSetting multiplier = add(NumberSetting.of("Jump multiplier", 1.5, 1.1, 3.0, 0.1));
    private boolean wasJumpDown;

    public HighJumpModule() {
        super("High Jump", "Boosts a normal grounded jump.", Category.MOVEMENT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.screen != null) return;
        boolean down = client.options.keyJump.isDown();
        if (down && !wasJumpDown && client.player.onGround()) {
            Vec3 velocity = client.player.getDeltaMovement();
            client.player.setDeltaMovement(velocity.x, 0.42 * multiplier.get(), velocity.z);
        }
        wasJumpDown = down;
    }

    @Override
    protected void onDisable() { wasJumpDown = false; }
}
