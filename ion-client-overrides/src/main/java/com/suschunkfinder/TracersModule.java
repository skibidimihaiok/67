package com.suschunkfinder;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.AgeableWaterCreature;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/** Configurable entity tracers, with ION-side HUD arrows for off-screen targets. */
public final class TracersModule extends ClientModule implements EspSource {
    private final BoolSetting ignoreSelf = add(BoolSetting.of("Ignore Self", true));
    private final BoolSetting players = add(BoolSetting.of("Players", true));
    private final BoolSetting animals = add(BoolSetting.of("Animals", false));
    private final BoolSetting waterAnimals = add(BoolSetting.of("Water Animals", false));
    private final BoolSetting monsters = add(BoolSetting.of("Monsters", true));
    private final BoolSetting ambient = add(BoolSetting.of("Ambient", false));
    private final BoolSetting misc = add(BoolSetting.of("Misc", false));
    private final BoolSetting ignoreFriends = add(BoolSetting.of("Ignore Friends", false));
    private final BoolSetting showInvisible = add(BoolSetting.of("Show Invisible", true));
    private final ModeSetting style = add(ModeSetting.of("Style", 0, "Lines", "Offscreen"));
    private final ModeSetting target = add(ModeSetting.of("Target", 1, "Head", "Body", "Feet"));
    private final BoolSetting stem = add(BoolSetting.of("Stem", true));
    private final NumberSetting maxDistance = add(NumberSetting.of("Max Distance", 256, 0, 256, 1));
    private final NumberSetting distanceOffscreen = add(NumberSetting.of("Distance Offscreen", 200, 0, 500, 1));
    private final NumberSetting sizeOffscreen = add(NumberSetting.of("Size Offscreen", 10, 2, 50, 1));
    private final BoolSetting blinkOffscreen = add(BoolSetting.of("Blink Offscreen", true));
    private final NumberSetting blinkSpeed = add(NumberSetting.of("Blink Offscreen Speed", 4, 1, 15, 1));
    private final BoolSetting distanceColors = add(BoolSetting.of("Distance Colors", false));
    private final BoolSetting showFriendColors = add(BoolSetting.of("Show Friend Colors", true));
    private final BoolSetting teamColors = add(BoolSetting.of("Team Colors", true));
    private final ColorSetting friendColor = add(new ColorSetting("Friend Color", 0xFF00AA54));
    private final ColorSetting playersColor = add(new ColorSetting("Players Color", 0xFFEE5555));
    private final ColorSetting animalsColor = add(new ColorSetting("Animals Color", 0xFF55DD77));
    private final ColorSetting waterColor = add(new ColorSetting("Water Animals Color", 0xFF55BBDD));
    private final ColorSetting monstersColor = add(new ColorSetting("Monsters Color", 0xFFFFAA55));
    private final ColorSetting ambientColor = add(new ColorSetting("Ambient Color", 0xFFAAAAFF));
    private final ColorSetting miscColor = add(new ColorSetting("Misc Color", 0xFFCCCCCC));
    private final List<Entity> lastTargets = new ArrayList<>();

    public TracersModule() {
        super("Tracers", "Lines or off-screen arrows for selected entity groups.", Category.RENDER);
    }

    private ColorSetting group(Entity e) {
        if (e instanceof Player) return players.get() ? playersColor : null;
        if (e instanceof AgeableWaterCreature) return waterAnimals.get() ? waterColor : null;
        if (e instanceof Animal) return animals.get() ? animalsColor : null;
        if (e instanceof Enemy) return monsters.get() ? monstersColor : null;
        if (e instanceof AmbientCreature) return ambient.get() ? ambientColor : null;
        if (e instanceof Mob) return misc.get() ? miscColor : null;
        return misc.get() ? miscColor : null;
    }

    private List<Entity> targets(Minecraft client) {
        List<Entity> out = new ArrayList<>();
        if (client.player == null || client.level == null) return out;
        for (Entity e : client.level.entitiesForRendering()) {
            if (e == client.player && ignoreSelf.get()) continue;
            if (e == client.player || client.player.distanceTo(e) > maxDistance.get()) continue;
            if (!showInvisible.get() && e.isInvisible()) continue;
            if (e instanceof Player p && ignoreFriends.get() && NameList.FRIENDS.contains(p.getGameProfile().name())) continue;
            if (group(e) != null) out.add(e);
        }
        return out;
    }

    @Override public void collect(EspBatch batch, float pt) {
        Minecraft client = Minecraft.getInstance();
        if (style.index() != 0 || client.player == null) return;
        lastTargets.clear();
        lastTargets.addAll(targets(client));
        // Use the active render camera, not the player eye, so the ray begins at the crosshair in camera-controlled views.
        Vec3 eye = client.gameRenderer.getMainCamera().position();
        for (Entity e : lastTargets) {
            ColorSetting setting = group(e);
            int argb = colorFor(client, e, setting);
            float r=((argb>>>16)&255)/255f, g=((argb>>>8)&255)/255f, b=(argb&255)/255f;
            Vec3 feet=e.getPosition(pt);
            double h=e.getBbHeight();
            double y=switch(target.index()){case 0->h;case 2->0;default->h*0.5;};
            Vec3 end=feet.add(0,y,0);
            batch.line(eye,end,r,g,b,((argb>>>24)&255)/255f,0.055);
            if (stem.get()) batch.line(feet,feet.add(0,h,0),r,g,b,0.55f,0.025);
        }
    }

    private int colorFor(Minecraft client, Entity e, ColorSetting base) {
        if (e instanceof Player p) {
            if (NameList.FRIENDS.contains(p.getGameProfile().name()) && showFriendColors.get()) return friendColor.argb();
            if (teamColors.get() && !distanceColors.get() && p.getTeamColor() != -1) return 0xFF000000 | (p.getTeamColor() & 0xFFFFFF);
        }
        if (!distanceColors.get()) return base.argb();
        float t=(float)Math.min(1.0,client.player.distanceTo(e)/Math.max(1.0,maxDistance.get()));
        int red=(int)(70+185*t), green=(int)(220-160*t);
        return 0xFF000000 | (red<<16) | (green<<8) | 0x44;
    }

    @Override public void renderHud(GuiGraphics g) {
        if (style.index()!=1) return;
        Minecraft client=Minecraft.getInstance();
        if (client.player==null || client.level==null) return;
        int cx=g.guiWidth()/2, cy=g.guiHeight()/2;
        double yaw=Math.toRadians(client.player.getYRot());
        float blink=blinkOffscreen.get()?(float)(0.55+0.45*Math.sin(System.currentTimeMillis()*blinkSpeed.get()*0.004)):1f;
        for (Entity e:targets(client)) {
            Vec3 d=e.position().subtract(client.player.position());
            double angle=Math.atan2(d.z,d.x)-yaw+Math.PI/2.0;
            double distance=client.player.distanceTo(e);
            int radius=(int)distanceOffscreen.getInt();
            int x=cx+(int)(Math.cos(angle)*radius), y=cy+(int)(Math.sin(angle)*radius);
            ColorSetting color=group(e);
            int argb=colorFor(client,e,color);
            int alpha=(int)(((argb>>>24)&255)*blink);
            int col=(alpha<<24)|(argb&0xFFFFFF);
            String glyph="▲";
            g.drawString(client.font,glyph,x-(int)(sizeOffscreen.get()/2),y-(int)(sizeOffscreen.get()/2),col,true);
        }
    }
}
