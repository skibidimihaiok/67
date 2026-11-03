package com.suschunkfinder;
import net.minecraft.client.Minecraft; import net.minecraft.world.phys.Vec3;
public final class OrbitParticlesModule extends ClientModule implements EspSource {
 private final NumberSetting count=add(NumberSetting.of("Orbit count",3,1,6,1)); private final NumberSetting radius=add(NumberSetting.of("Orbit radius",0.8,0.3,1.8,0.1)); private final NumberSetting speed=add(NumberSetting.of("Orbit speed",1,0.2,3,0.1));
 public OrbitParticlesModule(){super("Orbit Particles","Multi-layer animated orbital ribbons, rendered geometry only.",Category.RENDER);}
 public void collect(EspBatch b,float pt){Minecraft c=Minecraft.getInstance();if(c.player==null)return;float[] q=VisualEffects.color();Vec3 p=c.player.position();double t=System.currentTimeMillis()*0.001*speed.get();for(int n=0;n<count.getInt();n++){Vec3 prev=null;for(int i=0;i<=64;i++){double a=i*Math.PI*2/64+t+n*Math.PI/count.getInt();Vec3 v=p.add(Math.cos(a)*radius.get(),0.85+Math.sin(a*2+n)*0.42,Math.sin(a)*radius.get());if(prev!=null)b.line(prev,v,q[0],q[1],q[2],0.72f,0.035);prev=v;}}}
}