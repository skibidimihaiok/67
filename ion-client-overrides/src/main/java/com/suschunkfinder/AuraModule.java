package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

/** Crosshair-only aura: attacks the entity the player is actually aiming at. */
public final class AuraModule extends ClientModule {
    private final NumberSetting delay = add(NumberSetting.of("Delay (ticks)", 3, 0, 20, 1));
    private final NumberSetting range = add(NumberSetting.of("Range", 4.0, 1.0, 6.0, 0.5));
    private final ModeSetting filter = add(ModeSetting.of("Entity filter", 0, "Players", "Mobs", "Animals", "All"));
    private final ModeSetting weapon = add(ModeSetting.of("Weapon", 0, "Any", "Sword", "Axe", "Mace"));
    private int wait;

    public AuraModule() {
        super("Aura", "Attacks only a valid living target directly under your crosshair.", Category.COMBAT);
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
        if (!valid || !weaponAllowed(client)) return;
        if (client.player.getAttackStrengthScale(0.5F) < 1.0F) return;
        client.gameMode.attack(client.player, target);
        client.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        wait = delay.getInt();
    }

    private boolean weaponAllowed(Minecraft client) {
        var item = client.player.getMainHandItem().getItem();
        return switch (weapon.index()) {
            case 1 -> item instanceof net.minecraft.world.item.SwordItem;
            case 2 -> item instanceof net.minecraft.world.item.AxeItem;
            case 3 -> item == net.minecraft.world.item.Items.MACE;
            default -> true;
        };
    }

    @Override
    protected void onDisable() { wait = 0; }
}
