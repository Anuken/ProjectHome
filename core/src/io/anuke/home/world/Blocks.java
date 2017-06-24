package io.anuke.home.world;

import io.anuke.ucore.graphics.Hue;

public class Blocks{
	public static final Block
	
	air = new Block("air", BlockType.empty),
	sky = new Block("air", BlockType.empty){{
		overrideSolid = true;
		solid = true;
	}},
	
	grass = new Block("grass", BlockType.tile){{
		vary = false;
	}},
	pgrass = new Block("pgrass", BlockType.tile){{
		edgecolor = Hue.rgb(0x63569bff, 0.65f);
	}}, 
	pgrassdk = new Block("pgrassdk", BlockType.tile){{
		edgecolor = Hue.rgb(0x5a4e8cff, 0.65f);
	}}, 
	psoil = new Block("psoil", BlockType.tile){{
		edgecolor = Hue.rgb(0x5a4e8cff, 0.6f);
	}}, 
	marble = new Block("marble", BlockType.tile){{
		variants = 4;
		edgecolor = Hue.rgb(0x7f788cff, 0.7f);
	}}, 
	marbles = new Block("marbles", BlockType.tile){{
		vary = false;
	}}, 
	marbles2 = new Block("marbles2", BlockType.tile){{
		vary = false;
	}}, 
	blocks = new Block("blocks", BlockType.overlay),
	checkpoint = new Block("respawnpoint", BlockType.checkpoint),
	redrock = new Block("redrock", BlockType.tile), 
	stonewall = new Block("stonewall", BlockType.wall),
	pwall = new Block("pwall", BlockType.wall),
	pwall2 = new Block("pwall2", BlockType.wall),
	pwall3 = new Block("pwall3", BlockType.wall),
	pwall4 = new Block("pwall4", BlockType.wall),
	marblepillar = new Block("marblepillar", BlockType.wall),
	marbleblock = new Block("marbleblock", BlockType.wall),
	marker = new Block("marker", BlockType.object){{
		offset = 1;
	}},
	spawner = new Block("spawner", BlockType.spawner),
	bluetree = new Block("bluetree", BlockType.tree){{
		offset = 4;
		variants = 2;
	}},
	bluesapling = new Block("bluesapling", BlockType.tree){{
		offset = 1;
		variants = 3;
		overrideSolid = true;
	}};;
}
