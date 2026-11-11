package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

/** Estimates same-height landing range for an ender pearl directly under the crosshair. */
public final class PearlPredictModule extends ClientModule {
    private String readout = "";

    public PearlPredictModule() {
        super("Pearl Predict", "Estimates same-height pearl landing range for the pearl under your crosshair.", Category.COMBAT);
    }

    @Override
    public void tick(Minecraft client) {
        readout = "";
        if (!(client.crosshairPickEntity instanceof Projectile projectile)) return;
        if (!projectile.getType().toString().toLowerCase(java.util.Locale.ROOT).contains("ender_pearl")) return;
        Vec3 velocity = projectile.getDeltaMovement();
        double ticks = Math.max(0.0, 2.0 * velocity.y / 0.03);
        double range = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z) * ticks;
        readout = String.format(java.util.Locale.ROOT, "Pearl estimate: %.1f blocks / %.0f ticks", range, ticks);
    }

    @Override
    public void renderHud(GuiGraphics graphics) {
        if (!readout.isEmpty()) graphics.drawString(Minecraft.getInstance().font, readout, 8, 72, Hud.accent());
    }
}
