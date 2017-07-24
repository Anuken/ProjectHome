package io.anuke.home.world;

import io.anuke.home.Vars;
import io.anuke.ucore.util.Mathf;

public class Tile{
	public int x, y;
	public Block floor, wall;
	public short data1 = -1, data2 = -1;
	
	public Tile(int x, int y){
		floor = wall = Blocks.air;
		this.x = x;
		this.y = y;
	}
	
	public Tile(int x, int y, Block floor, Block wall){
		this(x,y);
		this.floor = floor;
		this.wall = wall;
	}
	
	public int rand(int max){
		return Mathf.randomSeed(x*y, 1, max);
	}
	
	public int worldx(){
		return x*Vars.tilesize;
	}
	
	public int worldy(){
		return y*Vars.tilesize;
	}
	
	public void setBlock(Block selected){
		if(selected.type.floor)
			floor = selected;
		else
			wall = selected;
	}
	
	@Override
	public String toString(){
		return "tile{"+floor.name + ", " + wall.name + ", " + data1 + ":" + data2 +"}";
	}

}
