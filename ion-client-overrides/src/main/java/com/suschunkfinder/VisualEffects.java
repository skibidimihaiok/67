package com.suschunkfinder;
import net.minecraft.world.phys.Vec3;
/** Shared geometry for ION's client-rendered visual effects; never spawns Minecraft particles. */
final class VisualEffects {
 private VisualEffects(){}
 static void circle(EspBatch b, Vec3 c,double radius,double y,float r,float g,float bl,float a,double width){
  Vec3 prev=null; for(int i=0;i<=48;i++){double t=Math.PI*2*i/48;Vec3 p=new Vec3(c.x+Math.cos(t)*radius,c.y+y,c.z+Math.sin(t)*radius);if(prev!=null)b.line(prev,p,r,g,bl,a,width);prev=p;}
 }
 static void trail(EspBatch b, java.util.List<Vec3> points,float r,float g,float bl){
  for(int i=1;i<points.size();i++){float a=(float)i/points.size();b.line(points.get(i-1),points.get(i),r,g,bl,0.12f+0.82f*a,0.035+0.035*a);}
 }
 static float[] color(){return ThemeModule.rgb();}
}