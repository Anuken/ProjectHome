package io.anuke.home.world;

import java.io.*;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.compression.Lzma;

import io.anuke.home.world.blocks.Blocks;

/**
 * Loads and saves maps.
 * <h2>Format:</h2>
 * <p>
 * All the data is compressed using LZMA. Decompress before reading.
 * Saved block data:
 *     # of blocks (int)
 *     List of block name Strings, going by ID
 * Map data: 
 *     two ints, width and height, respectively.
 *     Everything after: a flattened 2D array, in row-major order, with tile data.
 *     Tile data format: wall (int), floor (int), data (int)
 * </p>
 * @author Anuke
 *
 */
public class MapIO{
	
	public static void load(FileHandle file) throws IOException, FileNotFoundException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Lzma.decompress(file.read(), out);
		
		DataInputStream stream = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
		
		ObjectMap<Integer, Block> blockmap = new ObjectMap<>();
		int blocks = stream.readInt();
		for(int i = 0; i < blocks; i ++){
			String name = stream.readUTF();
			blockmap.put(i, Block.byName(name));
		}
		
		int width = stream.readInt();
		int height = stream.readInt();
		
		Tile[][] tiles = new Tile[width][height];
		
		for(int x = 0; x < width; x ++){
			for(int y = 0; y < height; y ++){
				int wall = stream.readInt();
				int floor = stream.readInt();
				int decal = stream.readInt();
				short data1 = stream.readShort();
				short data2 = stream.readShort();
				tiles[x][y] = new Tile(x, y, blockmap.get(floor, Blocks.air), blockmap.get(wall, Blocks.air));
				tiles[x][y].decal = blockmap.get(decal, Blocks.air);
				tiles[x][y].data1 = data1;
				tiles[x][y].data2 = data2;
				tiles[x][y].wall.cleanup(tiles[x][y]);
			}
		}
		
		World.data().read(stream);
		
		stream.close();
		
		World.setTiles(tiles);
	}
	
	public static void save(Tile[][] tiles, FileHandle file) throws IOException, FileNotFoundException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(out);
		
		stream.writeInt(Block.getAllBlocks().size);
		for(Block block : Block.getAllBlocks()){
			stream.writeUTF(block.name);
		}
		
		int width = tiles.length;
		int height = tiles[0].length;
		
		stream.writeInt(width);
		stream.writeInt(height);
		
		for(int x = 0; x < width; x ++){
			for(int y = 0; y < height; y ++){
				Tile tile = tiles[x][y];
				stream.writeInt(tile.wall.id);
				stream.writeInt(tile.floor.id);
				stream.writeInt(tile.decal.id);
				stream.writeShort(tile.data1);
				stream.writeShort(tile.data2);
			}
		}
		
		World.data().write(stream);
		
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		
		Lzma.compress(in, file.write(false));
		
		stream.close();
	}
}
