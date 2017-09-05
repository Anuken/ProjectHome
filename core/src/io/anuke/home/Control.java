package io.anuke.home;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

import io.anuke.gif.GifRecorder;
import io.anuke.home.GameState.State;
import io.anuke.home.effect.EffectLoader;
import io.anuke.home.effect.LightEffect;
import io.anuke.home.effect.Shaders;
import io.anuke.home.entities.Prototypes;
import io.anuke.home.entities.processors.HealthBarProcessor;
import io.anuke.home.entities.traits.PlayerTrait;
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
import io.anuke.ucore.facet.FacetLayerHandler;
import io.anuke.ucore.facet.Facets;
import io.anuke.ucore.graphics.Atlas;
import io.anuke.ucore.graphics.Textures;
import io.anuke.ucore.modules.RendererModule;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class Control extends RendererModule{
	private Basis basis;
	
	private Spark player;
	private Spark boss;
	
	private Tile checkpoint;
	private Array<Spark> killed = new Array<>();
	
	private GifRecorder recorder = new GifRecorder(batch);
	
	public Control(){
		atlas = new Atlas("sprites.atlas");
		Textures.load("textures/");
		Textures.repeatWrap("fog1", "fog2", "fog3", "fog4");
		
		Core.cameraScale = 4;
		pixelate();
		
		Shaders.create();
		
		basis = new Basis();
		basis.addProcessor(new CollisionProcessor());
		
		basis.addProcessor(new TileCollisionProcessor(Vars.tilesize, (x, y)->{
			Tile tile = World.get(x, y);
			return tile != null && tile.wall.solid;
		}, (x, y, out)->{
			Tile tile = World.get(x, y);
			tile.wall.getHitbox(tile, out);
		}));
		
		basis.addProcessor(new HealthBarProcessor());
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
			"weaponswitch", Keys.SPACE
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
			return tile != null && tile.wall.solid;
		}, (x, y, out)->{
			Tile tile = World.get(x, y);
			tile.wall.getHitbox(tile, out);
		});
		
		EffectLoader.load();
		
		Facets.instance().setLayerManager(new FacetLayerHandler());
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
		Facets.instance().clear();
		Renderer.updateWalls();
		killed.clear();
		World.loadMap("library-filled");
		Entities.clear();
		Renderer.clearWorld();
		
		checkpoint = World.get(World.getStartX(), World.getStartY());
		
		float center = World.width()*Vars.tilesize/2f;
		
		for(Trait trait : player.getTraits()){
			trait.removed(player);
		}
		player.get(PlayerTrait.class).reset();
		player.add();
		
		respawn();
		
		Entities.resizeTree(0, 0, center*2, center*2);
		basis.getProcessor(CollisionProcessor.class).resizeTree(0, 0, center*2, center*2);
	}
	
	@Override
	public void update(){
		
		clearColor = World.data().sky ? Vars.skyColor : Color.BLACK;
		
		Renderer.getEffect(LightEffect.class).setEnabled(World.data().dark);
		
		if(Inputs.keyDown(Keys.ESCAPE) && Vars.debug)
			Gdx.app.exit();
		
		if(GameState.is(State.playing)){
			basis.setProcessorsEnabled(true);
			
			if(Inputs.keyUp("pause")){
				GameState.set(State.paused);
				Vars.ui.showPaused();
			}
			
			Entities.update();
			
		}else if(GameState.is(State.paused)){
			basis.setProcessorsEnabled(false);
			basis.getProcessor(DrawProcessor.class).setEnabled(true);
			
			if(Inputs.keyUp("pause")){
				GameState.set(State.playing);
				Vars.ui.hidePaused();
			}
		}
		
		if(!GameState.is(State.menu)){
			basis.getProcessor(DrawProcessor.class).setEnabled(false);
			basis.getProcessor(HealthBarProcessor.class).setEnabled(false);
			basis.update();
			
			smoothCamera(player.pos().x, player.pos().y+2f, 0.2f);
			
			float lim = 3;
			
			if(Math.abs(player.pos().x - camera.position.x) > lim){
				camera.position.x = player.pos().x - Mathf.clamp(player.pos().x - camera.position.x, -lim, lim);
			}
			
			if(Math.abs(player.pos().y - camera.position.y) > lim){
				camera.position.y = player.pos().y - Mathf.clamp(player.pos().y - camera.position.y, -lim, lim);
			}
			
			if(boss != null){
				Musics.playTracks("boss");
			}else{
				Musics.playTracks("world");
			}
		}else{
			basis.setProcessorsEnabled(false);
			Musics.playTracks("menu");
			smoothCamera(World.getStartX()*Vars.tilesize, World.getStartY()*Vars.tilesize, 0.1f);
		}
		
		updateShake();
		clampCamera(0, 0, World.width()*Vars.tilesize-Vars.tilesize/2, World.height()*Vars.tilesize-Vars.tilesize/2);
		
		float bx = camera.position.x, by = camera.position.y;
		camera.position.set((int)bx, (int)by, 0);
		
		camera.update();
		
		drawDefault();
		
		camera.position.set(bx, by, 0);
		
		if(boss != null && boss.health().dead){
			boss = null;
		}
		
		if(Renderer.getEffect(LightEffect.class).isEnabled()){
			Renderer.getEffect(LightEffect.class).drawLight();
		}
		
		recorder.update();
		
		if(!GameState.is(State.paused)){
			Timers.update();
		}
		
	}
	
	@Override
	public void draw(){
		
		drawBackground();
		
		Renderer.renderWorld();
		Draw.color();
		Facets.instance().renderAll();
		
		basis.setProcessorsEnabled(false);
		basis.getProcessor(DrawProcessor.class).setEnabled(true);
		basis.getProcessor(HealthBarProcessor.class).setEnabled(true);
		basis.update();
		
		Entities.draw();
		Renderer.renderEffects();
	}
	
	@Override
	public void resize(){
		setCamera(World.getStartX()*Vars.tilesize, World.getStartY()*Vars.tilesize);
		camera.update();
		Renderer.resetEffects();
	}
	
	void drawBackground(){
		if(World.data().sky){
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
}
