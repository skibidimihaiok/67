package com.suschunkfinder;

/** Selects a preset accent or a fully custom RGB color for the GUI and HUD. */
public final class ThemeModule extends ClientModule {
    private static final int[] COLORS = {
        0xFF5AA0FF, 0xFFA070FF, 0xFF4CD84C, 0xFFE05050, 0xFFFF9A3C, 0xFFFF6EB4
    };
    private static ThemeModule instance;

    private final ModeSetting accent = add(ModeSetting.of(
            "Accent", 1, "Blue", "Purple", "Green", "Red", "Orange", "Pink", "Custom"));
    private final NumberSetting red = add(NumberSetting.of("Custom red", 160, 0, 255, 1));
    private final NumberSetting green = add(NumberSetting.of("Custom green", 112, 0, 255, 1));
    private final NumberSetting blue = add(NumberSetting.of("Custom blue", 255, 0, 255, 1));

    public ThemeModule() {
        super("Themes", "Changes the menu and HUD accent, including custom RGB.", Category.CLIENT);
        instance = this;
    }

    public static int color() {
        if (instance == null) return COLORS[1];
        if (instance.accent.index() == 6) {
            return 0xFF000000 | (instance.red.getInt() << 16)
                    | (instance.green.getInt() << 8) | instance.blue.getInt();
        }
        return COLORS[instance.accent.index()];
    }

    /** The accent color as red, green, blue between 0 and 1. */
    public static float[] rgb() {
        int color = color();
        return new float[] {
            ((color >> 16) & 255) / 255f,
            ((color >> 8) & 255) / 255f,
            (color & 255) / 255f
        };
    }
}
