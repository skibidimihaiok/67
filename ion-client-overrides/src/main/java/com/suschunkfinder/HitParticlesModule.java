package com.suschunkfinder;
import java.util.*; import net.fabricmc.fabric.api.event.player.AttackEntityCallback; import net.minecraft.client.Minecraft; import net.minecraft.world.InteractionResult; import net.minecraft.world.phys.Vec3;
/** Custom geometric impact burst. Does not spawn vanilla particles. */
public final class HitParticlesModule extends ClientModule implements EspSource {
 private final List<double[]> hits=new ArrayList<>();
 public HitParticlesModule(){super("Hit Particles","Stylized expanding impact rings and cross-shaped flashes.",Category.RENDER);AttackEntityCallback.EVENT.register((player,world,hand,entity,hit)->{Minecraft c=Minecraft.getInstance();if(isEnabled()&&c.player==player)hits.add(new double[]{entity.getX(),entity.getY()+entity.getBbHeight()*0.5,entity.getZ(),System.currentTimeMillis()});return InteractionResult.PASS;});}
 public void collect(EspBatch b,float pt){float[] q=VisualEffects.color();long n=System.currentTimeMillis();hits.removeIf(v->n-v[3]>450);for(double[] v:hits){double a=(n-v[3])/450.0;Vec3 p=new Vec3(v[0],v[1],v[2]);VisualEffects.circle(b,p,0.15+a*0.75,0,q[0],q[1],q[2],(float)(1-a),0.055);for(int i=0;i<4;i++){double t=i*Math.PI/2+a*0.5;Vec3 d=new Vec3(Math.cos(t),Math.sin(t),0);b.line(p.add(d.scale(0.1)),p.add(d.scale(0.45*(1-a))),q[0],q[1],q[2],(float)(1-a),0.045);}}}
 protected void onDisable(){hits.clear();}
}