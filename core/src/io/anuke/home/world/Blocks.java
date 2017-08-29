package io.anuke.home.world;

import com.badlogic.gdx.graphics.Color;

import io.anuke.home.world.blocks.BlockTypes.*;
import io.anuke.ucore.ecs.Basis;
import io.anuke.ucore.graphics.Hue;

public class Blocks{
	public static final Block
	
	air = new Block("air", BlockType.floor){
		
	},
	sky = new Block("sky", BlockType.floor){{
		solid = true;
	}},
	
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
	redrock = new Floor("redrock"), 
	stonewall = new Wall("stonewall"),
	pwall = new Wall("pwall"),
	pwall2 = new Wall("pwall2"),
	pwall3 = new Wall("pwall3"),
	pwall4 = new Wall("pwall4"),
	marblepillar = new Wall("marblepillar"),
	marbleblock = new Wall("marbleblock"),
	marker = new Prop("marker"){{
		offset = 1;
	}},
	spawner = new Spawner("spawner"),
	emptySpawner = new Block("spawner", BlockType.wall){
		@Override
		public void cleanup(Tile tile){
			Basis.instance().removeSpark(tile.data2);
			tile.wall = Blocks.spawner;
		}
	},
	bluetree = new Tree("bluetree"){{
		offset = 4;
		variants = 2;
	}},
	bluesapling = new Tree("bluesapling"){{
		offset = 1;
		variants = 3;
	}},
	startcheckpoint = new Checkpoint("startpoint"){{
		sides = 4;
		darkColor = Color.valueOf("dc997e");
		lightColor = Color.valueOf("ffb294");
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
	rocks = new Overlay("rocks"),
	table = new Prop("table"){{
		offset = 3;
	}},
	
	cobweb = new WallOverlay("cobweb"),
	
	end = null
	;
}
