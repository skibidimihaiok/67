package com.suschunkfinder.anubisaddon;

import java.util.ArrayList;
import java.util.List;

import com.suschunkfinder.ClientModule;
import com.suschunkfinder.ModuleManager;
import net.anubis.client.module.ModuleProvider;

/** Exposes only the ION-only features to Anubis's existing module registry and GUI. */
public final class IonModuleProvider implements ModuleProvider {
    @Override
    public String id() {
        return "ion-unique-modules";
    }

    @Override
    public List<net.anubis.client.module.ClientModule> createModules() {
        List<net.anubis.client.module.ClientModule> result = new ArrayList<>();
        for (ClientModule module : ModuleManager.all()) {
            result.add(new IonModuleAdapter(module));
        }
        return result;
    }
}
