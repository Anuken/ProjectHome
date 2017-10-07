package io.anuke.home.world;

import com.badlogic.gdx.Gdx;

import io.anuke.home.Vars;
import io.anuke.home.world.blocks.BlockTypes.Wall;
import io.anuke.home.world.blocks.Blocks;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.util.Mathf;

public class World{
	private static Tile[][] tiles;
	private static int width = 1024, height = 1024;
	private static int startx, starty;
	private static WorldData data = new WorldData();

	public static void create(){
		tiles = new Tile[width][height];

		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
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
	
	public static WorldData data(){
		return data;
	}

	public static void resize(int nwidth, int nheight, int offsetx, int offsety){
		Tile[][] newtiles = new Tile[nwidth][nheight];

		for(int x = 0; x < nwidth; x++){
			for(int y = 0; y < nheight; y++){
				Tile tile = null;

				int targetx = x + offsetx, targety = y + offsety;

				if(!Mathf.inBounds(targetx, targety, tiles)){
					tile = new Tile(x, y);
				}else{
					tile = tiles[targetx][targety];
					tile.wall.cleanup(tile);
					tile.x = (short)x;
					tile.y = (short)y;
				}

				newtiles[x][y] = tile;
			}
		}

		tiles = newtiles;
		width = nwidth;
		height = nheight;
	}
	
	public static int getStartX(){
		return startx;
	}
	
	public static int getStartY(){
		return starty;
	}

	public static void loadMap(String name){
		try{
			String filename = "maps/" + name + (name.endsWith(".hsv") ? "" : ".hsv");
			MapIO.load(Gdx.files.internal(filename));
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	public static void setTiles(Tile[][] tiles){
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				World.tiles[x][y].wall.cleanup(World.tiles[x][y]);
			}
		}
		width = tiles.length;
		height = tiles[0].length;
		
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				if(tiles[x][y].wall == Blocks.startcheckpoint){
					startx = x;
					starty = y;
				}
			}
		}

		World.tiles = tiles;
	}

	public static Tile[][] getTiles(){
		return tiles;
	}

	/** Returns null when out of bounds. Take care. */
	public static Tile get(int x, int y){
		if(!Mathf.inBounds(x, y, tiles)){
			return null;
		}

		return tiles[x][y];
	}
	
	public static Tile getWorld(float x, float y){
		return get(Mathf.scl2(x, Vars.tilesize), Mathf.scl2(y, Vars.tilesize));
	}

	public static void set(int x, int y, Tile tile){
		tile.x = (short)x;
		tile.y = (short)y;
		tiles[x][y] = tile;
	}
	
	public static boolean isWall(int x, int y){
		Tile tile = get(x, y);
		return tile != null && tile.wall instanceof Wall;
	}
	
	public static boolean solid(int x, int y){
		Tile tile = get(x, y);
		return tile == null || !tile.passable();
	}

	public static void placeSpawner(int x, int y, Prototype enemy){
		Tile tile = get(x, y);
		if(tile != null){
			tile.wall = Blocks.spawner;
			tile.data1 = (short) enemy.getTypeID();
		}
	}
}
