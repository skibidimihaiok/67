package com.suschunkfinder;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/** Lightweight local warning when a loaded player is invisible or absent from the tab list. */
public final class AntiVanishModule extends ClientModule {
    private final BoolSetting notify = add(BoolSetting.of("Notifications", true));
    private final BoolSetting invisible = add(BoolSetting.of("Invisible players", true));
    private final BoolSetting missingTab = add(BoolSetting.of("Missing from tab list", true));
    private final Set<UUID> reported = new HashSet<>();
    private Object lastLevel;

    public AntiVanishModule() {
        super("Anti Vanish", "Flags suspicious loaded players locally; this is a heuristic, not proof of vanish.", Category.MISC);
    }

    @Override public void tick(Minecraft client) {
        if (client.player == null || client.level == null || client.getConnection() == null) return;
        if (lastLevel != client.level) { reported.clear(); lastLevel = client.level; }
        for (Player other : client.level.players()) {
            if (other == client.player) continue;
            boolean hidden = (invisible.get() && other.isInvisible())
                || (missingTab.get() && client.getConnection().getPlayerInfo(other.getUUID()) == null);
            if (hidden && reported.add(other.getUUID()) && notify.get()) {
                client.player.displayClientMessage(Component.literal("[ION] Possible vanished/invisible player: "
                    + other.getGameProfile().name()), true);
            }
        }
    }

    @Override protected void onDisable() { reported.clear(); lastLevel = null; }
}
