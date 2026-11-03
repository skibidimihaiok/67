package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;

/** Compact HUD for the living entity under the crosshair. */
public final class TargetHudModule extends HudModule {
    private final BoolSetting showDistance = add(BoolSetting.of("Show distance", true));

    public TargetHudModule() {
        super("Target HUD", "Shows the name and health of the crosshair target.", Category.RENDER, 5, 230);
    }

    @Override
    protected void draw(GuiGraphics graphics, int x, int y) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || !(client.crosshairPickEntity instanceof LivingEntity target)
                || !target.isAlive()) return;
        int width = 150;
        int height = showDistance.get() ? 38 : 30;
        graphics.fill(x - 4, y - 4, x + width, y + height, Hud.PANEL);
        graphics.fill(x - 4, y - 4, x + width, y - 2, Hud.accent());
        Hud.text(graphics, Hud.fit(target.getName().getString(), width - 10), x, y, Hud.WHITE);
        float health = Math.max(0.0F, target.getHealth());
        float maxHealth = Math.max(1.0F, target.getMaxHealth());
        int barWidth = Math.min(width - 10, Math.round((width - 10) * health / maxHealth));
        graphics.fill(x, y + 13, x + width - 10, y + 17, 0xFF34313D);
        graphics.fill(x, y + 13, x + barWidth, y + 17, 0xFFB45CFF);
        String detail = String.format("%.1f / %.1f HP", health, maxHealth);
        Hud.text(graphics, detail, x, y + 20, Hud.WHITE);
        if (showDistance.get()) {
            Hud.text(graphics, String.format("%.1f blocks", client.player.distanceTo(target)),
                    x, y + 30, 0xFFB9B4C7);
        }
    }
}
