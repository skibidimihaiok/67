package com.suschunkfinder;

import java.util.Comparator;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

/** ION's configurable nearby-target aura. It does not spoof packets or evade server checks. */
public final class KillAuraModule extends ClientModule {
    private final BoolSetting players = add(BoolSetting.of("Players", true));
    private final BoolSetting hostiles = add(BoolSetting.of("Hostiles", false));
    private final ModeSetting priority = add(ModeSetting.of("Priority", 0, "Distance", "Health", "Angle"));
    private final ModeSetting crits = add(ModeSetting.of("Crits", 0, "Off", "Wait", "Jump"));
    private final NumberSetting range = add(NumberSetting.of("Range", 4.2, 2.0, 6.0, 0.1));
    private final NumberSetting delay = add(NumberSetting.of("Delay (ticks)", 0, 0, 20, 1));
    private int waitTicks;

    public KillAuraModule() {
        super("Kill Aura", "Targets nearby players or hostile mobs using normal client attacks.", Category.COMBAT);
    }

    @Override public void tick(Minecraft client) {
        if (client.player == null || client.level == null || client.gameMode == null || client.screen != null) return;
        if (waitTicks > 0) { waitTicks--; return; }
        LivingEntity target = client.level.entitiesForRendering().stream()
            .filter(e -> e instanceof LivingEntity)
            .map(e -> (LivingEntity)e)
            .filter(this::allowed)
            .filter(e -> e != client.player && e.isAlive() && e.isAttackable())
            .filter(e -> client.player.distanceTo(e) <= range.get())
            .filter(e -> client.player.hasLineOfSight(e))
            .min(comparator(client)).orElse(null);
        if (target == null) return;
        if (crits.index() == 1 && (client.player.onGround() || client.player.fallDistance <= 0.0f)) return;
        if (crits.index() == 2 && client.player.onGround()) { client.player.jumpFromGround(); return; }
        if (client.player.getAttackStrengthScale(0.5f) < 1.0f) return;
        client.gameMode.attack(client.player, target);
        client.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        waitTicks = delay.getInt();
    }

    private boolean allowed(LivingEntity entity) {
        if (entity instanceof Player p) {
            return players.get() && !NameList.FRIENDS.contains(p.getGameProfile().getName());
        }
        return hostiles.get() && entity instanceof Enemy;
    }

    private Comparator<LivingEntity> comparator(Minecraft client) {
        return switch (priority.index()) {
            case 1 -> Comparator.comparingDouble(LivingEntity::getHealth);
            case 2 -> Comparator.comparingDouble(e -> -client.player.getLookAngle().dot(e.position().subtract(client.player.getEyePosition()).normalize()));
            default -> Comparator.comparingDouble(client.player::distanceTo);
        };
    }

    @Override protected void onDisable() { waitTicks = 0; }
}
