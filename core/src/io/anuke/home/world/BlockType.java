package io.anuke.home.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import io.anuke.home.Vars;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.entities.Entity;
import io.anuke.ucore.graphics.Caches;
import io.anuke.ucore.renderables.*;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public enum BlockType{
	empty{
		public void drawCache(Tile tile, Block block){
			BlockType.tile.drawCache(tile, block);
		}
	},
	tile{
		private TextureRegion temp = new TextureRegion();
		
		public void drawCache(Tile tile, Block block){
			if(tile.wall.type == BlockType.wall) return;
			
			if(block != Blocks.air)
				Caches.draw(block.name + (block.vary ? Mathf.random(1, block.variants) : ""), tile.worldx(), tile.worldy());
			
			for(int dx = -1; dx <= 1; dx ++){
				for(int dy = -1; dy <= 1; dy ++){
					
					if(dx == 0 && dy == 0) continue;
					
					Tile other = World.get(tile.x+dx, tile.y+dy);
					
					if(other == null) continue;
					
					Block floor = other.floor;
					
					if(floor.id <= block.id || other.wall.type == BlockType.wall) continue;
					
					TextureRegion region = Draw.region(floor.name + "edge");
					
					if(region == null)
						region = Draw.region(floor.edge + "edge");
					
					int tsize = 12;
					
					int sx = -dx*tsize+2, sy = -dy*tsize+2;
					int x = Mathf.clamp(sx, 0, tsize+4);
					int y = Mathf.clamp(sy, 0, tsize+4);
					int w = Mathf.clamp(sx+tsize, 0, tsize+4)-x, h = Mathf.clamp(sy+tsize, 0, tsize+4)-y;
					
					float rx = Mathf.clamp(dx*tsize, 0, tsize-w);
					float ry = Mathf.clamp(dy*tsize, 0, tsize-h);
					
					temp.setTexture(region.getTexture());
					temp.setRegion(region.getRegionX()+x, region.getRegionY()+y+h, w, -h);
					
					Caches.draw(temp, tile.worldx()-tsize/2 + rx, tile.worldy()-tsize/2 + ry, w, h);
					
				}
			}
		}
	},
	wall(true){
		public void draw(RenderableList list, Tile tile, Block block){
			new SpriteRenderable(block.name).set(tile.worldx(), tile.worldy()-Vars.tilesize/2f)
			.centerX().addShadow(list, "wallshadow", 6).sort(Sorter.object).add(list);
		}
	},
	tree(true){
		public void draw(RenderableList list, Tile tile, Block block){
			new SpriteRenderable(block.name + (block.vary ? Mathf.randomSeed(tile.x*tile.y, 1, block.variants) : "")).set(tile.worldx(), tile.worldy()-block.offset)
			.layer(tile.worldy())
			.centerX().addShadow(list, block.offset).sort(Sorter.object).add(list);
		}
		
		public void getHitbox(Tile tile, Block block, Rectangle out){
			out.setSize(12, 2)
			.setCenter(tile.worldx(), tile.worldy());
		}
	},
	checkpoint{
		public void draw(RenderableList list, Tile tile, Block block){
			
			new SpriteRenderable("respawnpointedge").set(tile.worldx(), tile.worldy())
			.center().sort(Sorter.tile).add(list);
			
			new FuncRenderable(p->{
				Draw.color(new Color(0xd5bd7cff));
				Draw.polygon(3, tile.worldx(), tile.worldy() + Mathf.sin(Timers.time(), 16f, 2f)+10f, 4f, Timers.time()/1f);
				Draw.color(new Color(0xffeab2ff));
				Draw.polygon(3, tile.worldx(), tile.worldy() + Mathf.sin(Timers.time(), 16f, 2f)+11f, 4f, Timers.time()/1f);
				Draw.color();
				p.layer = tile.worldy() - 2;
			}).add(list);
		}
	},
	object(true){
		public void draw(RenderableList list, Tile tile, Block block){
			new SpriteRenderable(block.name).set(tile.worldx(), tile.worldy()-block.offset)
			.layer(tile.worldy())
			.centerX().addShadow(list, block.offset).sort(Sorter.object).add(list);
		}
		
		public void getHitbox(Tile tile, Block block, Rectangle out){
			out.setSize(12, 2)
			.setCenter(tile.worldx(), tile.worldy());
		}
	},
	overlay(false){
		public void draw(RenderableList list, Tile tile, Block block){
			String name = block.name + (block.vary ? Mathf.randomSeed(tile.x*tile.y, 1, block.variants) : "");
			new SpriteRenderable(name).set(tile.worldx(), tile.worldy()-block.offset)
			.layer(tile.worldy()+30)
			.centerX().addShadow(list, name,block.offset+8).sort(Sorter.object).add(list);
		}
		
		public void getHitbox(Tile tile, Block block, Rectangle out){
			out.setSize(12, 2)
			.setCenter(tile.worldx(), tile.worldy());
		}
	},
	spawner{
		public void draw(RenderableList list, Tile tile, Block block){
			if(tile.data == null || !(tile.data instanceof Entity)){
				throw new IllegalArgumentException("Spawner tile detected without any valid spawn data!");
			}
			
			Entity enemy = (Entity)tile.data;
			enemy.set(tile.worldx(), tile.worldy());
			enemy.add();
			tile.data = null;
			tile.wall = Blocks.air;
		}
	};
	private final boolean solid;
	
	private BlockType(){
		solid = false;
	}
	private BlockType(boolean solid){
		this.solid = solid;
	}
	
	public boolean solid(Block block){
		return solid ? (!block.overrideSolid || block.solid) : (block.overrideSolid && block.solid);
	}
	
	public void getHitbox(Tile tile, Block block, Rectangle out){
		out.setSize(Vars.tilesize).setCenter(tile.worldx(), tile.worldy());
	}
	
	public void draw(RenderableList list, Tile tile, Block block){}
	
	public void drawCache(Tile tile, Block block){}
}
