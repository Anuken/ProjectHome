package io.anuke.home.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import io.anuke.home.Vars;
import io.anuke.home.world.blocks.Blocks;
import io.anuke.ucore.facet.FacetList;

public class Block{
	private static int lastid;
	private static Array<Block> blocks = new Array<>();
	private static ObjectMap<String, Block> blockmap = new ObjectMap<>();
	
	public final int id;
	public final String name;
	public final BlockType type;
	public String edge;
	public float offset = -3f;
	
	public boolean vary = true;
	public int variants = 3;
	
	public boolean solid = false;
	public Color edgecolor = Color.DARK_GRAY;
	
	public int height = 9;
	
	public Rectangle hitbox = new Rectangle(0, 0, Vars.tilesize, Vars.tilesize);
	
	public boolean destructible = false;
	public int destoyDamage = 4;
	public Block destroyBlock = Blocks.air;
	public String destroyParticle;
	public String hitParticle;
	
	protected Block(String name, BlockType type){
		this.name = name;
		this.id = lastid++;
		this.edge = name;
		this.type = type;
		blocks.add(this);
		
		if(blockmap.containsKey(name)){
			throw new RuntimeException("Duplicate block name: " + name + ". Block names must be unique!");
		}else{
			blockmap.put(name, this);
		}
	}
	
	public void cleanup(Tile tile){
		
	}
	
	public void getHitbox(Tile tile, Rectangle out){
		out.setSize(hitbox.width, hitbox.height).setCenter(hitbox.x + tile.worldx(), hitbox.y + tile.worldy());
	}
	
	public void draw(FacetList list, Tile tile){}
	
	public void drawCache(Tile tile){}
	
	public static Array<Block> getAllBlocks(){
		return blocks;
	}
	
	/**Returns AIR if the block name is not used.s*/
	public static Block byName(String name){
		return blockmap.get(name, Blocks.air);
	}
	
	/**Returns AIR if the block index is out of bounds.*/
	public static Block byID(int id){
		if(id >= blocks.size)
			return blocks.get(0);
		return blocks.get(id);
	}
}
