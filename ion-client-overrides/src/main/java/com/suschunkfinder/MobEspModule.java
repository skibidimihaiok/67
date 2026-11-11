package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.AABB;

public final class MobEspModule extends ClientModule implements EspSource {
    private final NumberSetting range = add(NumberSetting.of("Range", 64, 16, 256, 16));
    private final BoolSetting hostile = add(BoolSetting.of("Hostile mobs", true));
    private final BoolSetting passive = add(BoolSetting.of("Passive mobs", true));
    private final BoolSetting fill = add(BoolSetting.of("Filled", false));
    private final BoolSetting tracers = add(BoolSetting.of("Tracers", false));

    public MobEspModule() {
        super("Mob ESP", "Draws boxes and optional tracer lines to mobs.", Category.RENDER);
    }

    @Override public void collect(EspBatch batch, float pt) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) return;
        var eye = client.player.getEyePosition(pt);
        for (Entity entity : client.level.entitiesForRendering()) {
            if (!(entity instanceof Mob) || client.player.distanceTo(entity) > range.get()) continue;
            boolean isHostile = entity instanceof Enemy;
            if (isHostile ? !hostile.get() : !passive.get()) continue;
            float r=isHostile?1f:0.3f, g=isHostile?0.25f:1f, b=isHostile?0.25f:0.3f;
            AABB box = EspUtil.lerped(entity, pt);
            batch.outline(box.minX,box.minY,box.minZ,box.maxX,box.maxY,box.maxZ,r,g,b,1f,0.03);
            if (fill.get()) batch.box(box.minX,box.minY,box.minZ,box.maxX,box.maxY,box.maxZ,r,g,b,0.18f);
            if (tracers.get()) batch.line(eye, box.getCenter(), r,g,b,0.9f,0.04);
        }
    }
}
