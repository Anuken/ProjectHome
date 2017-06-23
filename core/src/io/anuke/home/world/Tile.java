package io.anuke.home.world;

import io.anuke.home.Vars;

public class Tile{
	public Block floor, wall;
	public final int x, y;
	public Object data;
	
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
	
	public int worldx(){
		return x*Vars.tilesize;
	}
	
	public int worldy(){
		return y*Vars.tilesize;
	}
}
