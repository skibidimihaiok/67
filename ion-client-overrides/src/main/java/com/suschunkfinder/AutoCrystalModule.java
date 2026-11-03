package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;

/** Safely detonates only an end crystal directly under the crosshair. */
public final class AutoCrystalModule extends ClientModule {
    private final NumberSetting range = add(NumberSetting.of("Range", 5.0, 1.0, 6.0, 0.5));
    private final NumberSetting delay = add(NumberSetting.of("Delay (ticks)", 2, 0, 20, 1));
    private int wait;

    public AutoCrystalModule() {
        super("Auto Crystal", "Attacks only an end crystal directly under your crosshair.", Category.COMBAT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.gameMode == null || client.screen != null) return;
        if (wait > 0) { wait--; return; }
        if (!(client.crosshairPickEntity instanceof EndCrystal crystal) || !crystal.isAlive()) return;
        if (client.player.distanceTo(crystal) > range.get()) return;
        client.gameMode.attack(client.player, crystal);
        client.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        wait = delay.getInt();
    }

    @Override
    protected void onDisable() { wait = 0; }
}
