package com.suschunkfinder;
import net.minecraft.client.Minecraft; import net.minecraft.world.phys.Vec3;
public final class HaloModule extends ClientModule implements EspSource {
 private final NumberSetting radius=add(NumberSetting.of("Radius",0.55,0.2,1,0.05)); private final NumberSetting height=add(NumberSetting.of("Height",2,1.5,3,0.1));
 public HaloModule(){super("Halo","Smooth double neon halo above your head.",Category.RENDER);}
 public void collect(EspBatch b,float pt){Minecraft c=Minecraft.getInstance();if(c.player==null)return;float[] q=VisualEffects.color();Vec3 p=c.player.position();double t=System.currentTimeMillis()*0.001;VisualEffects.circle(b,p,radius.get(),height.get(),q[0],q[1],q[2],0.95f,0.045);VisualEffects.circle(b,p,radius.get()*0.72,height.get()+Math.sin(t)*0.08,q[0],q[1],q[2],0.55f,0.025);}
}