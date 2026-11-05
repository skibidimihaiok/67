package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

/** Detached camera controls integrated with ION's existing camera mixin and ClickGUI. */
public final class FreecamModule extends ClientModule {
    private final NumberSetting speed = add(NumberSetting.of("Speed", 1.0, 0.1, 50.0, 0.1));
    private final NumberSetting vertical = add(NumberSetting.of("Vertical", 1.0, 0.2, 5.0, 0.05));
    private final BoolSetting smooth = add(BoolSetting.of("Smooth", true));
    private final BoolSetting body = add(BoolSetting.of("Body", true));
    private final BoolSetting hands = add(BoolSetting.of("Hands", true));
    private final BoolSetting keysMoveBody = add(BoolSetting.of("Keys Move Body", false));
    private final BoolSetting keepWalking = add(BoolSetting.of("Keep Walking", true));
    private final BoolSetting scrollSpeed = add(BoolSetting.of("Scroll Speed", true));
    private final BoolSetting resetScroll = add(BoolSetting.of("Reset Scroll Speed", true));
    private final NumberSetting chunks = add(NumberSetting.of("Chunks", 12, 2, 32, 1));

    private ClientInput savedInput;
    private LocalPlayer owner;
    private boolean inputSuppressed;
    private double smoothX, smoothY, smoothZ;

    public FreecamModule() {
        super("Freecam", "Fly the view camera separately from your player.", Category.MOVEMENT);
        speed.onChange(() -> { smoothX = smoothY = smoothZ = 0; });
    }

    private void start(Minecraft client) {
        if (client.player == null) return;
        CameraControl.freeLookActive = false;
        Vec3 eye = client.player.getEyePosition();
        CameraControl.x = CameraControl.px = eye.x;
        CameraControl.y = CameraControl.py = eye.y;
        CameraControl.z = CameraControl.pz = eye.z;
        CameraControl.yaw = client.player.getYRot();
        CameraControl.pitch = client.player.getXRot();
        owner = client.player;
        savedInput = client.player.input;
        inputSuppressed = false;
        syncInput();
        CameraControl.freecamActive = true;
    }

    private void syncInput() {
        if (owner == null || savedInput == null) return;
        boolean suppress = !keepWalking.get() && !keysMoveBody.get();
        if (suppress && !inputSuppressed) {
            owner.input = new ClientInput();
            inputSuppressed = true;
        } else if (!suppress && inputSuppressed) {
            owner.input = savedInput;
            inputSuppressed = false;
        }
    }

    private void stop(Minecraft client) {
        CameraControl.freecamActive = false;
        if (client.player != null && client.player == owner && savedInput != null) {
            client.player.input = savedInput;
        }
        savedInput = null;
        owner = null;
        inputSuppressed = false;
        smoothX = smoothY = smoothZ = 0;
    }

    @Override protected void onEnable() { start(Minecraft.getInstance()); }
    @Override protected void onDisable() { stop(Minecraft.getInstance()); }

    @Override public void tick(Minecraft client) {
        if (client.player == null) { stop(client); return; }
        if (!CameraControl.freecamActive || owner != client.player) start(client);
        syncInput();
        CameraControl.px = CameraControl.x;
        CameraControl.py = CameraControl.y;
        CameraControl.pz = CameraControl.z;
        if (client.screen != null) return;

        Options o = client.options;
        double yaw = Math.toRadians(CameraControl.yaw);
        double pitch = Math.toRadians(CameraControl.pitch);
        double fx = -Math.sin(yaw) * Math.cos(pitch);
        double fy = -Math.sin(pitch);
        double fz = Math.cos(yaw) * Math.cos(pitch);
        double rx = -Math.cos(yaw), rz = -Math.sin(yaw);
        double mx = 0, my = 0, mz = 0;
        if (o.keyUp.isDown()) { mx += fx; my += fy; mz += fz; }
        if (o.keyDown.isDown()) { mx -= fx; my -= fy; mz -= fz; }
        if (o.keyLeft.isDown()) { mx -= rx; mz -= rz; }
        if (o.keyRight.isDown()) { mx += rx; mz += rz; }
        if (o.keyJump.isDown()) my += vertical.get();
        if (o.keyShift.isDown()) my -= vertical.get();
        double len = Math.sqrt(mx * mx + my * my + mz * mz);
        if (len > 0) {
            mx = mx / len * speed.get();
            my = my / len * speed.get();
            mz = mz / len * speed.get();
        }
        if (smooth.get()) {
            smoothX += (mx - smoothX) * 0.45;
            smoothY += (my - smoothY) * 0.45;
            smoothZ += (mz - smoothZ) * 0.45;
        } else {
            smoothX = mx; smoothY = my; smoothZ = mz;
        }
        CameraControl.x += smoothX;
        CameraControl.y += smoothY;
        CameraControl.z += smoothZ;
    }
}
