package io.anuke.home;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

import io.anuke.home.GameState.State;
import io.anuke.home.entities.ecs.Prototypes;
import io.anuke.home.entities.ecs.traits.PlayerTrait;
import io.anuke.home.world.Generator;
import io.anuke.home.world.Tile;
import io.anuke.home.world.World;
import io.anuke.ucore.core.*;
import io.anuke.ucore.ecs.Basis;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.Trait;
import io.anuke.ucore.ecs.extend.processors.CollisionProcessor;
import io.anuke.ucore.ecs.extend.processors.DrawProcessor;
import io.anuke.ucore.ecs.extend.processors.TileCollisionProcessor;
import io.anuke.ucore.entities.Entities;
import io.anuke.ucore.graphics.Atlas;
import io.anuke.ucore.graphics.Textures;
import io.anuke.ucore.modules.RendererModule;
import io.anuke.ucore.renderables.DrawLayerManager;
import io.anuke.ucore.renderables.RenderableHandler;
import io.anuke.ucore.util.Timers;

public class Control extends RendererModule{
	private Basis basis;
	private final int startx = 525, starty = 1024-898;
	
	private Spark player;
	private Spark boss;
	
	private Tile checkpoint;
	private Array<Spark> killed = new Array<>();
	
	//boss starting coords
	//private final int startx = 552, starty=1024-177;
	
	//dungeon starting coords
	//private final int startx = 522, starty=1024-414;
	
	//private GifRecorder recorder = new GifRecorder(batch);
	
	public Control(){
		atlas = new Atlas("projecthome.atlas");
		Textures.load("textures/");
		Textures.repeatWrap("fog1", "fog2", "fog3", "fog4");
		
		clearColor = new Color(0x889dabff);
		
		cameraScale = 4;
		pixelate();
		
		basis = new Basis();
		basis.addProcessor(new CollisionProcessor());
		
		basis.addProcessor(new TileCollisionProcessor(Vars.tilesize, (x, y)->{
			Tile tile = World.get(x, y);
			return tile != null && tile.wall.type.solid(tile.wall);
		}, (x, y, out)->{
			Tile tile = World.get(x, y);
			tile.wall.type.getHitbox(tile, tile.wall, out);
		}));
		
		basis.addProcessor(new DrawProcessor());
	
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
		
		Sounds.load("blockdie.wav", "hurt.wav", "pickup.wav", "shoot.wav", "slash.wav", 
				"slash2.wav", "tentadie.wav", "ult.wav", "walls.wav", "death.wav", "bossdie.wav", "respawn.wav");
		
		Musics.load("menu.ogg", "world1.mp3", "world2.mp3", "world3.mp3", "boss.mp3");
		
		Musics.createTracks("world", "world1", "world2", "world3");
		Musics.createTracks("menu", "menu");
		Musics.createTracks("boss", "boss");
		
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
		player = new Spark(Prototypes.player);
				
		World.create();
		reset();
	}
	
	public void setBoss(Spark boss){
		this.boss = boss;
	}
	
	public Spark getBoss(){
		return boss;
	}
	
	public void resetBoss(Spark boss){
		if(boss == this.boss)
			this.boss = null;
	}
	
	public void addKill(Spark e){
		killed.add(e);
	}
	
	public void onDeath(){
		Timers.run(50, ()->{
			Vars.ui.showDeath();
		});
	}
	
	public void addCheckpoint(Tile tile){
		checkpoint = tile;
		killed.clear();
	}
	
	public Spark getPlayer(){
		return player;
	}
	
	public void respawn(){
		
		if(GameState.is(State.playing))
			Effects.sound("respawn");
		
		player.health().heal();
		player.pos().set(checkpoint.worldx(), checkpoint.worldy());
		player.add();
		
		for(Spark enemy : killed){
			enemy.health().heal();
			enemy.add();
		}
		
		killed.clear();
	}
	
	public void reset(){
		if(Vars.ui.inventory != null)
			Vars.ui.clearInventory();
		RenderableHandler.instance().clear();
		Renderer.updateWalls();
		killed.clear();
		Generator.generate();
		Entities.clear();
		Generator.addDoors();
		Renderer.clearWorld();
		
		checkpoint = World.get(startx, starty);
		
		float center = Vars.worldsize*Vars.tilesize/2f;
		
		for(Trait trait : player.getTraits()){
			trait.removed(player);
		}
		player.get(PlayerTrait.class).reset();
		player.add();
		
		respawn();
		
		Entities.resizeTree(0, 0, center*2, center*2);
	}
	
	@Override
	public void update(){
		
		if(Inputs.keyDown(Keys.ESCAPE) && Vars.debug)
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
		
		if(!GameState.is(State.menu)){
			basis.setProcessorsEnabled(true);
			//setCamera(player.x, player.y);
			smoothCamera(player.pos().x, player.pos().y+2f, 0.3f);
			
			if(boss != null){
				Musics.playTracks("boss");
			}else{
				Musics.playTracks("world");
			}
		}else{
			basis.setProcessorsEnabled(false);
			Musics.playTracks("menu");
			smoothCamera(startx*Vars.tilesize, starty*Vars.tilesize, 0.1f);
		}
		
		updateShake();
		clampCamera(0, 0, Vars.worldsize*Vars.tilesize-Vars.tilesize/2, Vars.worldsize*Vars.tilesize-Vars.tilesize/2);
		
		camera.update();
		
		drawDefault();
		
		//recorder.update();
		
		if(!GameState.is(State.paused)){
			Timers.update();
		}
		
	}
	
	@Override
	public void draw(){
		drawBackground();
		Renderer.renderWorld();
		
		Draw.color();
		RenderableHandler.instance().renderAll();
		basis.update();
		
		Entities.draw();
	}
	
	public void resize(){
		setCamera(startx*Vars.tilesize, starty*Vars.tilesize);
		camera.update();
	}
	
	void drawBackground(){
		Draw.color();
		int s = 400;
		float scl = 1f;
		for(int i = 0; i < 4; i ++){
			Texture t = Textures.get("fog" + (i+1));
			int offset = (int)(Timers.time()/20*(i+1));
			batch.draw(t, camera.position.x-s/2, camera.position.y-s/2, s/2, s/2, s, s, scl, scl, 0f, offset, 0, s, s, false, false);
		}
	}
}
