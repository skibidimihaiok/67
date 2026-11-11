package com.suschunkfinder;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

public final class NumberSetting extends Setting {
    private final double min;
    private final double max;
    private final double step;
    private final DoubleSupplier getter;
    private final DoubleConsumer setter;
    private Runnable onChange = () -> {};

    public NumberSetting(String name, double min, double max, double step,
                         DoubleSupplier getter, DoubleConsumer setter) {
        super(name);
        this.min = min;
        this.max = max;
        this.step = step;
        this.getter = getter;
        this.setter = setter;
    }

    public static NumberSetting of(String name, double def, double min, double max, double step) {
        double[] box = {def};
        return new NumberSetting(name, min, max, step, () -> box[0], v -> box[0] = v);
    }

    public NumberSetting onChange(Runnable action) {
        this.onChange = action;
        return this;
    }

    public double get() { return getter.getAsDouble(); }
    public int getInt() { return (int) Math.round(get()); }
    public double min() { return min; }
    public double max() { return max; }

    public void set(double value) {
        double v = Math.max(min, Math.min(max, value));
        v = Math.round(v / step) * step;
        v = Math.max(min, Math.min(max, v));
        v = Math.round(v * 100.0) / 100.0;
        setter.accept(v);
        onChange.run();
    }

    public void change(boolean up) { set(get() + (up ? step : -step)); }

    @Override
    public String valueText() {
        return step >= 1 ? String.valueOf(getInt()) : String.format("%.1f", get());
    }

    @Override
    public void next() { change(true); }

    @Override
    public Object save() { return get(); }

    @Override
    public void load(Object value) {
        if (value instanceof Number n) set(n.doubleValue());
    }
}
