package com.suschunkfinder;

public final class ColorSetting extends Setting {
    private static final int[] PALETTE = {
        0xFFFFFFFF, 0xFFFF5555, 0xFF55FFFF, 0xFFFFFF55, 0xFF55FF55,
        0xFFFF55FF, 0xFF5599FF, 0xFFFFAA55, 0xFFAA55FF, 0xFF202A38
    };
    private int color;
    public ColorSetting(String name, int color) { super(name); this.color = color; }
    public int argb() { return color; }
    public float red() { return ((color >>> 16) & 255) / 255.0f; }
    public float green() { return ((color >>> 8) & 255) / 255.0f; }
    public float blue() { return (color & 255) / 255.0f; }
    public float alpha() { return ((color >>> 24) & 255) / 255.0f; }
    public void set(int value) { color = value; }
    @Override public String valueText() { return String.format("#%08X", color); }
    @Override public void next() {
        int nearest = 0; long best = Long.MAX_VALUE;
        for (int i = 0; i < PALETTE.length; i++) {
            long d = Math.abs((long)(color >>> 24) - (PALETTE[i] >>> 24))
                + Math.abs((long)((color >>> 16) & 255) - ((PALETTE[i] >>> 16) & 255))
                + Math.abs((long)((color >>> 8) & 255) - ((PALETTE[i] >>> 8) & 255))
                + Math.abs((long)(color & 255) - (PALETTE[i] & 255));
            if (d < best) { best = d; nearest = i; }
        }
        color = PALETTE[(nearest + 1) % PALETTE.length];
    }
    @Override public Object save() { return color; }
    @Override public void load(Object value) { if (value instanceof Number n) color = n.intValue(); }
}
