package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

/** Displays a simple ballistic estimate for the projectile directly under the crosshair. */
public final class ProjectilePredictModule extends ClientModule {
    private String readout = "";

    public ProjectilePredictModule() {
        super("Projectile Predict", "Estimates same-height flight range for the projectile under your crosshair.", Category.COMBAT);
    }

    @Override
    public void tick(Minecraft client) {
        readout = "";
        if (!(client.crosshairPickEntity instanceof Projectile projectile)) return;
        Vec3 velocity = projectile.getDeltaMovement();
        double horizontal = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        double ticks = Math.max(0.0, 2.0 * velocity.y / 0.05);
        double range = horizontal * ticks;
        readout = String.format(java.util.Locale.ROOT, "Projectile estimate: %.1f blocks / %.0f ticks", range, ticks);
    }

    @Override
    public void renderHud(GuiGraphics graphics) {
        if (!readout.isEmpty()) graphics.drawString(Minecraft.getInstance().font, readout, 8, 84, Hud.WHITE);
    }
}
