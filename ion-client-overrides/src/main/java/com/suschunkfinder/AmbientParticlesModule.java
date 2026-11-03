package com.suschunkfinder;
import net.minecraft.client.Minecraft; import net.minecraft.world.phys.Vec3;
/** Animated geometric orbit aura, rendered by ION rather than vanilla particles. */
public final class AmbientParticlesModule extends ClientModule implements EspSource {
 private final NumberSetting radius=add(NumberSetting.of("Aura radius",1.5,0.5,4,0.1));
 private final NumberSetting speed=add(NumberSetting.of("Rotation speed",1.0,0.2,3,0.1));
 public AmbientParticlesModule(){super("Ambient Particles","Custom floating orbit lines around you; no game particles.",Category.RENDER);}
 public void collect(EspBatch b,float pt){Minecraft c=Minecraft.getInstance();if(c.player==null)return;float[] q=VisualEffects.color();double t=System.currentTimeMillis()*0.001*speed.get();Vec3 p=c.player.position();VisualEffects.circle(b,p,radius.get(),0.55,q[0],q[1],q[2],0.75f,0.035);for(int i=0;i<3;i++){double a=t+i*Math.PI*2/3;Vec3 x=p.add(Math.cos(a)*radius.get(),0.3+Math.sin(t*1.4+i)*0.5,Math.sin(a)*radius.get());Vec3 y=p.add(Math.cos(a+0.3)*radius.get(),1.65,Math.sin(a+0.3)*radius.get());b.line(x,y,q[0],q[1],q[2],0.8f,0.045);}}
}