package com.suschunkfinder;
import java.util.*; import net.minecraft.client.Minecraft; import net.minecraft.world.phys.Vec3;
public final class JumpEffectsModule extends ClientModule implements EspSource {
 private final List<double[]> bursts=new ArrayList<>(); private boolean grounded=true;
 public JumpEffectsModule(){super("Jump Effects","Sharp double-ring pulse on takeoff, rendered in the world.",Category.RENDER);}
 public void tick(Minecraft c){if(c.player==null)return;boolean g=c.player.onGround();if(grounded&&!g&&c.player.getDeltaMovement().y>0.1)bursts.add(new double[]{c.player.getX(),c.player.getY()+0.025,c.player.getZ(),System.currentTimeMillis()});grounded=g;}
 public void collect(EspBatch b,float pt){float[] q=VisualEffects.color();long n=System.currentTimeMillis();bursts.removeIf(v->n-v[3]>550);for(double[] v:bursts){double a=(n-v[3])/550.0;Vec3 p=new Vec3(v[0],v[1],v[2]);VisualEffects.circle(b,p,0.15+a*1.1,0,q[0],q[1],q[2],(float)(0.95*(1-a)),0.05);VisualEffects.circle(b,p,0.1+a*0.65,0.02,q[0],q[1],q[2],(float)(0.5*(1-a)),0.025);}}
 protected void onDisable(){bursts.clear();grounded=true;}
}