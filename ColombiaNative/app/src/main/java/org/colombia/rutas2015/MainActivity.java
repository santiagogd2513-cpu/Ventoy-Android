package org.colombia.rutas2015;
import android.app.Activity;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.Choreographer;
import android.widget.FrameLayout;
import android.widget.Button;
import com.google.android.filament.*;
import com.google.android.filament.android.UiHelper;
public class MainActivity extends Activity {
 private Engine engine; private Renderer renderer; private Scene scene; private com.google.android.filament.View view;
 private Camera camera; private Skybox sky; private SwapChain chain; private UiHelper helper; private int cameraId, sun;
 private boolean running, ultra;
 private final Choreographer.FrameCallback frame = new Choreographer.FrameCallback(){
  public void doFrame(long time){ if(!running)return; Choreographer.getInstance().postFrameCallback(this);
   if(chain!=null && helper.isReadyToRender() && renderer.beginFrame(chain,time)){renderer.render(view);renderer.endFrame();}
  }
 };
 @Override public void onCreate(Bundle b){super.onCreate(b); Filament.init();
  engine=Engine.create(); renderer=engine.createRenderer(); scene=engine.createScene(); view=engine.createView();
  cameraId=EntityManager.get().create(); camera=engine.createCamera(cameraId);
  camera.lookAt(0,3,8,0,1,0,0,1,0); view.setCamera(camera);view.setScene(scene);
  sky=new Skybox.Builder().color(0.12f,0.35f,0.65f,1f).build(engine);scene.setSkybox(sky);
  sun=EntityManager.get().create();new LightManager.Builder(LightManager.Type.SUN).color(1f,0.95f,0.85f).intensity(100000f).direction(0.3f,-1f,-0.4f).castShadows(true).build(engine,sun);scene.addEntity(sun);
  view.setAntiAliasing(com.google.android.filament.View.AntiAliasing.FXAA);
  SurfaceView surface=new SurfaceView(this); FrameLayout layout=new FrameLayout(this);layout.addView(surface);
  Button toggle=new Button(this);toggle.setText("Full HD / 4K");layout.addView(toggle,new FrameLayout.LayoutParams(-2,-2));setContentView(layout);
  helper=new UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK);
  helper.setRenderCallback(new UiHelper.RendererCallback(){
   public void onNativeWindowChanged(Surface s){if(chain!=null)engine.destroySwapChain(chain);chain=engine.createSwapChain(s);}
   public void onDetachedFromSurface(){if(chain!=null){engine.destroySwapChain(chain);engine.flushAndWait();chain=null;}}
   public void onResized(int w,int h){if(h>0){view.setViewport(new Viewport(0,0,w,h));camera.setProjection(60.0,(double)w/h,0.1,2000.0,Camera.Fov.VERTICAL);}}
  });
  helper.setDesiredSize(1920,1080);helper.attachTo(surface);
  toggle.setOnClickListener(v->{ultra=!ultra;helper.setDesiredSize(ultra?3840:1920,ultra?2160:1080);toggle.setText(ultra?"4K":"Full HD");});
 }
 @Override protected void onResume(){super.onResume();running=true;Choreographer.getInstance().postFrameCallback(frame);}
 @Override protected void onPause(){running=false;Choreographer.getInstance().removeFrameCallback(frame);super.onPause();}
 @Override protected void onDestroy(){helper.detach();scene.removeEntity(sun);engine.destroyEntity(sun);EntityManager.get().destroy(sun);scene.setSkybox(null);engine.destroySkybox(sky);engine.destroyView(view);engine.destroyScene(scene);engine.destroyRenderer(renderer);engine.destroyCameraComponent(cameraId);EntityManager.get().destroy(cameraId);engine.destroy();super.onDestroy();}
}
