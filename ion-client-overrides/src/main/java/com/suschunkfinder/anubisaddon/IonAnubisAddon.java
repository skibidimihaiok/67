package com.suschunkfinder.anubisaddon;

import net.anubis.client.addon.AnubisAddon;
import net.anubis.client.module.ModuleRegistry;
import com.suschunkfinder.IonAddonRuntime;

/** Registers ION's non-duplicate modules inside Anubis's native Click GUI. */
public final class IonAnubisAddon extends AnubisAddon {
    @Override
    public String getPackage() {
        return "com.suschunkfinder.anubisaddon";
    }

    @Override
    public void onInitialize() {
        IonAddonRuntime.initializeForAnubisAddon();
        ModuleRegistry.registerProvider(new IonModuleProvider());
    }
}
