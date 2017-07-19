package io.anuke.home.editor;

import io.anuke.ucore.modules.Core;

public class Editor extends Core{

	@Override
	public void init(){
		addModule(new EditorInput());
		addModule(Evar.control = new EditorControl());
		addModule(Evar.ui = new EditorUI());
		
		/*
		try{
			Timers.mark();
			
			Tile[][] tiles = new Tile[1000][1000];
			for(int x = 0; x < tiles.length; x ++){
				for(int y = 0; y < tiles[0].length; y ++){
					tiles[x][y] = new Tile(x, y, Blocks.grass, Blocks.bluesapling);
					tiles[x][y].data = 1;
				}
			}
			
			MapLoader.save(tiles, Gdx.files.local("map.hs"));
			
			Tile[][] out = MapLoader.load(Gdx.files.local("map.hs"));
			UCore.log(out.length, out[0].length);
			UCore.log("Elapsed: " + Timers.elapsed() + "ms");
		}catch (Exception e){
			e.printStackTrace();
		}
		*/
	}

}
