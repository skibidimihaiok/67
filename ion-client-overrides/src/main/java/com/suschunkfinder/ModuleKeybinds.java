package com.suschunkfinder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.lwjgl.glfw.GLFW;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

/** Per-module keyboard and mouse binds with Toggle, Hold, and Action modes. */
public final class ModuleKeybinds {
    private static final Map<String, Integer> BINDS = new HashMap<>();
    private static final Map<String, Integer> MODES = new HashMap<>();
    private static final Set<Integer> DOWN = new HashSet<>();

    private ModuleKeybinds() {}

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.getWindow() == null) {
                releaseHolds();
                DOWN.clear();
                return;
            }
            long window = client.getWindow().handle();
            boolean active = client.player != null && client.screen == null;
            for (int key = GLFW.GLFW_KEY_SPACE; key <= GLFW.GLFW_KEY_LAST; key++) {
                poll(key, GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS, active);
            }
            for (int button = 0; button <= GLFW.GLFW_MOUSE_BUTTON_LAST; button++) {
                poll(-button - 1, GLFW.glfwGetMouseButton(window, button) == GLFW.GLFW_PRESS, active);
            }
            if (!active) releaseHolds();
        });
    }

    private static void poll(int code, boolean pressed, boolean active) {
        boolean wasDown = DOWN.contains(code);
        if (pressed && !wasDown) {
            DOWN.add(code);
            if (active) {
                for (ClientModule module : ModuleManager.all()) {
                    if (get(module) != code) continue;
                    switch (mode(module)) {
                        case 1 -> module.setEnabled(true);
                        case 2 -> module.onBindAction();
                        default -> module.setEnabled(!module.isEnabled());
                    }
                }
            }
        } else if (!pressed && wasDown) {
            DOWN.remove(code);
            for (ClientModule module : ModuleManager.all()) {
                if (get(module) == code && mode(module) == 1) module.setEnabled(false);
            }
        }
    }

    private static void releaseHolds() {
        for (ClientModule module : ModuleManager.all()) {
            if (mode(module) == 1 && module.isEnabled()) module.setEnabled(false);
        }
    }

    public static int get(ClientModule module) { return BINDS.getOrDefault(module.name, 0); }
    public static void set(ClientModule module, int code) {
        BINDS.put(module.name, code);
        ModuleManager.save();
    }

    /** 0 = Toggle, 1 = Hold, 2 = Action. */
    public static int mode(ClientModule module) {
        return Math.floorMod(MODES.getOrDefault(module.name, 0), 3);
    }

    public static void setMode(ClientModule module, int mode) {
        MODES.put(module.name, Math.floorMod(mode, 3));
        ModuleManager.save();
    }

    public static void cycleMode(ClientModule module) { setMode(module, mode(module) + 1); }

    public static String modeName(ClientModule module) {
        return switch (mode(module)) {
            case 1 -> "Hold";
            case 2 -> "Action";
            default -> "Toggle";
        };
    }

    public static String display(int code) {
        if (code == 0) return "None";
        if (code < 0) return "Mouse " + (-code);
        String name = GLFW.glfwGetKeyName(code, 0);
        if (name != null) return name.toUpperCase();
        return switch (code) {
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "Left Shift";
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "Right Shift";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "Left Ctrl";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "Right Ctrl";
            case GLFW.GLFW_KEY_LEFT_ALT -> "Left Alt";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "Right Alt";
            case GLFW.GLFW_KEY_SPACE -> "Space";
            case GLFW.GLFW_KEY_TAB -> "Tab";
            default -> "Key " + code;
        };
    }

    public static Map<String, Object> snapshot() {
        Map<String, Object> result = new HashMap<>();
        result.putAll(BINDS);
        result.put("__modes", new HashMap<>(MODES));
        return result;
    }

    public static void load(Map<String, Object> values) {
        BINDS.clear();
        MODES.clear();
        if (values == null) return;
        values.forEach((name, value) -> {
            if ("__modes".equals(name) && value instanceof Map<?, ?> modes) {
                modes.forEach((module, mode) -> {
                    if (module instanceof String key && mode instanceof Number number) {
                        MODES.put(key, number.intValue());
                    }
                });
            } else if (value instanceof Number number) {
                BINDS.put(name, number.intValue());
            }
        });
    }
}
