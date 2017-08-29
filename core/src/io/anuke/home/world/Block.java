package io.anuke.home.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import io.anuke.home.Vars;
import io.anuke.ucore.renderables.RenderableList;

public class Block{
	private static int lastid;
	private static Array<Block> blocks = new Array<>();
	
	public final int id;
	public final String name;
	public final BlockType type;
	public String edge;
	public float offset = -3f;
	
	public boolean vary = true;
	public int variants = 3;
	
	public boolean overrideSolid = false, solid = false;
	public Color edgecolor = Color.DARK_GRAY;
	
	public int height = 9;
	
	protected Block(String name, BlockType type){
		this.name = name;
		this.type = type;
		this.id = lastid++;
		this.edge = name;
		blocks.add(this);
	}
	
	public void cleanup(Tile tile){
		
	}
	
	public void getHitbox(Tile tile, Rectangle out){
		out.setSize(Vars.tilesize).setCenter(tile.worldx(), tile.worldy());
	}
	
	public void draw(RenderableList list, Tile tile){}
	
	public void drawCache(Tile tile){}
	
	public static Array<Block> getAllBlocks(){
		return blocks;
	}
	
	/**Returns AIR if the block index is out of bounds.*/
	public static Block byID(int id){
		if(id >= blocks.size)
			return blocks.get(0);
		return blocks.get(id);
	}
}
