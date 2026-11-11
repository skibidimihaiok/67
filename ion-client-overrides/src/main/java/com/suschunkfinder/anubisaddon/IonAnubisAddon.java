package com.suschunkfinder.anubisaddon;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.Identifier;
import net.anubis.client.addon.AnubisAddon;
import net.anubis.client.module.ModuleRegistry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import com.suschunkfinder.IonAddonRuntime;
import com.suschunkfinder.ClickGuiScreen;
import com.suschunkfinder.ModuleKeybinds;

/** Adds ION modules to Anubis's native GUI and keeps its GUI on a Left Shift shortcut. */
public final class IonAnubisAddon extends AnubisAddon {
    private KeyMapping openIonGuiKey;

    @Override
    public String getPackage() {
        return "com.suschunkfinder.anubisaddon";
    }

    @Override
    public void onConfigLoaded() {
        List<net.anubis.client.module.ClientModule> loaded =
            new ArrayList<>(ModuleRegistry.all());

        for (net.anubis.client.module.ClientModule module : loaded) {
            String id = normalize(module.id());
            String name = normalize(module.name());
            String combined = id + name;

            boolean requestedRemoval =
                combined.contains("esp")
                || combined.contains("tracer")
                || combined.contains("killaura");

            // Replace Anubis's native camera modules with the ION implementations.
            boolean nativeFreecam = id.equals("freecam") || name.equals("freecam");
            boolean nativeFreeLook = id.equals("freelook") || name.equals("freelook");

            if (requestedRemoval || nativeFreecam || nativeFreeLook) {
                if (module.isEnabled()) {
                    module.setEnabled(false);
                }
                ModuleRegistry.remove(module);
            }
        }

        IonModuleProvider.syncConfiguredSettings();
    }

    @Override
    public void onInitialize() {
        IonAddonRuntime.initializeForAnubisAddon();
        ModuleKeybinds.register();
        ModuleRegistry.registerProvider(new IonModuleProvider());

        // Anubis's built-in key defaults to Right Shift (key code 344).
        // Give ION a fresh mapping id so stale options.txt values cannot override it.
        KeyMapping.Category ionControls = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("ionclient", "controls")
        );
        openIonGuiKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.ionclient.open_gui",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            ionControls
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openIonGuiKey.consumeClick()) {
                if (client.player != null) {
                    client.setScreen(new ClickGuiScreen());
                }
            }
        });
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }
}
