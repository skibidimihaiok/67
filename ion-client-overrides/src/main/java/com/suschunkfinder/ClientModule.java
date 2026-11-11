package com.suschunkfinder;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/** A feature of the client with an on/off state and its own settings. */
public abstract class ClientModule {
    public enum Category {
        COMBAT("Combat"), RENDER("Render"), MOVEMENT("Movement"),
        CLIENT("Client"), MACRO("Macro"), MISC("Misc");

        public final String label;
        Category(String label) { this.label = label; }
    }

    public final String name;
    public final String description;
    public final Category category;
    private final List<Setting> settings = new ArrayList<>();
    private boolean enabled;

    protected ClientModule(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    protected <T extends Setting> T add(T setting) {
        settings.add(setting);
        return setting;
    }

    public List<Setting> settings() { return settings; }
    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean value) {
        if (enabled == value) return;
        enabled = value;
        if (value) onEnable(); else onDisable();
        ModuleManager.save();
    }

    protected void onEnable() {}
    protected void onDisable() {}

    /** Invoked by a keybind configured to Action mode. */
    public void onBindAction() { tick(Minecraft.getInstance()); }

    /** Called every game tick while the module is on. */
    public void tick(Minecraft client) {}

    /** False for modules that store their own settings elsewhere. */
    public boolean persists() { return true; }

    /** Called every frame while the module is on, for modules that draw on the HUD. */
    public void renderHud(GuiGraphics graphics) {}
}
