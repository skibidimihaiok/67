package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public final class PlayerEspModule extends ClientModule implements EspSource {
    private final NumberSetting range = add(NumberSetting.of("Range", 128, 16, 256, 16));
    private final BoolSetting box = add(BoolSetting.of("Box", true));
    private final BoolSetting fill = add(BoolSetting.of("Filled", true));
    private final BoolSetting tracers = add(BoolSetting.of("Tracers", true));
    private final NumberSetting lineWidth = add(NumberSetting.of("Tracer Width", 0.045, 0.02, 0.20, 0.005));
    private final ModeSetting color = add(ModeSetting.of("Color", 0, "Red", "White", "Cyan", "Yellow"));

    public PlayerEspModule() {
        super("Player ESP", "Boxes and tracer lines around other players, visible through walls.", Category.RENDER);
    }

    @Override public void collect(EspBatch batch, float pt) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) return;
        float[][] colors = {{1f,0.25f,0.25f},{1f,1f,1f},{0.3f,0.9f,1f},{1f,0.9f,0.2f}};
        float[] c = colors[color.index()];
        Vec3Eye:
        {
            var eye = client.player.getEyePosition(pt);
            for (Entity entity : client.level.entitiesForRendering()) {
                if (!(entity instanceof Player) || entity == client.player || client.player.distanceTo(entity) > range.get()) continue;
                AABB b = EspUtil.lerped(entity, pt);
                if (box.get()) batch.outline(b.minX,b.minY,b.minZ,b.maxX,b.maxY,b.maxZ,c[0],c[1],c[2],1f,0.03);
                if (fill.get()) batch.box(b.minX,b.minY,b.minZ,b.maxX,b.maxY,b.maxZ,c[0],c[1],c[2],0.18f);
                if (tracers.get()) batch.line(eye, b.getCenter(), c[0],c[1],c[2],0.95f,lineWidth.get());
            }
        }
    }
}
