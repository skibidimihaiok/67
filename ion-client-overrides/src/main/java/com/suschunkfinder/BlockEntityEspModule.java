package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

/** ESP for block entities not covered by Storage ESP. */
public final class BlockEntityEspModule extends ClientModule implements EspSource {
    private final NumberSetting range = add(NumberSetting.of("Range (chunks)", 8, 1, 32, 1));
    private final BoolSetting spawners = add(BoolSetting.of("Spawners", true));
    private final BoolSetting signs = add(BoolSetting.of("Signs", true));
    private final BoolSetting others = add(BoolSetting.of("Other blocks", false));
    private final BoolSetting tracers = add(BoolSetting.of("Tracers", false));

    public BlockEntityEspModule() {
        super("Block Entity ESP", "Boxes around spawners, signs and other special blocks.", Category.RENDER);
    }

    @Override public void collect(EspBatch batch, float pt) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) return;
        Vec3 eye = client.player.getEyePosition(pt);
        EspUtil.forEachBlockEntity(range.getInt(), be -> {
            if (be instanceof ChestBlockEntity || be instanceof BarrelBlockEntity || be instanceof ShulkerBoxBlockEntity
                || be instanceof EnderChestBlockEntity || be instanceof HopperBlockEntity || be instanceof DispenserBlockEntity
                || be instanceof AbstractFurnaceBlockEntity) return;
            float[] color;
            if (be instanceof SpawnerBlockEntity) {
                if (!spawners.get()) return;
                color = new float[]{0.7f,0.3f,1f};
            } else if (be instanceof SignBlockEntity) {
                if (!signs.get()) return;
                color = new float[]{0.3f,0.9f,1f};
            } else {
                if (!others.get()) return;
                color = new float[]{1f,1f,1f};
            }
            EspUtil.block(batch, be.getBlockPos(), color, true);
            if (tracers.get()) batch.line(eye, Vec3.atCenterOf(be.getBlockPos()), color[0],color[1],color[2],0.9f,0.04);
        });
    }
}
