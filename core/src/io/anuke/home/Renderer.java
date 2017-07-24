package io.anuke.home;

import static io.anuke.home.Vars.tilesize;

import com.badlogic.gdx.graphics.OrthographicCamera;

import io.anuke.home.world.Blocks;
import io.anuke.home.world.Tile;
import io.anuke.home.world.World;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.graphics.Cache;
import io.anuke.ucore.graphics.Caches;
import io.anuke.ucore.renderables.RenderableList;
import io.anuke.ucore.util.Mathf;

public class Renderer{
	private static int chunksize = 32;
	private static Cache[][] caches;
	private static RenderableList[][] renderables;
	private static int lastcamx = -100, lastcamy = -100;
	
	public static void updateWalls(){
		lastcamx = -100;
	}
	
	public static void clearWorld(){
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

	public static void renderWorld(){
		
		if(caches == null){
			caches = new Cache[World.width()/chunksize][World.height()/chunksize];
		}

		OrthographicCamera camera = DrawContext.camera;

		int camx = Mathf.scl(camera.position.x, tilesize);
		int camy = Mathf.scl(camera.position.y, tilesize);

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
		
		int padding = 6;
		//view range x/y (block only)
		int vrx = Mathf.scl2(camera.viewportWidth * camera.zoom, tilesize)+padding;
		int vry = Mathf.scl2(camera.viewportHeight * camera.zoom, tilesize)+padding;
		
		//change renderable list size on screen resize/startup
		if(renderables == null || renderables.length != vrx || renderables[0].length != vry){

			if(renderables != null){
				for(int rx = 0; rx < renderables.length; rx++){
					for(int ry = 0; ry < renderables[0].length; ry++){
						renderables[rx][ry].free();
					}
				}
			}

			renderables = new RenderableList[vrx][vry];
			
			//invalidate cam position
			lastcamx = -1;
			lastcamy = -1;

			for(int rx = 0; rx < vrx; rx++){
				for(int ry = 0; ry < vry; ry++){
					renderables[rx][ry] = new RenderableList();
				}
			}
		}
		
		
		//if the camera moved, re-render everything
		if(lastcamx != camx || lastcamy != camy){

			for(int x = 0; x < vrx; x++){
				for(int y = 0; y < vry; y++){
					int worldx = x - vrx / 2 + camx;
					int worldy = y - vry / 2 + camy;

					renderables[x][y].free();

					Tile tile = World.get(worldx, worldy);

					if(tile == null)
						continue;

					if(tile.wall != Blocks.air){
						tile.wall.type.draw(renderables[x][y], tile, tile.wall);
					}
				}
			}

			lastcamx = camx;
			lastcamy = camy;
		}
	}
	
	public static void updateWall(int x, int y){
		//TODO optimization
		updateWalls();
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
				if(tile.floor != Blocks.air && World.get(tilex, tiley-1) != null && World.get(tilex, tiley-1).floor == Blocks.air){
					renderBottom(tile);
				}
			}
		}
		
		Caches.color();
		
		for(int tilex = x * chunksize; tilex < (x + 1) * chunksize; tilex++){
			for(int tiley = y * chunksize; tiley < (y + 1) * chunksize; tiley++){
				World.get(tilex, tiley).floor.type.drawCache(World.get(tilex, tiley), World.get(tilex, tiley).floor);
			}
		}
		caches[x][y] = Caches.end();
	}
}
