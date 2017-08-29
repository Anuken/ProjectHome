package io.anuke.home.world;

import java.io.*;

import com.badlogic.gdx.Gdx;

public class WorldData{
	public boolean dark = false;
	
	public void write(DataOutputStream stream) throws IOException{
		stream.writeBoolean(dark);
	}
	
	public void read(DataInputStream stream) throws IOException{
		
		try{
			dark = stream.readBoolean();
		}catch (EOFException e){
			Gdx.app.error("[WorldData]", "Warning, map data is invalid: No WorldData detected.");
		}
	}
}
