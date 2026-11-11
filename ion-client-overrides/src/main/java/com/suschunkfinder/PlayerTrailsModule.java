package com.suschunkfinder;
import java.util.*; import net.minecraft.client.Minecraft; import net.minecraft.world.phys.Vec3;
public final class PlayerTrailsModule extends ClientModule implements EspSource {
 private final NumberSetting length=add(NumberSetting.of("Trail length",28,6,64,1)); private final ArrayDeque<Vec3> path=new ArrayDeque<>();
 public PlayerTrailsModule(){super("Player Trails","Smooth ribbon along your recent movement path.",Category.RENDER);}
 public void tick(Minecraft c){if(c.player==null){path.clear();return;}Vec3 p=c.player.position().add(0,0.08,0);if(path.isEmpty()||path.getLast().distanceToSqr(p)>0.018){path.addLast(p);while(path.size()>length.getInt())path.removeFirst();}}
 public void collect(EspBatch b,float pt){float[] q=VisualEffects.color();VisualEffects.trail(b,new ArrayList<>(path),q[0],q[1],q[2]);}
 protected void onDisable(){path.clear();}
}