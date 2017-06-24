package io.anuke.home.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import io.anuke.home.Vars;
import io.anuke.home.entities.Door;
import io.anuke.home.entities.Enemy;
import io.anuke.home.entities.enemies.*;
import io.anuke.ucore.entities.Entity;
import io.anuke.ucore.noise.Noise;
import io.anuke.ucore.util.Mathf;

public class World{
	private static Tile[][] tiles;
	private static ObjectMap<Integer, Block> map = new ObjectMap<>();
	private static ObjectMap<Integer, String> structmap = new ObjectMap<>();
	private static ObjectMap<String, Pixmap> pixmaps = new ObjectMap<>();
	private static Array<Door> doors = new Array<Door>();
	private static Pixmap pixmap;

	public static void create(){
		tiles = new Tile[Vars.worldsize][Vars.worldsize];

		map.put(0x4bc339ff, Blocks.grass);
		map.put(0x000000ff, Blocks.air);
		map.put(0x00000000, Blocks.air);
		map.put(0x7163afff, Blocks.pgrass);
		map.put(0xd9d6deff, Blocks.marbleblock);
		map.put(0xb0aeb3ff, Blocks.marble);
		map.put(0xf0edf4ff, Blocks.marblepillar);
		map.put(0x9a979dff, Blocks.marbles);
		map.put(0x89878cff, Blocks.marbles2);
		map.put(0xffef80ff, Blocks.checkpoint);
		map.put(0xa297b1ff, Blocks.pwall4);
		
		structmap.put(0xa6b3d4ff, "temple");
		structmap.put(0xcad2e7ff, "dungeon");
		//map.put(0xaf6261ff, Blocks.redrock);

		generate();
	}
	
	public static void addDoors(){
		for(Entity e : doors)
			e.add();
	}

	/** Returns null when out of bounds. Take care. */
	public static Tile get(int x, int y){
		if(!Mathf.inBounds(x, y, tiles)){
			return null;
		}

		return tiles[x][y];
	}

	public static void placeWall(int x, int y, Block block){
		Tile tile = get(x, y);
		if(tile != null){
			tile.wall = block;
		}
	}

	public static void placeFloor(int x, int y, Block block){
		Tile tile = get(x, y);
		if(tile != null){
			tile.floor = block;
		}
	}
	
	public static void placeSquare(int x, int y, int size, Block block){
		for(int dx = 0; dx <= size; dx++){
			for(int dy = 0; dy <= size; dy++){
				placeFloor(dx - size / 2 + x, dy - size / 2 + y, block);
				placeWall(dx - size / 2 + x, dy - size / 2 + y, Blocks.air);
			}
		}
	}
	
	public static void placeSpawner(int x, int y, Enemy enemy){
		Tile tile = get(x, y);
		if(tile != null){
			tile.wall = Blocks.spawner;
			tile.data = enemy;
		}
	}

	public static void placeRad(int x, int y, int rad, Block block){
		placeWall(x - rad, y - rad, block);
		placeWall(x - rad, y + rad, block);
		placeWall(x + rad, y - rad, block);
		placeWall(x + rad, y + rad, block);
	}
	
	public static Pixmap loadMap(String name){
		if(pixmaps.containsKey(name)){
			return pixmaps.get(name);
		}else{
			Pixmap pix = new Pixmap(Gdx.files.internal("maps/"+name + ".png"));
			pixmaps.put(name, pix);
			return pix;
		}
	}
	
	public static void placePix(String name, int cx, int cy){
		Pixmap pix = loadMap(name);
		Pixmap floor = loadMap(name + "-floor");
		
		for(int x = 0; x < pix.getWidth(); x ++){
			for(int y = 0; y < pix.getHeight(); y ++){
				int color = pix.getPixel(x, pix.getHeight()-1-y);
				int fcolor = floor.getPixel(x, pix.getHeight()-1-y);
				
				int worldx = cx+x-pix.getWidth()/2, worldy = cy+y-pix.getHeight()/2;
				
				if(map.containsKey(color)){
					get(worldx, worldy).wall = map.get(color);
				}
				
				if(map.get(fcolor, Blocks.air) != Blocks.air){
					get(worldx, worldy).floor = map.get(fcolor);
				}
				
				if(color == 0xff00ffff){
					doors.add(new Door(true, worldx, worldy));
				}
				
				if(color == 0x00ff00ff){
					doors.add(new Door(false, worldx, worldy));
				}
				
				if(color == 0xff99feff){
					Door door = new Door(true, worldx, worldy);
					door.width = 8;
					door.height = 2;
					door.areaw = door.areah = 39;
					doors.add(door);
				}
				
				if(color == 0x99ffaaff){
					Door door = new Door(false, worldx, worldy);
					door.width = 8;
					door.height = 4;
					door.areaw = door.areah = 39;
					doors.add(door);
				}
				
				if(color == 0x00ffffff){
					Door a = new Door(true, worldx, worldy+1);
					Door b = new Door(false, worldx, worldy-1);
					Door.link(a, b);
					doors.add(a);
					doors.add(b);
				}
				
				if(color == 0xffdadcff){
					placeSpawner(worldx, worldy, new MarbleGolem());
				}else if(color == 0xff874cff){
					placeSpawner(worldx, worldy, new Boss());
				}else if(color == 0xae5b5fff){
					placeSpawner(worldx, worldy, new MarbleDrone());
				}else if(color == 0xf64e56ff){
					placeSpawner(worldx, worldy, new MarbleObelisk());
				}
			}
		}
	}

