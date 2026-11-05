package com.suschunkfinder;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/** Optional LRCLIB lookup. This runs off the render thread and never blocks Minecraft. */
final class SpotifyLyrics {
    private SpotifyLyrics() { }
    static String lookup(String title, String artist) {
        try {
            String url = "https://lrclib.net/api/get?track_name="
                + URLEncoder.encode(title, StandardCharsets.UTF_8)
                + "&artist_name=" + URLEncoder.encode(artist, StandardCharsets.UTF_8);
            HttpURLConnection c = (HttpURLConnection)URI.create(url).toURL().openConnection();
            c.setConnectTimeout(4000); c.setReadTimeout(5000); c.setRequestProperty("User-Agent", "ION-CLIENT/1.0");
            try (InputStream in = c.getInputStream(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                in.transferTo(out);
                JsonObject json = JsonParser.parseString(out.toString(StandardCharsets.UTF_8)).getAsJsonObject();
                if (json.has("plainLyrics") && !json.get("plainLyrics").isJsonNull()) return json.get("plainLyrics").getAsString();
                if (json.has("syncedLyrics") && !json.get("syncedLyrics").isJsonNull()) return json.get("syncedLyrics").getAsString().replaceAll("\\[[^]]+\\]", "").trim();
            } finally { c.disconnect(); }
        } catch (Exception ignored) { }
        return "";
    }
}
