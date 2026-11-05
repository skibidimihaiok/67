package com.suschunkfinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

/** Reconnects to the same multiplayer server once the configured Overworld Y threshold is crossed. */
public final class AutoRelogModule extends ClientModule {
    private final NumberSetting yLevel = add(NumberSetting.of("Y Level", 0, -60, 16, 1));
    private final NumberSetting delay = add(NumberSetting.of("Delay", 3, 1, 30, 0.5));
    private int aboveTicks;
    private int rejoinTicks;
    private ServerData pendingServer;
    private boolean disconnected;

    public AutoRelogModule() {
        super("Auto Relog", "Reconnects once after dropping below the selected Y level.", Category.MISC);
    }

    @Override public void tick(Minecraft client) {
        if (pendingServer != null) {
            if (!disconnected) {
                if (client.level == null) disconnected = true;
                return;
            }
            if (client.level != null) { clearPending(); return; }
            if (rejoinTicks-- > 0) return;
            ServerData server = pendingServer;
            pendingServer = null;
            disconnected = false;
            if (client.screen != null) {
                ConnectScreen.startConnecting(client.screen, client, ServerAddress.parseString(server.ip), server, false, null);
            }
            return;
        }
        if (client.player == null || client.level == null || client.isSingleplayer()
            || client.level.dimension() != Level.OVERWORLD || client.screen != null) {
            aboveTicks = 0;
            return;
        }
        double y = client.player.getY();
        if (y >= yLevel.get() + 4.0) {
            if (aboveTicks < 40) aboveTicks++;
        } else if (y < yLevel.get()) {
            boolean armed = aboveTicks >= 40;
            aboveTicks = 0;
            if (armed) {
                ServerData server = client.getCurrentServer();
                if (server == null) return;
                pendingServer = server;
                rejoinTicks = (int)Math.round(delay.get() * 20.0);
                client.player.displayClientMessage(Component.literal("[ION] Auto Relog: reconnecting..."), true);
                client.disconnect(new TitleScreen(), false);
                disconnected = false;
            }
        }
    }

    private void clearPending() { pendingServer = null; disconnected = false; rejoinTicks = 0; }
    @Override protected void onDisable() { aboveTicks = 0; clearPending(); }
}
