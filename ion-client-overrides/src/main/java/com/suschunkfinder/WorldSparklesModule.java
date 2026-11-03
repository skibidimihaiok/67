package com.suschunkfinder;
import net.minecraft.client.Minecraft; import net.minecraft.world.phys.Vec3;
public final class WorldSparklesModule extends ClientModule implements EspSource {
 private final NumberSetting radius=add(NumberSetting.of("Radius",3,1,8,0.5)); private final NumberSetting count=add(NumberSetting.of("Glints",8,2,20,1));
 public WorldSparklesModule(){super("World Sparkles","Floating custom geometric glints around you, not game particles.",Category.RENDER);}
 public void collect(EspBatch b,float pt){Minecraft c=Minecraft.getInstance();if(c.player==null)return;float[] q=VisualEffects.color();Vec3 p=c.player.position();double t=System.currentTimeMillis()*0.001;for(int i=0;i<count.getInt();i++){double a=t*0.35+i*Math.PI*2/count.getInt();double x=Math.cos(a)*radius.get(),z=Math.sin(a)*radius.get();Vec3 center=p.add(x,0.55+Math.sin(t+i*2)*0.35,z);double s=0.09+0.04*Math.sin(t*2+i);b.line(center.add(-s,0,0),center.add(s,0,0),q[0],q[1],q[2],0.8f,0.025);b.line(center.add(0,-s,0),center.add(0,s,0),q[0],q[1],q[2],0.8f,0.025);b.line(center.add(0,0,-s),center.add(0,0,s),q[0],q[1],q[2],0.8f,0.025);}}
}