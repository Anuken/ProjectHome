package io.anuke.home;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

import io.anuke.gif.GifRecorder;
import io.anuke.home.GameState.State;
import io.anuke.home.entities.Enemy;
import io.anuke.home.entities.Player;
import io.anuke.home.world.Tile;
import io.anuke.home.world.World;
import io.anuke.ucore.core.*;
import io.anuke.ucore.entities.Entities;
import io.anuke.ucore.graphics.Atlas;
import io.anuke.ucore.graphics.Textures;
import io.anuke.ucore.modules.RendererModule;
import io.anuke.ucore.renderables.DrawLayerManager;
import io.anuke.ucore.renderables.RenderableHandler;
import io.anuke.ucore.util.Timers;

public class Control extends RendererModule{
	public Player player;
	private GifRecorder recorder = new GifRecorder(batch);
	private Tile checkpoint;
	private Array<Enemy> killed = new Array<>();
	
	public Control(){
		atlas = new Atlas("projecthome.atlas");
		Textures.load("textures/");
		Textures.repeatWrap("fog1", "fog2", "fog3", "fog4");
		
		clearColor = new Color(0x889dabff);
		
		cameraScale = 4;
		pixelate();
	
		KeyBinds.defaults(
			"up", Keys.W,
			"left", Keys.A,
			"down", Keys.S,
			"right", Keys.D,
			"dash", Keys.SHIFT_LEFT,
			"pause", Keys.ESCAPE,
			"pickup", Keys.Q,
			"weapon1", Keys.NUM_1,
			"weapon2", Keys.NUM_2,
			"weapon3", Keys.NUM_3
		);
		
		Settings.loadAll("io.anuke.home");

		Entities.initPhysics();
		Entities.setCollider(Vars.tilesize, (x, y)->{
			Tile tile = World.get(x, y);
			return tile != null && tile.wall.type.solid(tile.wall);
		}, (x, y, out)->{
			Tile tile = World.get(x, y);
			tile.wall.type.getHitbox(tile, tile.wall, out);
		});
		
		EffectLoader.load();
		
		RenderableHandler.instance().setLayerManager(new DrawLayerManager());
	}
	
	@Override
	public void init(){
		World.create();
		reset();
	}
	
	public void addKill(Enemy e){
		killed.add(e);
	}
	
	public void onDeath(){
		Vars.ui.showDeath();
	}
	
	public void addCheckpoint(Tile tile){
		checkpoint = tile;
		killed.clear();
	}
	
	public void respawn(){
		player.heal();
		player.set(checkpoint.worldx(), checkpoint.worldy()).add();
		player.oncheckpoint = true;
		
		for(Enemy enemy : killed){
			enemy.heal();
			enemy.add();
		}
		
		killed.clear();
	}
	
	public void reset(){
		killed.clear();
		Entities.clear();
		World.addDoors();
		
		float center = Vars.worldsize*Vars.tilesize/2f;
		
		player = new Player().set(center, center)/*.set(12*569, 12*(1024-110))*/.add();
		
		respawn();
		
		Entities.resizeTree(0, 0, center*2, center*2);
	}
	
	@Override
	public void update(){
		
		//TODO remove
		if(Inputs.keyDown(Keys.ESCAPE))
			Gdx.app.exit();
		
		
		if(GameState.is(State.playing)){
			
			if(Inputs.keyUp("pause")){
				GameState.set(State.paused);
				Vars.ui.showPaused();
			}
			
			Entities.update();
			
		}else if(GameState.is(State.paused)){
			if(Inputs.keyUp("pause")){
				GameState.set(State.playing);
				Vars.ui.hidePaused();
			}
		}
		
		setCamera(player.x, player.y);
		updateShake();
		clampCamera(0, 0, Vars.worldsize*Vars.tilesize-Vars.tilesize/2, Vars.worldsize*Vars.tilesize-Vars.tilesize/2);
		camera.update();
		
		drawDefault();
		
		recorder.update();
		
		Timers.update();
	}
	
	@Override
	public void draw(){
		drawBackground();
		Renderer.renderWorld();
		
		Draw.color();
		RenderableHandler.instance().renderAll();
		
		Entities.draw();
	}
	
	void drawBackground(){
		int s = 400;
		float scl = 1f;
		for(int i = 0; i < 4; i ++){
			Texture t = Textures.get("fog" + (i+1));
			int offset = (int)(Timers.time()/20*(i+1));
			batch.draw(t, camera.position.x-s/2, camera.position.y-s/2, s/2, s/2, s, s, scl, scl, 0f, offset, 0, s, s, false, false);
		}
	}
}
