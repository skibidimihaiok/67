package com.suschunkfinder;
import net.minecraft.client.Minecraft; import net.minecraft.world.entity.LivingEntity; import net.minecraft.world.phys.AABB;
public final class TargetHighlightModule extends ClientModule implements EspSource {
 private final NumberSetting height=add(NumberSetting.of("Height offset",0.5,0.1,1.5,0.1));
 public TargetHighlightModule(){super("Target Highlight","Neon target frame with animated ground rings.",Category.RENDER);}
 public void collect(EspBatch b,float pt){Minecraft c=Minecraft.getInstance();if(!(c.crosshairPickEntity instanceof LivingEntity e)||!e.isAlive())return;float[] q=VisualEffects.color();AABB a=e.getBoundingBox().inflate(0.06);b.outline(a.minX,a.minY,a.minZ,a.maxX,a.maxY,a.maxZ,q[0],q[1],q[2],0.95f,0.025);double pulse=0.8+Math.sin(System.currentTimeMillis()*0.006)*0.12;VisualEffects.circle(b,e.position(),e.getBbWidth()*pulse,0.04,q[0],q[1],q[2],0.85f,0.045);VisualEffects.circle(b,e.position(),e.getBbWidth()*1.25,height.get(),q[0],q[1],q[2],0.45f,0.025);}
}