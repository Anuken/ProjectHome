package io.anuke.home.world;

import static io.anuke.home.world.World.get;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import io.anuke.home.Vars;
import io.anuke.home.entities.Door;
import io.anuke.home.entities.Enemy;
import io.anuke.home.entities.PlayerDoor;
import io.anuke.home.entities.enemies.*;
import io.anuke.ucore.entities.Entity;
import io.anuke.ucore.noise.Noise;
import io.anuke.ucore.util.Mathf;

public class Generator{
	private static ObjectMap<Integer, Block> map = new ObjectMap<>();
	private static ObjectMap<Integer, String> structmap = new ObjectMap<>();
	private static ObjectMap<String, Pixmap> pixmaps = new ObjectMap<>();
	private static Array<Door> doors = new Array<Door>();
	private static Pixmap pixmap;
	
	static{
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
		map.put(0x5c4f93ff, Blocks.psoil);
		
		structmap.put(0xa6b3d4ff, "temple");
		structmap.put(0xcad2e7ff, "dungeon");
		structmap.put(0xd2b4ffff, "spawnpoint");
		structmap.put(0x9fffc5ff, "2spawnpoint");
		structmap.put(0x86deffff, "smalltemple");
		structmap.put(0x4ccfffff, "obeliskaltar");
		structmap.put(0x7b5cffff, "tentaboss");
		structmap.put(0xff86dbff, "golemtemple");
		structmap.put(0x4cff83ff, "golemaltar");
	}
	
	public static void addDoors(){
		for(Entity e : doors)
			e.add();
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
	
	public static void placeCircle(int x, int y, int size, Block block){
		for(int dx = 0; dx <= size; dx++){
			for(int dy = 0; dy <= size; dy++){
				if(Vector2.dst(dx, dy, size/2f, size/2f) > size/2f) continue;
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
				}else if(color == 0x00ff00ff){
					doors.add(new Door(false, worldx, worldy));
				}else if(color == 0xff99feff){
					Door door = new Door(true, worldx, worldy);
					door.width = 8;
					door.height = 2;
					door.areaw = door.areah = 39;
					doors.add(door);
				}else if(color == 0x99ffaaff){
					Door door = new Door(false, worldx, worldy);
					door.width = 8;
					door.height = 4;
					door.areaw = door.areah = 39;
					doors.add(door);
				}else if(color == 0x00ffffff){
					Door a = new Door(true, worldx, worldy+1);
					Door b = new Door(false, worldx, worldy-1);
					Door.link(a, b);
					doors.add(a);
					doors.add(b);
				}else if(color == 0xa1ff6eff){
					PlayerDoor door = new PlayerDoor(worldx, worldy);
					door.width = 5;
					doors.add(door);
				}else if(color == 0x6ef3ffff){
					PlayerDoor door = new PlayerDoor(worldx, worldy);
					door.front = false;
					door.width = 5;
					doors.add(door);
				}else if(color == 0xcaffadff){
					PlayerDoor door = new PlayerDoor(worldx, worldy);
					door.width = 4;
					doors.add(door);
				}
				
				if(color == 0xffdadcff){
					placeSpawner(worldx, worldy, new MarbleGolem());
				}else if(color == 0xff874cff){
					placeSpawner(worldx, worldy, new Boss());
				}else if(color == 0xae5b5fff){
					placeSpawner(worldx, worldy, new MarbleDrone());
				}else if(color == 0xf64e56ff){
					placeSpawner(worldx, worldy, new MarbleObelisk());
				}else if(color == 0xff51ccff){
					placeSpawner(worldx, worldy, new Tentacolumn());
				}
				
				if(name.equals("tentaboss")){
					placeFloor(worldx, worldy+1, Blocks.psoil);
				}
			}
		}
	}

	public static void generate(){
		doors.clear();
		Noise.setSeed(Mathf.random(0, 999999));
		
		if(pixmap == null)
			pixmap = loadMap("map");

		for(int x = 0; x < Vars.worldsize; x++){
			for(int y = 0; y < Vars.worldsize; y++){
				Block floor = Blocks.air;
				Block wall = Blocks.air;

				int color = pixmap.getPixel(x, pixmap.getHeight() - y);

				floor = map.get(color, Blocks.grass);

				if(floor == Blocks.pgrass){

					if(Noise.nnoise(x, y, 10, 12) > 1){
						floor = Blocks.pgrassdk;
					}

					if(Mathf.chance(0.005))
						wall = Blocks.bluetree;

					if(Mathf.chance(0.005))
						wall = Blocks.bluesapling;

					if(Mathf.chance(0.01))
						wall = Blocks.blocks;

					if(Noise.nnoise(x, y, 10, 5) > 1)
						wall = Blocks.pwall;
					if(Noise.nnoise(x, y, 10, 5) > 1.1)
						wall = Blocks.pwall2;
					if(Noise.nnoise(x, y, 10, 5) > 1.2)
						wall = Blocks.pwall3;
					if(Noise.nnoise(x, y, 10, 5) > 1.35)
						wall = Blocks.pwall4;

					if(wall == Blocks.air && Mathf.chance(0.0013)){
						wall = Blocks.spawner;
						get(x,y).data = Mathf.choose(new Tentapod(), new Tentawarrior(), new Tentafly());
					}
				}
				
				if(color == 0x8579b5ff){
					floor = Blocks.pgrass;
				}
				
				get(x, y).floor = floor;
				get(x, y).wall = wall;
			}
		}

		genStructures();
		
		for(int x = 0; x < Vars.worldsize; x++){
			for(int y = 0; y < Vars.worldsize; y++){
				Tile tile = get(x, y);
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
						if(Mathf.chance(0.0002)){
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
							
						}else if(Mathf.chance(0.0002)){
							placeCircle(x, y, 6, Blocks.pgrassdk);
							placeCircle(x, y, 4, Blocks.psoil);
							for(int i = 0; i < 6; i ++){
								int nx = x + Mathf.range(4);
								int ny = y + Mathf.range(4);
								placeSpawner(nx, ny, Mathf.choose(new Tentapod(), new Tentawarrior(), new Tentafly()));
							}
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