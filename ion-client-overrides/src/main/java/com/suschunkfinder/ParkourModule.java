package com.suschunkfinder;

import net.minecraft.client.Minecraft;

/** Automatically jumps when the player runs into a low obstacle. */
public final class ParkourModule extends ClientModule {
    private final BoolSetting requireForward = add(BoolSetting.of("Require forward input", true));

    public ParkourModule() {
        super("Parkour", "Automatically jumps when you run into an obstacle.", Category.MOVEMENT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.screen != null || !client.player.onGround()) return;
        if (requireForward.get() && !client.options.keyUp.isDown()) return;
        if (client.player.horizontalCollision) client.player.jumpFromGround();
    }
}