	private static void generate(){
		Noise.setSeed(Mathf.random(0, 999999));
		
		if(pixmap == null)
			pixmap = loadMap("map-debug");

		for(int x = 0; x < Vars.worldsize; x++){
			for(int y = 0; y < Vars.worldsize; y++){
				Tile tile = new Tile(x, y, Blocks.grass, Blocks.air);

				int color = pixmap.getPixel(x, pixmap.getHeight() - y);

				tile.floor = map.get(color, Blocks.grass);

				if(tile.floor == Blocks.pgrass){

					if(Noise.nnoise(x, y, 10, 12) > 1){
						tile.floor = Blocks.pgrassdk;
					}

					if(Mathf.chance(0.005))
						tile.wall = Blocks.bluetree;

					if(Mathf.chance(0.005))
						tile.wall = Blocks.bluesapling;

					if(Mathf.chance(0.01))
						tile.wall = Blocks.blocks;

					if(Noise.nnoise(x, y, 10, 5) > 1)
						tile.wall = Blocks.pwall;
					if(Noise.nnoise(x, y, 10, 5) > 1.1)
						tile.wall = Blocks.pwall2;
					if(Noise.nnoise(x, y, 10, 5) > 1.2)
						tile.wall = Blocks.pwall3;
					if(Noise.nnoise(x, y, 10, 5) > 1.35)
						tile.wall = Blocks.pwall4;

					if(tile.wall == Blocks.air && Mathf.chance(0.0017)){
						tile.wall = Blocks.spawner;
						tile.data = Mathf.choose(new Tentapod(), new Tentawarrior(), new Tentafly());
					}
				}
				
				if(tile.floor == Blocks.checkpoint){
					Vars.control.addCheckpoint(tile);
					tile.wall = Blocks.checkpoint;
					tile.floor = Blocks.marble;
				}

				tiles[x][y] = tile;
			}
		}

		genStructures();
		
		for(int x = 0; x < Vars.worldsize; x++){
			for(int y = 0; y < Vars.worldsize; y++){
				Tile tile = tiles[x][y];
				if(tile.floor == Blocks.air){
					tile.wall = Blocks.sky;
				}
			}
		}
	}

	private static void genStructures(){
		for(int x = 0; x < Vars.worldsize; x++){
			for(int y = 0; y < Vars.worldsize; y++){
				int color = pixmap.getPixel(x, pixmap.getHeight() - 1 - y);
				Tile tile = World.get(x, y);

				//purpley place
				if(color == 0x7163afff){

					if(tile.wall == Blocks.air){
						if(Mathf.chance(0.0003)){
							int size = 6;
							for(int dx = 0; dx <= size; dx++){
								for(int dy = 0; dy <= size; dy++){
									placeFloor(dx - size / 2 + x, dy - size / 2 + y, Vector2.dst(dx, dy, size / 2, size / 2) < 2.5f ? Blocks.marbles : Blocks.marble);
								}
							}

							placeRad(x, y, size / 2 - 1, Blocks.pwall4);
							//placeRad(x, y, size/2-2, Blocks.marker);

							tile.wall = Blocks.spawner;
							tile.data = new MarbleGolem();
						}else if(Mathf.chance(0.0004)){
							placeSquare(x, y+1, 2, Blocks.marble);
							placeFloor(x, y+1, Blocks.marbles);
							placeWall(x, y+1, Blocks.marker);
							
							tile.data = new MarbleDrone();
							tile.wall = Blocks.spawner;
							
						}
					}
				}
			}
		}
		
		for(int x = 0; x < Vars.worldsize; x++){
			for(int y = 0; y < Vars.worldsize; y++){
				int color = pixmap.getPixel(x, pixmap.getHeight() - 1 - y);
				
				if(structmap.containsKey(color)){
					placePix(structmap.get(color), x, y);
				}
			}
		}
		
		//placePix("temple", Vars.worldsize/2, Vars.worldsize/2);
	}
}
