package io.anuke.home;

import static io.anuke.home.Vars.tilesize;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pools;

import io.anuke.home.effect.*;
import io.anuke.home.world.Tile;
import io.anuke.home.world.World;
import io.anuke.home.world.blocks.Blocks;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.facet.FacetList;
import io.anuke.ucore.graphics.Cache;
import io.anuke.ucore.graphics.Caches;
import io.anuke.ucore.util.ClassMap;
import io.anuke.ucore.util.Mathf;

public class Renderer{
	private static int chunksize = 32;
	private static Cache[][] caches;
	private static FacetList[][] facets, writeback;
	private static ClassMap<RenderEffect> effects = new ClassMap<>();
	private static int lastcamx = -100, lastcamy = -100;
	private static boolean resized = false;
	private static TextureRegion region = new TextureRegion(); //temporary region
	
	static{
		addEffects();
	}
	
	private static void addEffects(){
		effects.add(new LightEffect());
		effects.add(new Rain());
		effects.add(new Darkness());
	}
	
	public static TextureRegion getRune(int index){
		region.setRegion(Draw.region("language"));
		region.setRegionX(region.getRegionX() + index*7 + 1);
		region.setRegionY(region.getRegionY());
		region.setRegionWidth(5);
		region.setRegionHeight(5);
		return region;
	}
	
	public static void resetEffects(){
		for(RenderEffect w : effects){
			w.init();
			w.reset();
		}
	}
	
	public static <N extends RenderEffect> N getEffect(Class<N> c){
		return effects.get(c);
	}
	
	public static void setEffectEnabled(Class<? extends RenderEffect> type, boolean enabled){
		effects.get(type).setEnabled(enabled);
	}
	
	public static void updateWalls(){
		resized = true;
	}
	
	public static void clearWorld(){
		resetEffects();
		
		if(caches == null) return;
		
		for(int x = 0; x < caches.length; x ++){
			for(int y = 0; y < caches[x].length; y ++){
				if(caches[x][y] != null){
					caches[x][y].dispose();
					caches[x][y] = null;
				}
			}
		}
	}
	
	public static void clearAll(){
		caches = null;
		clearWorld();
		updateWalls();
	}
	
	public static void renderEffects(){
		
	}
	
	public static void renderTiles(){

		for(RenderEffect effect : effects){
			if(effect.isEnabled())
				effect.update();
		}
		
		if(caches == null){
			caches = new Cache[World.width()/chunksize+1][World.height()/chunksize+1];
		}

		OrthographicCamera camera = Core.camera;

		int crangex = Math.round(camera.viewportWidth * camera.zoom / (chunksize * tilesize)) + 1;
		int crangey = Math.round(camera.viewportHeight * camera.zoom / (chunksize * tilesize)) + 1;

		Draw.end();
		
		//render tile chunks
		for(int x = -crangex; x <= crangex; x++){
			for(int y = crangey; y >= -crangey; y--){
				int worldx = Mathf.scl(camera.position.x, chunksize * tilesize) + x;
				int worldy = Mathf.scl(camera.position.y, chunksize * tilesize) + y;

				if(!Mathf.inBounds(worldx, worldy, caches)){
					continue;
				}

				if(caches[worldx][worldy] == null){
					cacheChunk(worldx, worldy);
				}

				caches[worldx][worldy].render();
			}
		}

		Draw.begin();

	}
	
