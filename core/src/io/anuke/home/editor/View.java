package io.anuke.home.editor;

import static io.anuke.home.Vars.tilesize;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;

import io.anuke.home.world.Tile;
import io.anuke.home.world.World;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.util.Mathf;

public enum View{
	none, 
	hitboxes{
		public void draw(){
			OrthographicCamera camera = Core.camera;

			int camx = Mathf.scl(camera.position.x, tilesize);
			int camy = Mathf.scl(camera.position.y, tilesize);

			int rangex = Math.round(camera.viewportWidth * camera.zoom / (tilesize)) + 1;
			int rangey = Math.round(camera.viewportHeight * camera.zoom / (tilesize)) + 1;
			
			for(int x = -rangex; x <= rangex; x++){
				for(int y = rangey; y >= -rangey; y--){
					int worldx = camx + x;
					int worldy = camy + y;

					if(World.get(worldx, worldy) == null){
						continue;
					}

					Tile tile = World.get(worldx, worldy);
					
					if(tile.wall.solid){
						tile.wall.getHitbox(tile, Rectangle.tmp);
					}
					
					Draw.color(Color.GREEN);
					Draw.thick(1);
					Draw.linerect(Rectangle.tmp);
					Draw.reset();
				}
			}
		}
	};
	
	public void draw(){}
}
