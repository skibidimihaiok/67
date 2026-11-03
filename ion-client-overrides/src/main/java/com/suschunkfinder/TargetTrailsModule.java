package com.suschunkfinder;
import java.util.*; import net.minecraft.client.Minecraft; import net.minecraft.world.entity.LivingEntity; import net.minecraft.world.phys.Vec3;
public final class TargetTrailsModule extends ClientModule implements EspSource {
 private final NumberSetting length=add(NumberSetting.of("Trail length",24,4,60,1)); private final ArrayDeque<Vec3> path=new ArrayDeque<>(); private int target=-1;
 public TargetTrailsModule(){super("Target Trails","Smooth animated ribbon following the crosshair target.",Category.RENDER);}
 public void tick(Minecraft c){if(!(c.crosshairPickEntity instanceof LivingEntity e)||!e.isAlive()){path.clear();target=-1;return;}if(target!=e.getId()){path.clear();target=e.getId();}Vec3 p=e.position().add(0,e.getBbHeight()*0.5,0);if(path.isEmpty()||path.getLast().distanceToSqr(p)>0.012){path.addLast(p);while(path.size()>length.getInt())path.removeFirst();}}
 public void collect(EspBatch b,float pt){float[] q=VisualEffects.color();VisualEffects.trail(b,new ArrayList<>(path),q[0],q[1],q[2]);}
 protected void onDisable(){path.clear();target=-1;}
}