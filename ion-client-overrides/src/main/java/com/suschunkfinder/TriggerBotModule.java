package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

/** Attacks only the living entity currently under the crosshair. */
public final class TriggerBotModule extends ClientModule {
    private final NumberSetting delay = add(NumberSetting.of("Delay (ticks)", 4, 0, 20, 1));
    private final NumberSetting range = add(NumberSetting.of("Range", 4.0, 1.0, 6.0, 0.5));
    private final ModeSetting filter = add(ModeSetting.of("Entity filter", 0, "Players", "Mobs", "Animals", "All"));
    private int wait;

    public TriggerBotModule() {
        super("Trigger Bot", "Attacks only a valid living entity under your crosshair.", Category.COMBAT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.gameMode == null || client.screen != null) return;
        if (wait > 0) { wait--; return; }
        if (!(client.crosshairPickEntity instanceof LivingEntity target) || !target.isAlive()) return;
        if (client.player.distanceTo(target) > range.get()) return;
        boolean valid = switch (filter.index()) {
            case 0 -> target instanceof Player;
            case 1 -> target instanceof Monster;
            case 2 -> target instanceof Animal;
            default -> true;
        };
        if (!valid || client.player.getAttackStrengthScale(0.5F) < 1.0F) return;
        client.gameMode.attack(client.player, target);
        client.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        wait = delay.getInt();
    }

    @Override
    protected void onDisable() {
        wait = 0;
    }
}
