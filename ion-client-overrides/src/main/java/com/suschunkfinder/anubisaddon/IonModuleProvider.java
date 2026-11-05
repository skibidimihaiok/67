package com.suschunkfinder.anubisaddon;

import java.util.ArrayList;
import java.util.List;

import com.suschunkfinder.ClientModule;
import com.suschunkfinder.ModuleManager;
import net.anubis.client.module.ModuleProvider;

/** Exposes only the ION-only features to Anubis's existing module registry and GUI. */
public final class IonModuleProvider implements ModuleProvider {
    private static final List<IonModuleAdapter> REGISTERED = new ArrayList<>();

    @Override
    public String id() {
        return "ion-unique-modules";
    }

    @Override
    public List<net.anubis.client.module.ClientModule> createModules() {
        List<net.anubis.client.module.ClientModule> result = new ArrayList<>();
        REGISTERED.clear();
        for (ClientModule module : ModuleManager.all()) {
            IonModuleAdapter adapter = new IonModuleAdapter(module);
            REGISTERED.add(adapter);
            result.add(adapter);
        }
        return result;
    }

    public static void syncConfiguredSettings() {
        for (IonModuleAdapter adapter : REGISTERED) {
            adapter.syncSettingsToDelegate();
        }
    }
}
