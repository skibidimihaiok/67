package com.suschunkfinder;

import org.lwjgl.glfw.GLFW;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;

/** Independent camera rotation with adjustable mouse sensitivity and optional arrow-key control. */
public final class FreeLookModule extends ClientModule {
    private final ModeSetting mode = add(ModeSetting.of("Mode", 0, "Player", "Camera"));
    private final BoolSetting togglePerspective = add(BoolSetting.of("Toggle Perspective", true));
    private final NumberSetting sensitivity = add(NumberSetting.of("Camera Sensitivity", 8.0, 0, 10, 0.1));
    private final BoolSetting opposite = add(BoolSetting.of("Arrows Control Opposite", true));
    private final NumberSetting arrowSpeed = add(NumberSetting.of("Arrow Speed", 4.0, 0, 10, 0.5));
    private CameraType previousPerspective;

    public FreeLookModule() {
        super("Free Look", "Look around independently while keeping your player facing forward.", Category.RENDER);
    }

    private void start(Minecraft client) {
        if (client.player == null || CameraControl.freecamActive) return;
        CameraControl.yaw = client.player.getYRot();
        CameraControl.pitch = client.player.getXRot();
        CameraControl.sensitivity = (float)(sensitivity.get() / 8.0);
        CameraControl.freeLookActive = true;
        if (togglePerspective.get()) {
            previousPerspective = client.options.getCameraType();
            client.options.setCameraType(previousPerspective.cycle());
        }
    }

    @Override protected void onEnable() { start(Minecraft.getInstance()); }

    @Override protected void onDisable() {
        CameraControl.freeLookActive = false;
        Minecraft client = Minecraft.getInstance();
        if (previousPerspective != null) {
            client.options.setCameraType(previousPerspective);
            previousPerspective = null;
        }
        CameraControl.sensitivity = 1.0f;
    }

    @Override public void tick(Minecraft client) {
        if (client.player == null || CameraControl.freecamActive) return;
        if (!CameraControl.freeLookActive) start(client);
        CameraControl.sensitivity = (float)(sensitivity.get() / 8.0);
        if (client.screen != null || arrowSpeed.get() <= 0) return;
        long window = client.getWindow().getWindow();
        float direction = opposite.get() ? -1.0f : 1.0f;
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) CameraControl.yaw -= (float)arrowSpeed.get() * direction;
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) CameraControl.yaw += (float)arrowSpeed.get() * direction;
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS) CameraControl.pitch = Math.max(-90, CameraControl.pitch - (float)arrowSpeed.get() * direction);
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) CameraControl.pitch = Math.min(90, CameraControl.pitch + (float)arrowSpeed.get() * direction);
    }
}
