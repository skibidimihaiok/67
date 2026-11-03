package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

/** Draws a compact 2D ballistic preview for a projectile under the crosshair. */
public final class TrajectoriesModule extends ClientModule {
    private Vec3 velocity;

    public TrajectoriesModule() {
        super("Trajectories", "Draws a compact ballistic preview for a projectile under your crosshair.", Category.COMBAT);
    }

    @Override
    public void tick(Minecraft client) {
        velocity = client.crosshairPickEntity instanceof Projectile projectile
                ? projectile.getDeltaMovement() : null;
    }

    @Override
    public void renderHud(GuiGraphics graphics) {
        if (velocity == null) return;
        int x0 = 10;
        int y0 = 112;
        double horizontal = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        for (int i = 1; i <= 18; i++) {
            double t = i * 0.35;
            int x = x0 + i * 5;
            int y = y0 - (int) Math.round((velocity.y * t - 0.025 * t * t) * 8.0);
            graphics.fill(x, y, x + 2, y + 2, Hud.accent());
        }
        graphics.drawString(Minecraft.getInstance().font, "Trajectory preview", x0, y0 + 5, Hud.WHITE);
    }

    @Override
    protected void onDisable() { velocity = null; }
}
