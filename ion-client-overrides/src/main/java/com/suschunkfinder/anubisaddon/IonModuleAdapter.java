package com.suschunkfinder.anubisaddon;

import com.suschunkfinder.BoolSetting;
import com.suschunkfinder.ClientModule;
import com.suschunkfinder.ColorSetting;
import com.suschunkfinder.ModeSetting;
import com.suschunkfinder.NumberSetting;
import com.suschunkfinder.Setting;

import net.anubis.client.module.ModuleCategory;

/** Bridges an ION module and its options into Anubis's native toggle and settings UI. */
public final class IonModuleAdapter extends net.anubis.client.module.ClientModule {
    private final ClientModule delegate;

    public IonModuleAdapter(ClientModule delegate) {
        super("ion-" + slug(delegate.name), delegate.name, delegate.description, category(delegate.category));
        this.delegate = delegate;
        for (Setting setting : delegate.settings()) {
            if (setting instanceof BoolSetting source) {
                net.anubis.client.module.setting.BooleanSetting target =
                    new net.anubis.client.module.setting.BooleanSetting(source.name, source.get());
                target.onChange(() -> source.set(target.get()));
                addSetting(target);
            } else if (setting instanceof NumberSetting source) {
                net.anubis.client.module.setting.NumberSetting target =
                    new net.anubis.client.module.setting.NumberSetting(
                        source.name, (float) source.get(), (float) source.min(), (float) source.max()
                    ).step((float) source.step());
                target.onChange(() -> source.set(target.get()));
                addSetting(target);
            } else if (setting instanceof ModeSetting source) {
                net.anubis.client.module.setting.ModeSetting target =
                    new net.anubis.client.module.setting.ModeSetting(source.name, source.mode(), source.modes());
                target.onChange(() -> source.setMode(target.get()));
                addSetting(target);
            } else if (setting instanceof ColorSetting source) {
                net.anubis.client.module.setting.ColorSetting target =
                    new net.anubis.client.module.setting.ColorSetting(source.name, source.argb());
                target.onChange(() -> source.set(target.argb()));
                addSetting(target);
            }
        }
        if (delegate.isEnabled()) setEnabled(true);
    }

    @Override
    protected void onEnable() {
        delegate.setEnabled(true);
    }

    @Override
    protected void onDisable() {
        delegate.setEnabled(false);
    }

    private static ModuleCategory category(ClientModule.Category category) {
        return switch (category) {
            case COMBAT -> ModuleCategory.COMBAT;
            case RENDER -> ModuleCategory.RENDER;
            case MOVEMENT -> ModuleCategory.MOVEMENT;
            case MACRO -> ModuleCategory.DONUT_MACROS;
            case CLIENT -> ModuleCategory.CLIENT;
            case MISC -> ModuleCategory.MISC;
        };
    }

    private static String slug(String value) {
        String slug = value.toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
        slug = slug.replaceAll("^-+|-+$", "");
        return slug.isEmpty() ? "module" : slug;
    }
}
