package org.colombia.rutas2015;
/** Synthetic terrain prototype, coordinates in meters. Not historical GIS data. */
public final class TerrainData {
 public static final int SIZE=128, STEP=4, N=SIZE/STEP+1;
 public static float road(float z){return (float)(45*Math.sin(z*.006));}
 public static float height(float x,float z){
  float d=Math.abs(x-road(z));float t=Math.min(1,Math.max(0,(d-6)/70));t=t*t*(3-2*t);
  return (float)(24*Math.sin(z*.002)+t*(38+18*Math.sin(x*.025)*Math.cos(z*.019)+9*Math.sin((x+z)*.071)));
 }
 public static float[] vertices(int tx,int tz){
  float[] a=new float[N*N*3];int k=0;
  for(int j=0;j<N;j++)for(int i=0;i<N;i++){float x=tx*SIZE+i*STEP,z=tz*SIZE+j*STEP;a[k++]=x;a[k++]=height(x,z);a[k++]=z;}return a;
 }
 public static short[] indices(){short[] a=new short[(N-1)*(N-1)*6];int k=0;for(int j=0;j<N-1;j++)for(int i=0;i<N-1;i++){int p=j*N+i;a[k++]=(short)p;a[k++]=(short)(p+N);a[k++]=(short)(p+1);a[k++]=(short)(p+1);a[k++]=(short)(p+N);a[k++]=(short)(p+N+1);}return a;}
}
