package com.suschunkfinder;

/** Shared state for Free Look and Freecam. */
public final class CameraControl {
    public static boolean freeLookActive;
    public static boolean freecamActive;
    public static float yaw;
    public static float pitch;
    public static float sensitivity = 1.0f;
    public static double x, y, z, px, py, pz;
    private CameraControl() { }
    public static boolean active() { return freeLookActive || freecamActive; }
}
