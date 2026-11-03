package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

/** Uses a held firework rocket while the player is gliding with an elytra. */
public final class ElytraBoostModule extends ClientModule {
    private final NumberSetting interval = add(NumberSetting.of("Rocket interval (ticks)", 20, 5, 60, 1));
    private final ModeSetting hand = add(ModeSetting.of("Rocket hand", 0, "Main hand", "Offhand"));
    private int wait;

    public ElytraBoostModule() {
        super("Elytra Boost", "Uses a held firework rocket while gliding.", Category.MOVEMENT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.gameMode == null || client.screen != null) return;
        if (!client.player.isFallFlying()) { wait = 0; return; }
        if (wait > 0) { wait--; return; }
        InteractionHand selected = hand.index() == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        if (!client.player.getItemInHand(selected).is(Items.FIREWORK_ROCKET)) return;
        client.gameMode.useItem(client.player, selected);
        wait = interval.getInt();
    }

    @Override
    protected void onDisable() { wait = 0; }
}
