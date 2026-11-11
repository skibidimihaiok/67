package com.suschunkfinder.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.suschunkfinder.CameraControl;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

/** Routes mouse motion to ION's detached camera while Free Look or Freecam is active. */
@Mixin(Entity.class)
public abstract class TurnMixin {
    @Inject(method = "turn", at = @At("HEAD"), cancellable = true, require = 0)
    private void suschunk$turn(double yRot, double xRot, CallbackInfo ci) {
        if (CameraControl.active() && (Object)this == Minecraft.getInstance().player) {
            float scale = 0.15f * CameraControl.sensitivity;
            CameraControl.yaw += (float)yRot * scale;
            CameraControl.pitch = Mth.clamp(CameraControl.pitch + (float)xRot * scale, -90f, 90f);
            ci.cancel();
        }
    }
}
