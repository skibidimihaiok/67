package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

/** Waits for a legitimate falling-attack window, then attacks the crosshair target. */
public final class CriticalsModule extends ClientModule {
    private final NumberSetting delay = add(NumberSetting.of("Delay (ticks)", 2, 0, 10, 1));
    private int wait;

    public CriticalsModule() {
        super("Criticals", "Times crosshair attacks for natural falling critical hits; sends no spoof packets.", Category.COMBAT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.gameMode == null || client.screen != null) return;
        if (wait > 0) { wait--; return; }
        if (client.player.onGround() || client.player.fallDistance <= 0.0F
                || client.player.isInWater() || client.player.onClimbable()) return;
        if (!(client.crosshairPickEntity instanceof LivingEntity target) || !target.isAlive()) return;
        if (client.player.distanceTo(target) > 4.0 || client.player.getAttackStrengthScale(0.5F) < 1.0F) return;
        client.gameMode.attack(client.player, target);
        client.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        wait = delay.getInt();
    }

    @Override
    protected void onDisable() { wait = 0; }
}
