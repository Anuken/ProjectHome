package io.anuke.home.entities.traits;

import com.badlogic.gdx.math.Vector2;

import io.anuke.home.Renderer;
import io.anuke.home.Vars;
import io.anuke.home.world.Tile;
import io.anuke.home.world.World;
import io.anuke.home.world.blocks.Lightable;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.Trait;

public class DarkenTrait extends Trait{
	public int darkenRad = 2;
	
	@Override
	public void update(Spark spark){
		
		for(int dx = -darkenRad; dx <= darkenRad; dx ++){
			for(int dy = -darkenRad; dy <= darkenRad; dy ++){
				if(Vector2.dst(dx, dy, 0, 0) < darkenRad){
					Tile tile = World.getWorld(spark.pos().x + dx * Vars.tilesize, spark.pos().y + dy * Vars.tilesize);
					
					if(tile != null && tile.wall instanceof Lightable){
						Lightable l = (Lightable)tile.wall;
						
						if(l.extinguish(tile)){
							Renderer.updateWall(tile.x, tile.y);
						}
					}
				}
			}
		}
		
	}
}
