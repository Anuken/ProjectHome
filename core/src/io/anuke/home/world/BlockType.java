package io.anuke.home.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import io.anuke.home.Vars;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.ecs.Basis;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.graphics.Caches;
import io.anuke.ucore.renderables.*;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public enum BlockType{
	empty(false){
		public void drawCache(Tile tile, Block block){
			BlockType.tile.drawCache(tile, block);
		}
	},
	tile(true){
		private TextureRegion temp = new TextureRegion();
		
		public void drawCache(Tile tile, Block block){
			if(tile.wall.type == BlockType.wall) return;
			
			if(block != Blocks.air)
				Caches.draw(block.name + (block.vary ? tile.rand(block.variants) : ""), tile.worldx(), tile.worldy());
			
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
	wall(false, true){
		public void draw(RenderableList list, Tile tile, Block block){
			new SpriteRenderable(block.name).set(tile.worldx(), tile.worldy()-Vars.tilesize/2f)
			.centerX().addShadow(list, "wallshadow", 6).sort(Sorter.object).add(list);
		}
	},
	tree(false, true){
		public void draw(RenderableList list, Tile tile, Block block){
			new SpriteRenderable(block.name + (block.vary ? tile.rand(block.variants) : "")).set(tile.worldx(), tile.worldy()-block.offset)
			.layer(tile.worldy())
			.centerX().addShadow(list, block.offset).sort(Sorter.object).add(list);
		}
		
		public void getHitbox(Tile tile, Block block, Rectangle out){
			out.setSize(12, 2)
			.setCenter(tile.worldx(), tile.worldy());
		}
	},
	checkpoint(false){
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
	startcheckpoint(false){
		public void draw(RenderableList list, Tile tile, Block block){
			
			new SpriteRenderable("startpointedge").set(tile.worldx(), tile.worldy())
			.center().sort(Sorter.tile).add(list);
			
			new FuncRenderable(p->{
				Draw.color(Color.valueOf("dc997e"));
				Draw.polygon(4, tile.worldx(), tile.worldy() + Mathf.sin(Timers.time(), 16f, 2f)+10f, 4f, Timers.time()/1f);
				Draw.color(Color.valueOf("ffb294"));
				Draw.polygon(4, tile.worldx(), tile.worldy() + Mathf.sin(Timers.time(), 16f, 2f)+11f, 4f, Timers.time()/1f);
				Draw.color();
				p.layer = tile.worldy() - 2;
			}).add(list);
		}
	},
	object(false, true){
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
	overlay(false, false){
		public void draw(RenderableList list, Tile tile, Block block){
			String name = block.name + (block.vary ? tile.rand(block.variants) : "");
			new SpriteRenderable(name).set(tile.worldx(), tile.worldy()-block.offset)
			.layer(tile.worldy()+30)
			.center().addShadow(list, name,block.offset+8).sort(Sorter.object).add(list);
		}
		
		public void getHitbox(Tile tile, Block block, Rectangle out){
			out.setSize(12, 2)
			.setCenter(tile.worldx(), tile.worldy());
		}
	},
	spawner(false){
		//data1 = prototype ID
		//data2 = entity spawned ID
		public void draw(RenderableList list, Tile tile, Block block){
			if(tile.data1 == -1){
				throw new IllegalArgumentException("Spawner tile detected without any valid spawn data!");
			}
			
			Spark enemy = new Spark(Prototype.getAllTypes().get(tile.data1));
			enemy.pos().set(tile.worldx(), tile.worldy());
			enemy.add();
			tile.data2 = (short)enemy.getID();
			tile.wall = Blocks.emptySpawner;
		}
	},
	emptySpawner(false){
		public void cleanup(Tile tile){
			Basis.instance().removeSpark(tile.data2);
			tile.wall = Blocks.spawner;
		}
	};
	private final boolean solid;
	public final boolean floor;
	
	private BlockType(boolean floor){
		solid = false;
		this.floor = floor;
	}
	private BlockType(boolean floor, boolean solid){
		this.solid = solid;
		this.floor = floor;
	}
	
	public void cleanup(Tile tile){
		
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
