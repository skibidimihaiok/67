package com.suschunkfinder;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

/** Starts only ION's unique module runtime when hosted by Anubis. */
public final class IonAddonRuntime {
    private static boolean initialized;

    private IonAddonRuntime() { }

    public static synchronized void initializeFromClient() {
        if (FabricLoader.getInstance().isModLoaded("anubisplus")) {
            initializeForAnubisAddon();
        } else {
            initializeStandalone();
        }
    }

    public static synchronized void initializeForAnubisAddon() {
        if (initialized) return;
        Config.get();
        ModuleManager.initForAnubisAddon();
        EspRenderer.register();
        attachHud();
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> EspRenderer.close());
        initialized = true;
    }

    private static void initializeStandalone() {
        if (initialized) return;
        Config.get();
        ModuleManager.init();
        ChunkScanner.register();
        SusRenderer.register();
        EspRenderer.register();
        CustomCrosshairModule.register();
        Commands.register();
        ExtraCommands.register();
        Keys.register();
        attachHud();
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            SusRenderer.close();
            EspRenderer.close();
        });
        initialized = true;
    }

    private static void attachHud() {
        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CHAT,
            Identifier.fromNamespaceAndPath(SusChunkFinderClient.MOD_ID, "hud"),
            (graphics, tickCounter) -> ModuleManager.renderHud(graphics)
        );
    }
}
