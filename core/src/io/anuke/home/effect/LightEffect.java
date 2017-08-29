package io.anuke.home.effect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pools;

import io.anuke.home.Vars;
import io.anuke.home.world.Tile;
import io.anuke.home.world.World;
import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.graphics.Hue;
import io.anuke.ucore.lights.Light;
import io.anuke.ucore.lights.PointLight;
import io.anuke.ucore.lights.RayHandler;
import io.anuke.ucore.util.Mathf;

public class LightEffect extends RenderEffect{
	private final int rayamount = 140;
	private final int chunksize = 3 * Vars.tilesize;
	private RayHandler rays = new RayHandler();
	
	private int lastcamx, lastcamy;

	@Override
	public void init(){
		rays.setTint(Hue.rgb(0.65, 0.5, 0.3).mul(1.1f));
	}
	
	@Override
	public void reset(){
		rays.resizeFBO(Gdx.graphics.getWidth()/4, Gdx.graphics.getHeight()/4);
		rays.pixelate();
		rays.setBounds(0, 0, World.width()*Vars.tilesize, World.height()*Vars.tilesize);
		rays.updateRects();
		
		lastcamx = lastcamy = 0;
	}

	@Override
	public void update(){
		OrthographicCamera cam = DrawContext.camera;
		int camcx = Mathf.scl(cam.position.x, chunksize), camcy = Mathf.scl(cam.position.y, chunksize);
		int cwidth = Mathf.scl(cam.viewportWidth, Vars.tilesize) + 1, cheight = Mathf.scl(cam.viewportHeight, Vars.tilesize) + 1;
		int camx = Mathf.scl(cam.position.x, Vars.tilesize), camy = Mathf.scl(cam.position.y, Vars.tilesize);
		
		if(camcx != lastcamx || camcy != lastcamy){
			for(Rectangle rect : rays.getRects()){
				Pools.free(rect);
			}
			rays.clearRects();
			
			for(int x = 0; x < cwidth; x ++){
				for(int y = 0; y < cheight; y ++){
					int wx = camx + x - cwidth/2, wy = camy + y - cheight/2;
					Tile tile = World.get(wx, wy);
					
					if(tile != null && tile.wall.type.solid(tile.wall)){
						Rectangle rect = Pools.obtain(Rectangle.class);
						tile.wall.type.getHitbox(tile, tile.wall, rect);
						rays.addRect(rect);
					}
				}
			}
			
			
			//rays.setBounds(cam.position.x - cam.viewportWidth/2, 
			//		cam.position.y - cam.viewportHeight/2, cam.viewportWidth, cam.viewportHeight);
			
			rays.updateRects();
		}
		
		lastcamx = camcx;
		lastcamy = camcy;
		
		//rays.setTint(Hue.rgb(0.65, 0.5, 0.3).mul(1.1f + MathUtils.sin(Timers.time() / 10f) / 30f));
	}
	
	public Light addLight(float radius){
		Light light = new PointLight(rays, rayamount, Color.WHITE, radius, 0, 0);
		light.setSoftnessLength(50f);
		light.setNoise(4f, 1f, 4f);
		return light;
	}
	
	public void drawLight(){
		rays.setCombinedMatrix(DrawContext.camera);
		rays.updateAndRender();
	}
	
	@Override
	public void dispose(){
		rays.dispose();
	}

}
