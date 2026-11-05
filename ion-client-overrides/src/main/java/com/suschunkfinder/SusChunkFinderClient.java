package com.suschunkfinder;

import net.fabricmc.api.ClientModInitializer;

/** ION bootstrap; Anubis installs the same modules through its native add-on GUI. */
public final class SusChunkFinderClient implements ClientModInitializer {
    public static final String MOD_ID = "suschunkfinder";

    @Override
    public void onInitializeClient() {
        IonAddonRuntime.initializeFromClient();
    }
}
