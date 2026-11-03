package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

/** Gives the player a small upward boost when jumping in water. */
public final class DolphinModule extends ClientModule {
    private final NumberSetting boost = add(NumberSetting.of("Swim boost", 0.35, 0.1, 0.8, 0.05));
    private boolean wasJumpDown;

    public DolphinModule() {
        super("Dolphin", "Adds a configurable swim boost when you press jump in water.", Category.MOVEMENT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.screen != null) return;
        boolean down = client.options.keyJump.isDown();
        if (down && !wasJumpDown && client.player.isInWater()) {
            Vec3 velocity = client.player.getDeltaMovement();
            client.player.setDeltaMovement(velocity.x, Math.max(velocity.y, boost.get()), velocity.z);
        }
        wasJumpDown = down;
    }

    @Override
    protected void onDisable() { wasJumpDown = false; }
}
