package com.suschunkfinder.anubisaddon;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.anubis.client.addon.AnubisAddon;
import net.anubis.client.module.ModuleRegistry;
import com.suschunkfinder.IonAddonRuntime;

/** Registers ION modules in Anubis's native module registry and removes only the requested duplicates. */
public final class IonAnubisAddon extends AnubisAddon {
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
                combined.contains("playeresp")
                || combined.contains("storageesp")
                || combined.contains("killaura")
                || combined.contains("triggerbot")
                || combined.contains("mace");

            // Replace Anubis's own Freecam with the ION implementation, but do not
            // remove the adapter named "ION Freecam".
            boolean nativeFreecam = id.equals("freecam") || name.equals("freecam");

            if (requestedRemoval || nativeFreecam) {
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
        ModuleRegistry.registerProvider(new IonModuleProvider());
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }
}
