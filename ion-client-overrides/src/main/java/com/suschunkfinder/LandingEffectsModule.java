package com.suschunkfinder;
import java.util.*; import net.minecraft.client.Minecraft; import net.minecraft.world.phys.Vec3;
public final class LandingEffectsModule extends ClientModule implements EspSource {
 private final List<double[]> bursts=new ArrayList<>(); private boolean grounded;
 public LandingEffectsModule(){super("Landing Effects","Layered expanding shockwave rings when you land.",Category.RENDER);}
 public void tick(Minecraft c){if(c.player==null)return;boolean g=c.player.onGround();if(!grounded&&g)bursts.add(new double[]{c.player.getX(),c.player.getY()+0.03,c.player.getZ(),System.currentTimeMillis()});grounded=g;}
 public void collect(EspBatch b,float pt){float[] q=VisualEffects.color();long n=System.currentTimeMillis();bursts.removeIf(v->n-v[3]>700);for(double[] v:bursts){double a=(n-v[3])/700.0;Vec3 p=new Vec3(v[0],v[1],v[2]);VisualEffects.circle(b,p,0.2+a*1.35,0,q[0],q[1],q[2],(float)(1-a),0.06);VisualEffects.circle(b,p,0.12+a*0.9,0.015,q[0],q[1],q[2],(float)(0.55*(1-a)),0.025);}}
 protected void onDisable(){bursts.clear();grounded=false;}
}