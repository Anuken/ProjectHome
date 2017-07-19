package io.anuke.home.world;

import io.anuke.home.Vars;
import io.anuke.ucore.util.Mathf;

public class Tile{
	public final int x, y;
	public Block floor, wall;
	public int data = -1;
	
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
	
	@Override
	public String toString(){
		return "tile{"+floor.name + ", " + wall.name + ", " + data +"}";
	}
}
