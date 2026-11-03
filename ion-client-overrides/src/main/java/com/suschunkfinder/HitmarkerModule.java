package com.suschunkfinder;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.InteractionResult;

/** Brief HUD marker after the local player attacks an entity. */
public final class HitmarkerModule extends HudModule {
    private static long lastHit;

    public HitmarkerModule() {
        super("Hitmarker", "Shows a short marker when your attack connects locally.", Category.RENDER, 400, 220);
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            Minecraft client = Minecraft.getInstance();
            if (isEnabled() && client.player == player) lastHit = System.currentTimeMillis();
            return InteractionResult.PASS;
        });
    }

    @Override
    protected void draw(GuiGraphics graphics, int x, int y) {
        if (System.currentTimeMillis() - lastHit > 250) return;
        Hud.text(graphics, "×", x, y, 0xFFFFFFFF);
    }
}
