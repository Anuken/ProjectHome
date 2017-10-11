package io.anuke.home.world.blocks;

import com.badlogic.gdx.graphics.Color;

import io.anuke.home.Vars;
import io.anuke.home.world.Block;
import io.anuke.home.world.BlockType;
import io.anuke.home.world.Tile;
import io.anuke.home.world.blocks.BlockTypes.*;
import io.anuke.home.world.blocks.BlockTypes.LiveSpellCircle.SpellShape;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.ecs.Basis;
import io.anuke.ucore.facet.*;
import io.anuke.ucore.graphics.Hue;
import io.anuke.ucore.util.Angles;

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
		blendWith = block->block.name.contains("brick") && block instanceof Wall;
	}},
	tallbrickwall = new Wall("tallbrickwall"){{
		height = 19;
		edge = "brickwall";
		blendWith = block->block.name.contains("brick") && block instanceof Wall;
	}},
	brickshelf = new Wall("brickshelf"){{
		height = 13;
		variants = 3;
		vary = true;
		edge = "brickwall";
		blendWith = block->block.name.contains("brick") && block instanceof Wall;
	}},
	stonefloor = new Floor("stonefloor"){{
		variants = 5;
	}},
	cavefloor = new Floor("cavefloor"){{
		variants = 1;
	}},
	bottles = new Overlay("bottles"){
		Color color = new Color(0.9f, 0.9f, 0.9f, 0.86f);
		{
			vary = true;
			variants = 5;
			shadow = false;
			spread = true;
		}
		
		@Override
		public void draw(FacetList list, Tile tile){
			String name = this.name + (vary ? tile.rand(variants) : "");
			new SpriteFacet(name).set(tile.worldx(), tile.worldy()-offset)
			.color(color)
			.layer(tile.worldy())
			.center().sort(Sorter.object).add(list);
			
			new SpriteFacet(name + "shadow").set(tile.worldx(), tile.worldy()-offset)
				.layer(tile.worldy()+5)
				.center().shadow().add(list);
		}
	},
	barrel = new Prop("barrel"){{
		offset = 3;
		spread = true;
	}},
	brokenbarrel = new Prop("brokenbarrel"){{
		offset = 3;
		spread = true;
	}},
	torchlit = new Torch("torchstandlit", "torchstand", true){
		{
			offset = 1;
			height -= 1f;
			lightradius = 100f;
		}

		@Override
		public void loadTransition(){
			transition = torchunlit;
		}
	},
	torchunlit = new Torch("torchstand", "torchstand", false){
		{
			offset = 1;
			height -= 1f;
		}

		@Override
		public void loadTransition(){
			transition = torchlit;
		}
	},
	books = new Overlay("books"){
		Color[] colors = {Color.valueOf("4c5f3e"), Color.valueOf("7b6844"), Color.valueOf("445e6d"), Color.valueOf("704533"), Color.valueOf("8f875f")};
		Color temp = new Color();
		int w = 3, h = 4;
		
		@Override
		public void draw(FacetList list, Tile tile){
			new BaseFacet(tile.worldy()+5, p->{
				
				int amount = tile.rand(-1, colors.length);
				
				for(int i = 0; i < amount; i ++){
					
					int dx = tile.rand(i*2 + 0, Vars.tilesize);
					int dy = tile.rand(i*2 + 1, Vars.tilesize);
					int rot = tile.rand(i*2 + 2, 360);
					float mul = 1f + (tile.rand(i*2 + 3, 255)/255f-0.5f)/6f;
					
					drawBook(w, h, 
							tile.worldx() + dx-Vars.tilesize/2 /*- 0.5f + Core.camera.position.x % 1f*/, 
							tile.worldy() + dy - Vars.tilesize/2 /*- 0.5f + Core.camera.position.y % 1f*/,
							rot, colors[i], mul);
				}
			}).add(list);
		}
	},
	candles = new Candle("candles", false){

		@Override
		public void loadTransition(){
			transition = litcandles;
		}
	},
	litcandles = new Candle("litcandles", true){

		@Override
		public void loadTransition(){
			transition = candles;
		}
	},
	rocks = new Overlay("rocks"),
	bigrock = new Overlay("bigrocks"){{
		vary = false;
		solid = true;
		hitbox.setSize(10, 10);
		destructible = true;
		destoyDamage = 12;
		destroyBlock = Blocks.rocks;
		destroyParticle = "rockbreak";
		hitParticle = "rockspark";
	}},
	rubble = new Overlay("rubble"){{
		vary = false;
		offset = -1;
	}},
	redcarpet = new Floor("redcarpet"){{
		useEdge = false;
		vary = false;
	}},
	tatteredredcarpet = new DecalFloor("tatteredredcarpet"){{
		vary = true;
		variants = 3;
		under = stonefloor;
	}},
	tatteredredcarpetedge = new DecalFloor("tatteredredcarpetedge"){{
		variants = 3;
		under = stonefloor;
	}},
	tatteredredcarpetedge2 = new DecalFloor("tatteredredcarpetedge2"){{
		variants = 3;
		under = stonefloor;
	}},
	tatteredredcarpetmid = new DecalFloor("tatteredredcarpetmid"){{
		under = stonefloor;
	}},
	tatteredredcarpetl = new DecalFloor("tatteredredcarpetl"){{
		under = stonefloor;
	}},
	tatteredredcarpetr = new DecalFloor("tatteredredcarpetr"){{
		under = stonefloor;
	}},
	tatteredredcarpetl2 = new DecalFloor("tatteredredcarpetl2"){{
		under = stonefloor;
	}},
	tatteredredcarpetr2 = new DecalFloor("tatteredredcarpetr2"){{
		under = stonefloor;
	}},
	redcarpettriml = new Decal("redcarpettriml"){{
		variants = 2;
		vary = true;
	}},
	redcarpettrimr = new Decal("redcarpettrimr"){{
		variants = 2;
		vary = true;
	}},
	table = new Prop("table"){{
		offset = 3;
	}},
	booktable = new Prop("booktable"){
		Color[] colors = {Color.valueOf("4c5f3e"), Color.valueOf("7b6844"), Color.valueOf("445e6d"), Color.valueOf("704533"), Color.valueOf("8f875f")};
		
		{
			offset = 3;
		}
		
		@Override
		public void draw(FacetList list, Tile tile){
			table.draw(list, tile);
			new BaseFacet(tile.worldy() - offset, p->{
				
				int amount = tile.rand(-1, 3);
				
				for(int i = 0; i < amount; i ++){
					
					int index = tile.rand(-2 - i*2, colors.length) - 1;
					int dx = tile.rand(i*2 + 0, 14);
					int dy = tile.rand(i*2 + 2, 4);
					int rot = tile.rand(i*2 + 4, 360);
					float mul = 1f + (tile.rand(i*2 + 8, 255)/255f-0.5f)/5f;
					
					drawBook(3, 4, 
							tile.worldx() + dx-7, 
							tile.worldy() + dy + 7 - 2 - offset,
							rot, colors[index], mul);
				}
			}).add(list);
		}
	},
	smallshelf = new Bookshelf("smallshelf"){{
		blendWith = block->block == this;
		height = 13;
		edge = "shelf";
	}},
	tallshelf = new Bookshelf("tallshelf"){{
		blendWith = block->block == this;
		height = 19;
		shelves = 3;
		hitbox.height = 25;
		hitbox.y += 3.5f;
		edge = "shelf";
	}},
	verytallshelf = new Bookshelf("verytallshelf"){{
		blendWith = block->block == this;
		height = 25;
		shelves = 4;
		edge = "shelf";
	}},
	cobweb = new WallOverlay("cobweb"){{
		vary = false;
	}},
	
	moss = new Moss("moss"){
		{
			color = Color.valueOf("515c14");
		}
	},
	thickmoss = new Moss("thickmoss"){
		{
			color = Color.valueOf("5f6639");
		}
	},
	blackweb = new Moss("blackweb"){
		{
			color = Color.valueOf("414141");
		}
	},
	blackink = new Moss("blackink"){
		{
			color = Color.valueOf("414141");
		}
	},
	wallmoss = new WallOverlay("wallmoss"){
		{
			color = Color.valueOf("66741b");
		}
	},
	spawncircle = new SpellCircle("spawncircle"){
		
		@Override
		public void draw(Tile tile, float x, float y){
			
			Draw.color(Color.DARK_GRAY);
			Draw.thick(2f);
			
			Draw.rect("spawncircle", x, y);
			
			Angles.circleVectors(16, 40, (ox, oy)->{
				Draw.rect(randRune((int)(ox+oy*100), tile), (int)(x + ox), (int)(y + oy));
			});
			
			//Draw.rect(randGlyph(23, tile), (int)x, (int)y);
			//Geometry.circleVectors(8, 15, (ox, oy)->{
			//	Draw.rect(randGlyph((int)(oy*100), tile), (int)(x + ox), (int)(y + oy));
			//});
			
			Draw.reset();
		}
	},
	
	lightspawncircle = new LiveSpellCircle("lightspawncircle",
			new SpellShape(25, 26f, 0f, 2f),
			new SpellShape(5, 25f, 0f, 1f),
			new SpellShape(5, 25f, 180f/5, 1f),
			new SpellShape(true, 10, 13f)
			){
		
	},
	
	end = null
	;
	
	static Color temp = new Color();
	
	static void drawBook(int w, int h, float x, float y, float rot, Color color, float mul){
		
		temp.set(color).mul(mul, mul, mul, 1f);
		
		Draw.color(0f, 0f, 0f, 0.16f);
		Draw.rect("blank", x, y - 2f, w, h, rot);
		
		Draw.color(temp.mul(0.8f, 0.8f, 0.8f, 1f));
		Draw.rect("blank", x, y - 0.7f, w, h, rot);
		
		//Draw.color(Hue.lightness(0.7f));
		//Draw.rect("blank", x, y - 1f, w, h, rot);
		
		Draw.color(temp.set(color).mul(mul, mul, mul, 1f));
		Draw.rect("blank", x, y, w, h, rot);
		
		Draw.color();
	}
}
