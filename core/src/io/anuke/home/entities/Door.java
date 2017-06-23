package io.anuke.home.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import io.anuke.home.Renderer;
import io.anuke.home.Vars;
import io.anuke.home.world.Block;
import io.anuke.home.world.Blocks;
import io.anuke.home.world.World;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.entities.Entities;
import io.anuke.ucore.entities.Entity;
import io.anuke.ucore.entities.SolidEntity;
import io.anuke.ucore.util.Timers;

public class Door extends Entity{
	public static final int width = 4, height = 3;
	public static final Block block = Blocks.marblepillar;
	public static final int areaw = 53, areah = 19;
	
	private static final Rectangle rect = new Rectangle(0, 0, areaw*Vars.tilesize, areah*Vars.tilesize);
	
	boolean front;
	boolean open;
	int tilex, tiley;
	Door linked;
	
	public static void link(Door a, Door b){
		a.linked = b;
		b.linked = a;
	}
	
	public Door(boolean front, int tilex, int tiley){
		this.tilex = tilex;
		this.tiley = tiley;
		this.front = front;
	}
	
	@Override
	public void update(){
		if(Timers.get(this, "wallupdate", 60)){
			float cx = (tilex-1.5f)*Vars.tilesize;
			if(front){
				rect.setPosition(cx - rect.getWidth()/2, tiley*Vars.tilesize);
			}else{
				rect.setPosition(cx - rect.getWidth()/2, tiley*Vars.tilesize - rect.getHeight());
			}
			Array<SolidEntity> entities = Entities.getNearby(rect);
			
			boolean hasplayer=false, hasenemy=false;
			
			for(SolidEntity entity : entities){
				if(!rect.contains(entity.x, entity.y)) continue;
				if(hasplayer && hasenemy) break;
				
				if(entity instanceof Player)
					hasplayer = true;
				
				if(entity instanceof Enemy)
					hasenemy = true;
			}
			
			open = hasplayer && hasenemy;
			
			if(open){
				showBlocks();
			}else if(linked == null || !linked.open){
				hideBlocks();
			}
		}
	}
	
	void showBlocks(){
		if(World.get(tilex, tiley).wall == block)
			return;
		
		place(block);
		
		Renderer.updateWalls();
	}
	
	void hideBlocks(){
		if(World.get(tilex, tiley).wall == Blocks.air)
			return;
		
		place(Blocks.air);
		
		Renderer.updateWalls();
	}
	
	void place(Block block){
		for(int x = 0; x < width; x ++){
			for(int y = 0; y < height; y ++){
				int worldx = tilex-x, worldy = front ? tiley-y : tiley + y;
				World.placeWall(worldx, worldy, block);
				Effects.effect("dust", (worldx)*Vars.tilesize, (worldy)*Vars.tilesize);
			}
		}
	}
}
