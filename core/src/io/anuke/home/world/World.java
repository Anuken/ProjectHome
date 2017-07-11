package io.anuke.home.world;

import io.anuke.home.Vars;
import io.anuke.ucore.util.Mathf;

public class World{
	private static Tile[][] tiles;

	public static void create(){
		tiles = new Tile[Vars.worldsize][Vars.worldsize];
		
		for(int x = 0; x < Vars.worldsize; x ++){
			for(int y = 0; y < Vars.worldsize; y ++){
				tiles[x][y] = new Tile(x, y);
			}
		}
	}
	
	/** Returns null when out of bounds. Take care. */
	public static Tile get(int x, int y){
		if(!Mathf.inBounds(x, y, tiles)){
			return null;
		}

		return tiles[x][y];
	}

}
