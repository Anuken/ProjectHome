package io.anuke.home.world;

import com.badlogic.gdx.graphics.Color;

import io.anuke.home.Vars;
import io.anuke.home.world.blocks.BlockTypes.*;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.ecs.Basis;
import io.anuke.ucore.graphics.Hue;
import io.anuke.ucore.renderables.FuncRenderable;
import io.anuke.ucore.renderables.RenderableList;

public class Blocks{
	public static final Block
	
	air = new Block("air", BlockType.floor){
		
	},
	
	grass = new Floor("grass"){{
		vary = false;
	}},
	pgrass = new Floor("pgrass"){{
		edgecolor = Hue.rgb(0x63569bff, 0.65f);
	}}, 
	pgrassdk = new Floor("pgrassdk"){{
		edgecolor = Hue.rgb(0x5a4e8cff, 0.65f);
	}}, 
	psoil = new Floor("psoil"){{
		edgecolor = Hue.rgb(0x5a4e8cff, 0.6f);
	}}, 
	marble = new Floor("marble"){{
		variants = 4;
		edgecolor = Hue.rgb(0x7f788cff, 0.7f);
	}}, 
	marbles = new Floor("marbles"){{
		vary = false;
	}}, 
	marbles2 = new Floor("marbles2"){{
		vary = false;
	}}, 
	blocks = new Overlay("blocks"),
	checkpoint = new Checkpoint("respawnpoint"),
	startcheckpoint = new Checkpoint("startpoint"){{
		sides = 4;
		darkColor = Color.valueOf("dc997e");
		lightColor = Color.valueOf("ffb294");
	}},
	spawner = new Spawner("spawner"),
	emptySpawner = new Block("emptyspawner", BlockType.wall){
		@Override
		public void cleanup(Tile tile){
			Basis.instance().removeSpark(tile.data2);
			tile.wall = Blocks.spawner;
		}
	},
	pwall = new Wall("pwall"),
	pwall2 = new Wall("pwall2"),
	pwall3 = new Wall("pwall3"),
	pwall4 = new Wall("pwall4"),
	
	marblepillar = new Wall("marblepillar"),
	marbleblock = new Wall("marbleblock"),
	marker = new Prop("marker"){{
		offset = 1;
	}},
	bluetree = new Tree("bluetree"){{
		offset = 4;
		variants = 2;
	}},
	bluesapling = new Tree("bluesapling"){{
		offset = 1;
		variants = 3;
	}},
	brickwall = new Wall("brickwall"){{
		height = 13;
	}},
	stonefloor = new Floor("stonefloor"){{
		variants = 5;
	}},
	bottles = new Overlay("bottles"){{
		vary = false;
	}},
	barrel = new Prop("barrel"){{
		offset = 3;
	}},
	brokenbarrel = new Prop("brokenbarrel"){{
		offset = 3;
	}},
	books = new Overlay("books"){
		Color[] colors = {Color.valueOf("4c5f3e"), Color.valueOf("7b6844"), Color.valueOf("445e6d"), Color.valueOf("704533"), Color.valueOf("8f875f")};
		Color temp = new Color();
		int w = 3, h = 4;
		
		@Override
		public void draw(RenderableList list, Tile tile){
			new FuncRenderable(p->{
				p.layer = tile.worldy()+24;
				
				int amount = tile.rand(-1, colors.length+1)-1;
				
				for(int i = 0; i < amount; i ++){
					
					int dx = tile.rand(i*2 + 0, Vars.tilesize);
					int dy = tile.rand(i*2 + 1, Vars.tilesize);
					int rot = tile.rand(i*2 + 2, 360);
					float mul = 1f + (tile.rand(i*2 + 3, 255)/255f-0.5f)/6f;
					
					temp.set(colors[i]).mul(mul, mul, mul, 1f);
					
					Draw.color(0f, 0f, 0f, 0.14f);
					Draw.rect("blank", tile.worldx() + dx-Vars.tilesize/2, tile.worldy() + dy - Vars.tilesize/2 - 1.8f, w, h, rot);
					
					Draw.color(temp.mul(0.8f, 0.8f, 0.8f, 1f));
					
					Draw.rect("blank", tile.worldx() + dx-Vars.tilesize/2, tile.worldy() + dy - Vars.tilesize/2 - 0.7f, w, h, rot);
					
					temp.set(colors[i]).mul(mul, mul, mul, 1f);
					
					Draw.color(temp);
					
					Draw.rect("blank", tile.worldx() + dx-Vars.tilesize/2, tile.worldy() + dy - Vars.tilesize/2, w, h, rot);
					
					Draw.color();
				}
			}).add(list);
		}
	},
	rocks = new Overlay("rocks"),
	table = new Prop("table"){{
		offset = 3;
	}},
	
	cobweb = new WallOverlay("cobweb"),
	
	end = null
	;
}
