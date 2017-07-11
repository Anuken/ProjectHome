package io.anuke.home.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

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
	
	protected Block(String name, BlockType type){
		this.name = name;
		this.type = type;
		this.id = lastid++;
		this.edge = name;
		blocks.add(this);
	}
	
	public static Block byID(int id){
		return blocks.get(id);
	}
}
