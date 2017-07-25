package io.anuke.home.world;

import java.io.*;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.compression.Lzma;

/**
 * Loads and saves maps.
 * <h2>Format:</h2>
 * <p>
 * All the data is compressed using LZMA. Decompress before reading.
 * Header: two ints, width and height, respectively.
 * Everything after: a flattened 2D array, in row-major order, with tile data.
 * Tile data format: wall (int), floor (int), data(int)
 * </p>
 * @author Anuke
 *
 */
public class MapIO{
	
	public static Tile[][] load(FileHandle file) throws IOException, FileNotFoundException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Lzma.decompress(file.type() == FileType.Internal ? MapIO.class.getResourceAsStream("/"+file.toString()) : 
			new FileInputStream(file.file()), out);
		
		DataInputStream stream = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
		
		int width = stream.readInt();
		int height = stream.readInt();
		
		Tile[][] tiles = new Tile[width][height];
		
		for(int x = 0; x < width; x ++){
			for(int y = 0; y < height; y ++){
				int wall = stream.readInt();
				int floor = stream.readInt();
				short data1 = stream.readShort();
				short data2 = stream.readShort();
				tiles[x][y] = new Tile(x, y, Block.byID(floor), Block.byID(wall));
				tiles[x][y].data1 = data1;
				tiles[x][y].data2 = data2;
				tiles[x][y].wall.type.cleanup(tiles[x][y]);
			}
		}
		
		stream.close();
		
		return tiles;
	}
	
	public static void save(Tile[][] tiles, FileHandle file) throws IOException, FileNotFoundException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(out);
		
		int width = tiles.length;
		int height = tiles[0].length;
		
		stream.writeInt(width);
		stream.writeInt(height);
		
		for(int x = 0; x < width; x ++){
			for(int y = 0; y < height; y ++){
				Tile tile = tiles[x][y];
				stream.writeInt(tile.wall.id);
				stream.writeInt(tile.floor.id);
				stream.writeShort(tile.data1);
				stream.writeShort(tile.data2);
			}
		}
		
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		
		Lzma.compress(in, new FileOutputStream(file.file()));
		
		stream.close();
	}
}
