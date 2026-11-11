package com.suschunkfinder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/** Settings screen for one module, including bind capture and bind activation mode. */
public class ModuleSettingsScreen extends Screen {
    private static final int COL_W = 190;
    private static final int ROW = 32;

    private record Label(Supplier<String> text, int x, int y) {}
    private record Slider(NumberSetting setting, int x, int y) {}

    private final ClientModule module;
    private final Screen parent;
    private final List<Label> labels = new ArrayList<>();
    private final List<Slider> sliders = new ArrayList<>();
    private boolean waitingForBind;
    private Button bindButton;
    private Button bindModeButton;

    public ModuleSettingsScreen(ClientModule module, Screen parent) {
        super(Component.literal(module.name));
        this.module = module;
        this.parent = parent;
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    protected void init() {
        labels.clear();
        sliders.clear();
        int left = this.width / 2 - COL_W + 6;
        int top = 56;
        int rows = Math.max(3, (this.height - top - 54) / ROW);

        bindButton = this.addRenderableWidget(Button.builder(bindText(), b -> {
            waitingForBind = true;
            b.setMessage(Component.literal("Press a key or mouse button (ESC clears)"));
        }).bounds(left, top + ROW, 170, 20).build());

        bindModeButton = this.addRenderableWidget(Button.builder(bindModeText(), b -> {
            ModuleKeybinds.cycleMode(module);
            b.setMessage(bindModeText());
        }).bounds(left, top + ROW * 2, 170, 20).build());

        this.addRenderableWidget(Button.builder(enabledText(), b -> {
            module.setEnabled(!module.isEnabled());
            b.setMessage(enabledText());
        }).bounds(left, top, 170, 20).build());

        int n = 2;
        for (Setting setting : module.settings()) {
            int x = left + (n / rows) * COL_W;
            int y = top + (n % rows) * ROW + ROW;
            n++;

            if (setting instanceof NumberSetting number) {
                labels.add(new Label(() -> number.name + ": " + number.valueText(), x, y + 6));
                this.addRenderableWidget(Button.builder(Component.literal("-"), b -> { number.change(false); ModuleManager.save(); })
                        .bounds(x + 122, y, 22, 20).build());
                this.addRenderableWidget(Button.builder(Component.literal("+"), b -> { number.change(true); ModuleManager.save(); })
                        .bounds(x + 148, y, 22, 20).build());
                sliders.add(new Slider(number, x, y + 22));
            } else {
                this.addRenderableWidget(Button.builder(Component.literal(setting.name + ": " + setting.valueText()), b -> {
                    setting.next();
                    b.setMessage(Component.literal(setting.name + ": " + setting.valueText()));
                    ModuleManager.save();
                }).bounds(x, y, 170, 20).build());
            }
        }

        this.addRenderableWidget(Button.builder(Component.literal("Back"), b -> this.onClose())
                .bounds(this.width / 2 - 40, this.height - 30, 80, 20).build());
    }

    private Component bindText() {
        return Component.literal("Bind: " + ModuleKeybinds.display(ModuleKeybinds.get(module)));
    }

    private Component bindModeText() {
        return Component.literal("Bind mode: " + ModuleKeybinds.modeName(module));
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (waitingForBind) {
            waitingForBind = false;
            ModuleKeybinds.set(module, event.key() == 256 ? 0 : event.key());
            if (bindButton != null) bindButton.setMessage(bindText());
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!waitingForBind && event.button() == 0) {
            for (Slider slider : sliders) {
                if (event.x() >= slider.x() && event.x() <= slider.x() + 170
                        && event.y() >= slider.y() && event.y() <= slider.y() + 6) {
                    double fraction = Math.max(0.0, Math.min(1.0, (event.x() - slider.x()) / 170.0));
                    NumberSetting number = slider.setting();
                    number.set(number.min() + (number.max() - number.min()) * fraction);
                    ModuleManager.save();
                    return true;
                }
            }
        }
        if (waitingForBind) {
            waitingForBind = false;
            ModuleKeybinds.set(module, -event.button() - 1);
            if (bindButton != null) bindButton.setMessage(bindText());
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    private Component enabledText() {
        return Component.literal("Enabled: " + (module.isEnabled() ? "ON" : "OFF"));
    }

    @Override
    public void onClose() {
        ModuleManager.save();
        this.minecraft.setScreen(parent);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderBackground(graphics, mouseX, mouseY, delta);
        int left = this.width / 2 - COL_W;
        int right = this.width / 2 + COL_W;
        int alpha = ClickGuiModule.panelAlpha() << 24;
        int accent = Hud.accent();
        graphics.fill(left - 6, 10, right + 6, this.height - 6, 0xFF000000 | (accent & 0x00FFFFFF) >> 2);
        graphics.fillGradient(left - 5, 11, right + 5, this.height - 7, alpha | 0x1A2234, alpha | 0x0C111B);
        graphics.fill(left - 5, 11, right + 5, 14, accent);
        graphics.fill(left - 5, 14, right + 5, 46, (alpha & 0xFF000000) | 0x0A0E16);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        int left = this.width / 2 - COL_W + 6;
        graphics.drawString(this.font, module.name, left, 20, Hud.accent());
        graphics.drawString(this.font, module.description, left, 33, 0xFF8890A0);
        for (Label label : labels) {
            graphics.drawString(this.font, label.text().get(), label.x(), label.y(), Hud.WHITE);
        }
        for (Slider slider : sliders) {
            NumberSetting number = slider.setting();
            int x = slider.x();
            int y = slider.y();
            int filled = (int) Math.round(170.0 * (number.get() - number.min())
                    / Math.max(0.0001, number.max() - number.min()));
            graphics.fill(x, y, x + 170, y + 4, 0xFF34313D);
            graphics.fill(x, y, x + Math.max(0, Math.min(170, filled)), y + 4, Hud.accent());
        }
    }
}
