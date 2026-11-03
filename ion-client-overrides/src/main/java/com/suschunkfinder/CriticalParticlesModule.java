package com.suschunkfinder;
import java.util.*;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.Vec3;
/** Custom angular impact flash for falling attacks; never emits vanilla particles. */
public final class CriticalParticlesModule extends ClientModule implements EspSource {
 private final List<double[]> bursts=new ArrayList<>();
 public CriticalParticlesModule(){super("Critical Particles","Sharp expanding critical-hit geometry instead of Minecraft particles.",Category.RENDER);AttackEntityCallback.EVENT.register((player,world,hand,entity,hit)->{Minecraft c=Minecraft.getInstance();if(isEnabled()&&c.player==player&&c.level==world&&!player.onGround()&&player.fallDistance>0)bursts.add(new double[]{entity.getX(),entity.getY()+entity.getBbHeight()*0.6,entity.getZ(),System.currentTimeMillis()});return InteractionResult.PASS;});}
 public void collect(EspBatch b,float pt){float[] q=VisualEffects.color();long n=System.currentTimeMillis();bursts.removeIf(v->n-v[3]>500);for(double[] v:bursts){double a=(n-v[3])/500.0;Vec3 p=new Vec3(v[0],v[1],v[2]);VisualEffects.circle(b,p,0.12+a*0.85,0,q[0],q[1],q[2],(float)(1-a),0.05);for(int i=0;i<6;i++){double t=i*Math.PI/3;Vec3 d=new Vec3(Math.cos(t),Math.sin(t),0);b.line(p.add(d.scale(0.12)),p.add(d.scale(0.55*(1-a))),q[0],q[1],q[2],(float)(1-a),0.04);}}}
 protected void onDisable(){bursts.clear();}
}