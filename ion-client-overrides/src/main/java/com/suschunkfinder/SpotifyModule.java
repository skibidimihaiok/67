package com.suschunkfinder;

import org.lwjgl.glfw.GLFW;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/** Spotify HUD using ION's existing PKCE client and configurable Anubis-style display/media options. */
public final class SpotifyModule extends HudModule {
    private final BoolSetting showArtist = add(BoolSetting.of("Show Artist", true));
    private final BoolSetting showAlbumArt = add(BoolSetting.of("Show Album Art", true));
    private final BoolSetting showProgress = add(BoolSetting.of("Show Progress", true));
    private final BoolSetting scrollTitles = add(BoolSetting.of("Scroll Long Titles", true));
    private final BoolSetting showLyrics = add(BoolSetting.of("Show Lyrics", true));
    private final NumberSetting lyricLines = add(NumberSetting.of("Lyric Lines", 2, 1, 3, 1));
    private final ModeSetting lyricMotion = add(ModeSetting.of("Lyric Motion", 0, "Wave", "Glide", "Fade"));
    private final ModeSetting lyricHighlight = add(ModeSetting.of("Lyric Highlight", 0, "Karaoke", "Line", "Off"));
    private final BoolSetting lyricsOnline = add(BoolSetting.of("Lyrics Online", true));
    private final NumberSetting cardWidth = add(NumberSetting.of("Card Width", 320, 200, 560, 1));
    private final ColorSetting titleColor = add(new ColorSetting("Title Color", -1447442));
    private final ColorSetting artistColor = add(new ColorSetting("Artist Color", -6052949));
    private final ColorSetting lyricsColor = add(new ColorSetting("Lyrics Color", -657929));
    private final ColorSetting upcomingColor = add(new ColorSetting("Upcoming Lyrics Color", -6052949));
    private final ColorSetting highlightColor = add(new ColorSetting("Highlight Color", -14756000));
    private final BoolSetting showWhenIdle = add(BoolSetting.of("Show When Idle", false));
    private final BoolSetting mediaKeys = add(BoolSetting.of("Media Keys", false));
    private final NumberSetting playPauseKey = add(NumberSetting.of("Play/Pause Key", -1, -1, 348, 1));
    private final NumberSetting nextKey = add(NumberSetting.of("Next Track Key", -1, -1, 348, 1));
    private final NumberSetting previousKey = add(NumberSetting.of("Previous Track Key", -1, -1, 348, 1));
    private boolean playDown, nextDown, previousDown;
    private volatile String lyrics = "";
    private volatile String requestedTrack = "";

    public SpotifyModule() {
        super("Spotify", "Spotify now-playing HUD. Set up with /suschunk spotify id and /suschunk spotify login.", Category.CLIENT, 5, 420);
    }

    @Override protected void onEnable() { SpotifyClient.start(); }
    @Override public void tick(Minecraft client) {
        SpotifyClient.start();
        if (!mediaKeys.get() || client.screen != null || client.getWindow() == null) return;
        long window = client.getWindow().getWindow();
        playDown = key(window, playPauseKey, playDown, "play");
        nextDown = key(window, nextKey, nextDown, "next");
        previousDown = key(window, previousKey, previousDown, "prev");
        if (!SpotifyClient.title.isEmpty() && !SpotifyClient.title.equals(requestedTrack) && lyricsOnline.get()) {
            requestedTrack = SpotifyClient.title;
            String t = SpotifyClient.title, a = SpotifyClient.artist;
            Thread worker = new Thread(() -> lyrics = SpotifyLyrics.lookup(t, a), "ion-spotify-lyrics");
            worker.setDaemon(true); worker.start();
        }
    }

    private boolean key(long window, NumberSetting setting, boolean wasDown, String action) {
        int code = setting.getInt();
        boolean down = code >= 0 && GLFW.glfwGetKey(window, code) == GLFW.GLFW_PRESS;
        if (down && !wasDown) {
            if ("play".equals(action)) SpotifyClient.control(SpotifyClient.playing ? "pause" : "play");
            else SpotifyClient.control(action);
        }
        return down;
    }

    private static String fit(String text, int width) {
        String value = text == null ? "" : text;
        while (value.length() > 1 && Hud.width(value) > width) value = value.substring(0, value.length() - 1);
        return value;
    }

    @Override protected void draw(GuiGraphics g, int x, int y) {
        SpotifyClient.start();
        int width = cardWidth.getInt();
        if (SpotifyClient.title.isEmpty()) {
            if (!showWhenIdle.get()) return;
            g.fill(x - 3, y - 3, x + width + 3, y + 38, Hud.PANEL);
            Hud.text(g, SpotifyClient.hasClientId() ? "Spotify: nothing playing" : "Spotify: set a Client ID", x, y, Hud.WHITE);
            return;
        }
        long duration = Math.max(1, SpotifyClient.durationMs);
        long progress = SpotifyClient.progressMs;
        if (SpotifyClient.playing) progress += System.currentTimeMillis() - SpotifyClient.stamp;
        progress = Math.min(progress, duration);
        int lines = showLyrics.get() && !lyrics.isEmpty() ? lyricLines.getInt() : 0;
        int height = 35 + (showArtist.get() ? Hud.LINE : 0) + (showProgress.get() ? 7 : 0) + lines * Hud.LINE;
        g.fill(x - 3, y - 3, x + width + 3, y + height + 3, Hud.PANEL);
        g.fill(x - 3, y - 3, x - 1, y + height + 3, 0xFF1DB954);
        int textX = x;
        int textWidth = width;
        if (showAlbumArt.get()) {
            g.fill(x, y, x + 28, y + 28, 0xFF1DB954);
            g.drawString(Minecraft.getInstance().font, "♫", x + 8, y + 9, 0xFFFFFFFF, true);
            textX += 34; textWidth -= 34;
        }
        String title = SpotifyClient.title;
        if (scrollTitles.get() && Hud.width(title) > textWidth && title.length() > 1) {
            int offset = (int)((System.currentTimeMillis() / 250) % title.length());
            title = title.substring(offset) + "  " + title.substring(0, offset);
        }
        Hud.text(g, fit(title, textWidth), textX, y, titleColor.argb());
        int lineY = y + Hud.LINE;
        if (showArtist.get()) { Hud.text(g, fit(SpotifyClient.artist, textWidth), textX, lineY, artistColor.argb()); lineY += Hud.LINE; }
        if (showProgress.get()) {
            int barW = width - 2;
            g.fill(x, lineY + 1, x + barW, lineY + 4, 0x66FFFFFF);
            g.fill(x, lineY + 1, x + (int)(barW * (double)progress / duration), lineY + 4, highlightColor.argb());
            lineY += 7;
        }
        if (lines > 0) {
            String[] rows = lyrics.split("\\R");
            int start = (int)((System.currentTimeMillis() / 2500) % Math.max(1, rows.length));
            for (int i = 0; i < lines && i < rows.length; i++) {
                int index = (start + i) % rows.length;
                int color = i == 0 && lyricHighlight.index() != 2 ? highlightColor.argb() : (i == 0 ? lyricsColor.argb() : upcomingColor.argb());
                Hud.text(g, fit(rows[index].trim(), width), x, lineY, color);
                lineY += Hud.LINE;
            }
        } else if (showLyrics.get() && lyricsOnline.get()) {
            Hud.text(g, "Lyrics unavailable for this track", x, lineY, lyricsColor.argb());
        }
    }
}
