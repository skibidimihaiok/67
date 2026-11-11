package com.suschunkfinder;

import java.util.HashMap;
import java.util.Map;

/** DonutSMP's 9x9 public region grid adapted from the Anubis Beta 0.9.9.1 GPL-3.0 release. */
public final class DonutRegionMapData {
    public static final int MAP_SIZE = 9;
    private static final double REGION_SIZE = 50000.0;
    private static final double MAP_OFFSET = 225000.0;
    private static final String[] REGION_TYPE_NAMES = {"EU Central", "EU West", "NA East", "NA West", "Asia", "Oceania"};
    private static final int[] REGION_TYPE_COLORS = { -10236257, -10246656, -1397425, -4559313, -12401931, -16545540 };
    private static final Map<Integer, RegionInfo> REGION_MAP = buildRegionMap();

    private DonutRegionMapData() { }
    public record RegionInfo(int regionId, int regionType) { }
    public static RegionInfo regionInfo(int index) { return REGION_MAP.get(index); }
    public static int regionColor(int type) { return type < 0 || type >= REGION_TYPE_COLORS.length ? -7106930 : REGION_TYPE_COLORS[type]; }
    public static String[] regionTypeNames() { return REGION_TYPE_NAMES.clone(); }
    public static int[] regionTypeColors() { return REGION_TYPE_COLORS.clone(); }
    public static int regionAt(double x, double z) {
        int[] grid = worldToGrid(x, z);
        if (grid[0] < 0 || grid[0] >= MAP_SIZE || grid[1] < 0 || grid[1] >= MAP_SIZE) return -1;
        RegionInfo info = REGION_MAP.get(grid[1] * MAP_SIZE + grid[0]);
        return info == null ? -1 : info.regionId();
    }
    public static String regionTypeName(double x, double z) {
        int[] grid = worldToGrid(x, z);
        if (grid[0] < 0 || grid[0] >= MAP_SIZE || grid[1] < 0 || grid[1] >= MAP_SIZE) return "Unknown";
        RegionInfo info = REGION_MAP.get(grid[1] * MAP_SIZE + grid[0]);
        return info == null ? "Unknown" : REGION_TYPE_NAMES[info.regionType()];
    }
    public static int[] worldToGrid(double x, double z) {
        return new int[]{(int)((x + MAP_OFFSET) / REGION_SIZE), (int)((z + MAP_OFFSET) / REGION_SIZE)};
    }
    public static double[] worldToCellPosition(double x, double z) {
        return new double[]{positiveModulo(x + MAP_OFFSET, REGION_SIZE) / REGION_SIZE,
                            positiveModulo(z + MAP_OFFSET, REGION_SIZE) / REGION_SIZE};
    }
    private static double positiveModulo(double value, double divisor) {
        double mod = value % divisor;
        return mod < 0.0 ? mod + divisor : mod;
    }
    private static Map<Integer, RegionInfo> buildRegionMap() {
        int[][] layout = {
            {82,5},{100,3},{101,3},{102,3},{103,2},{104,2},{105,2},{106,2},{91,2},
            {83,5},{44,3},{75,3},{42,3},{41,2},{40,2},{39,2},{38,2},{92,2},
            {84,5},{45,3},{14,3},{13,3},{12,2},{11,2},{10,2},{37,2},{93,2},
            {85,5},{46,5},{74,5},{3,3},{2,2},{1,2},{25,2},{36,2},{94,2},
            {86,4},{47,4},{72,4},{71,4},{5,2},{4,2},{24,2},{35,2},{95,2},
            {87,4},{51,1},{17,1},{9,0},{8,0},{7,0},{23,0},{34,0},{96,2},
            {88,4},{54,1},{18,1},{61,0},{62,0},{21,0},{22,0},{33,0},{97,0},
            {89,0},{26,1},{27,0},{28,0},{29,0},{30,0},{59,0},{32,0},{98,0},
            {90,0},{107,1},{108,1},{109,1},{110,1},{111,1},{112,1},{113,1},{99,0}
        };
        Map<Integer, RegionInfo> result = new HashMap<>();
        for (int i = 0; i < layout.length; i++) result.put(i, new RegionInfo(layout[i][0], layout[i][1]));
        return Map.copyOf(result);
    }
}
