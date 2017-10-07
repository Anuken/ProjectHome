package io.anuke.home.world;

import io.anuke.home.Vars;
import io.anuke.home.world.blocks.Blocks;
import io.anuke.ucore.util.Bits;
import io.anuke.ucore.util.Mathf;

//TODO make coords shorts?
public class Tile{
	public Block floor, wall, decal;
	public short x, y;
	public short data1 = -1, data2 = -1;
	public float data3 = 0f;
	public Object data4 = null;
	
	public Tile(int x, int y){
		floor = wall = decal = Blocks.air;
		this.x = (short)x;
		this.y = (short)y;
	}
	
	public Tile(int x, int y, Block floor, Block wall){
		this(x,y);
		this.floor = floor;
		this.wall = wall;
	}
	
	public boolean passable(){
		return !wall.solid;
	}
	
	public float randFloat(int offset){
		return rand(offset, 200)/200f;
	}
	
	public int rand(int max){
		return rand(0, max);
	}
	
	public int rand(int offset, int max){
		return Mathf.randomSeed(offset + Bits.packLong(x, y), 1, max);
	}
	
	public int worldx(){
		return x*Vars.tilesize;
	}
	
	public int worldy(){
		return y*Vars.tilesize;
	}
	
	public void setBlock(Block selected){
		if(selected.type == BlockType.floor)
			floor = selected;
		else if(selected.type == BlockType.decal)
			decal = selected;
		else
			wall = selected;
	}
	
	@Override
	public String toString(){
		return "tile{"+floor.name + ", " + wall.name + ", " + data1 + ":" + data2 +"}";
	}

}
