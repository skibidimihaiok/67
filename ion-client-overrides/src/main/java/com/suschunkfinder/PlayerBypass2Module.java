package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * A local chunk-visibility diagnostic. It highlights loaded chunks outside the configured
 * simulation-distance margin; it does not bypass the server or reveal unloaded chunks.
 */
public final class PlayerBypass2Module extends ClientModule implements EspSource {
    private final NumberSetting sensitivity = add(NumberSetting.of("Sensitivity", 1, 1, 10, 1));
    private final NumberSetting range = add(NumberSetting.of("Range (chunks)", 12, 4, 32, 1));
    private int detected;

    public PlayerBypass2Module() {
        super("Player Bypass 2", "Diagnostic overlay for loaded chunks beyond the simulation-distance margin.", Category.RENDER);
    }

    @Override public void collect(EspBatch batch, float partialTick) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) return;
        int pcx = client.player.getBlockX() >> 4;
        int pcz = client.player.getBlockZ() >> 4;
        int sim = Math.max(2, client.options.simulationDistance().get());
        int margin = Math.max(1, 11 - sensitivity.getInt());
        detected = 0;
        for (LevelChunk chunk : ChunkScanner.loadedChunks()) {
            ChunkPos pos = chunk.getPos();
            int dx = pos.x - pcx, dz = pos.z - pcz;
            int distance = Math.max(Math.abs(dx), Math.abs(dz));
            if (distance <= sim + margin || distance > range.getInt()) continue;
            detected++;
            double x = pos.getMinBlockX(), z = pos.getMinBlockZ();
            double y = client.player.getY() - 0.02;
            batch.outline(x, y, z, x + 16, y + 0.05, z + 16, 1.0f, 0.72f, 0.12f, 0.9f, 0.05);
        }
    }

    @Override public void renderHud(net.minecraft.client.gui.GuiGraphics graphics) {
        if (Minecraft.getInstance().player != null) {
            graphics.drawString(Minecraft.getInstance().font, "PB2 diagnostics: " + detected + " loaded chunks",
                6, 48, 0xFFFFC65A, true);
        }
    }
}