	public static void updateFacets(){
		OrthographicCamera camera = Core.camera;

		int camx = Mathf.scl(camera.position.x, tilesize);
		int camy = Mathf.scl(camera.position.y, tilesize);

		int padding = 6;
		//view range x/y (block only)
		int vrx = Mathf.scl2(camera.viewportWidth * camera.zoom, tilesize)+padding;
		int vry = Mathf.scl2(camera.viewportHeight * camera.zoom, tilesize)+padding;
		
		//change renderable list size on screen resize/startup
		if(facets == null || facets.length != vrx || facets[0].length != vry){
			
			if(facets != null){
				for(int rx = 0; rx < facets.length; rx++){
					for(int ry = 0; ry < facets[0].length; ry++){
						facets[rx][ry].free();
					}
				}
			}

			facets = new FacetList[vrx][vry];
			writeback = new FacetList[vrx][vry];
			
			//invalidate cam position
			lastcamx = -1;
			lastcamy = -1;

			for(int rx = 0; rx < vrx; rx++){
				for(int ry = 0; ry < vry; ry++){
					facets[rx][ry] = Pools.obtain(FacetList.class);
				}
			}
			
			resized = true;
		}
		
		
		//if the camera moved, re-render everything
		if(lastcamx != camx || lastcamy != camy){
			
			if(!resized){
			int shiftx = -(camx-lastcamx);
			int shifty = -(camy-lastcamy);
			
			//clear writeback
			for(int x = 0; x < vrx; x++){
				for(int y = 0; y < vry; y++){
					writeback[x][y] = null;
				}
			}
			
			//shift everything
			for(int x = 0; x < vrx; x++){
				for(int y = 0; y < vry; y++){
					
					int targetx = x + shiftx;
					int targety = y + shifty;
					
					if(!Mathf.inBounds(targetx, targety, facets)){
						facets[x][y].free();
						Pools.free(facets[x][y]);
					}else{
						writeback[targetx][targety] = facets[x][y];
						facets[x][y] = null;
					}
				}
			}
			
			for(int x = 0; x < vrx; x++){
				for(int y = 0; y < vry; y++){
					facets[x][y] = writeback[x][y];
				}
			}
			
			}
			
			for(int x = 0; x < vrx; x++){
				for(int y = 0; y < vry; y++){
					int worldx = x - vrx / 2 + camx;
					int worldy = y - vry / 2 + camy;
					
					if(facets[x][y] != null && !resized){
						continue;
					}else if(!resized){
						facets[x][y] = Pools.obtain(FacetList.class);
					}

					Tile tile = World.get(worldx, worldy);

					if(tile == null)
						continue;

					if(tile.wall != Blocks.air){
						tile.wall.draw(facets[x][y], tile);
					}
				}
			}

			lastcamx = camx;
			lastcamy = camy;
			
			resized = false;
		}
	}

	public static void renderWorld(){
		renderTiles();
		updateFacets();
	}
	
	public static void updateWall(int x, int y){
		//TODO optimization
		int camx = x - Mathf.scl(Core.camera.position.x, tilesize) + facets.length/2;
		int camy = y - Mathf.scl(Core.camera.position.y, tilesize) + facets[0].length/2;
		
		//invalid coords?
		if(!Mathf.inBounds(camx, camy, facets)) return;
		
		facets[camx][camy].free();
		
		Tile tile = World.get(x, y);
		
		tile.wall.draw(facets[camx][camy], tile);
	}
	
	public static void updateFloor(int x, int y){
		int cx = x/chunksize;
		int cy = y/chunksize;
		
		if(caches[cx][cy] != null){
			caches[cx][cy].dispose();
		}
		
		caches[cx][cy] = null;
	}
	
	private static void renderBottom(Tile tile){
		float length =  1 + tile.rand(6);
		float rnd = 0.95f + 0.05f * (tile.rand(3)-1);
		Caches.color(tile.floor.edgecolor.cpy().mul(rnd, rnd, rnd, 1f));
		for(int i = 0; i < length; i ++){
			Caches.draw(Draw.region("wall"), tile.worldx()-6, tile.worldy()-6-i*6, 12, -6);
		}
		Caches.draw("edge", tile.worldx(), tile.worldy() - 12 - length*6);
	}

	private static void cacheChunk(int x, int y){
		Caches.begin(1600);
		
		for(int tilex = x * chunksize; tilex < (x + 1) * chunksize; tilex++){
			for(int tiley = y * chunksize; tiley < (y + 1) * chunksize; tiley++){
				Tile tile = World.get(tilex, tiley);
				if(tile != null && tile.floor != Blocks.air && World.get(tilex, tiley-1) != null && World.get(tilex, tiley-1).floor == Blocks.air){
					renderBottom(tile);
				}
			}
		}
		
		Caches.color();
		
		for(int tilex = x * chunksize; tilex < (x + 1) * chunksize; tilex++){
			for(int tiley = y * chunksize; tiley < (y + 1) * chunksize; tiley++){
				if(World.get(tilex, tiley) != null){
					World.get(tilex, tiley).floor.drawCache(World.get(tilex, tiley));
				}
			}
		}
		
		for(int tilex = x * chunksize; tilex < (x + 1) * chunksize; tilex++){
			for(int tiley = y * chunksize; tiley < (y + 1) * chunksize; tiley++){
				if(World.get(tilex, tiley) != null){
					World.get(tilex, tiley).decal.drawCache(World.get(tilex, tiley));
				}
			}
		}
		caches[x][y] = Caches.end();
	}
}
