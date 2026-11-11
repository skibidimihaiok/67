package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;

/** Mines only obsidian-family blocks that are directly under the crosshair. */
public final class SurroundMinerModule extends ClientModule {
    private final NumberSetting range = add(NumberSetting.of("Range", 4.5, 1.0, 6.0, 0.5));

    public SurroundMinerModule() {
        super("Surround Miner", "Mines a crosshair-targeted obsidian block; does not search through walls.", Category.COMBAT);
    }

    @Override
    public void tick(Minecraft client) {
        if (client.player == null || client.level == null || client.gameMode == null || client.screen != null) return;
        if (!(client.hitResult instanceof BlockHitResult hit)) return;
        if (client.player.getEyePosition().distanceTo(net.minecraft.world.phys.Vec3.atCenterOf(hit.getBlockPos())) > range.get()) return;
        var state = client.level.getBlockState(hit.getBlockPos());
        if (!state.is(net.minecraft.world.level.block.Blocks.OBSIDIAN)
                && !state.is(net.minecraft.world.level.block.Blocks.CRYING_OBSIDIAN)) return;
        client.gameMode.startDestroyBlock(hit.getBlockPos(), hit.getDirection());
    }
}
