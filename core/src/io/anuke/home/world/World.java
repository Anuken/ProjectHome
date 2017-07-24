package io.anuke.home.world;

import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.util.Mathf;

public class World{
	private static Tile[][] tiles;
	private static int width = 1024, height = 1024;

	public static void create(){
		tiles = new Tile[width][height];
		
		for(int x = 0; x < width; x ++){
			for(int y = 0; y < height; y ++){
				tiles[x][y] = new Tile(x, y);
			}
		}
	}
	
	public static int width(){
		return width;
	}
	
	public static int height(){
		return height;
	}
	
	/** Returns null when out of bounds. Take care. */
	public static Tile get(int x, int y){
		if(!Mathf.inBounds(x, y, tiles)){
			return null;
		}

		return tiles[x][y];
	}
	
	public static void set(int x, int y, Tile tile){
		tile.x = x;
		tile.y = y;
		tiles[x][y] = tile;
	}
	
	public static void placeSpawner(int x, int y, Prototype enemy){
		Tile tile = get(x, y);
		if(tile != null){
			tile.wall = Blocks.spawner;
			tile.data1 = (short)enemy.getTypeID();
		}
	}
}
