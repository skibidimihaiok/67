package com.suschunkfinder;
import java.util.*; import net.minecraft.client.Minecraft; import net.minecraft.world.phys.Vec3;
public final class FootstepParticlesModule extends ClientModule implements EspSource {
 private final List<double[]> rings=new ArrayList<>(); private Vec3 last;
 public FootstepParticlesModule(){super("Footstep Particles","Expanding ground rings at each step; no vanilla particles.",Category.RENDER);}
 public void tick(Minecraft c){if(c.player==null)return;Vec3 p=c.player.position();if(c.player.onGround()&&(last==null||p.distanceToSqr(last)>0.04)){rings.add(new double[]{p.x,p.y+0.025,p.z,System.currentTimeMillis()});if(rings.size()>12)rings.remove(0);}last=p;}
 public void collect(EspBatch b,float pt){float[] q=VisualEffects.color();long n=System.currentTimeMillis();rings.removeIf(v->n-v[3]>650);for(double[] v:rings){double a=(n-v[3])/650.0;VisualEffects.circle(b,new Vec3(v[0],v[1],v[2]),0.15+a*0.75,0,q[0],q[1],q[2],(float)(0.85*(1-a)),0.035);}}
 protected void onDisable(){rings.clear();last=null;}
}