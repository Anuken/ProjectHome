package io.anuke.home.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import io.anuke.home.Renderer;
import io.anuke.home.Vars;
import io.anuke.home.effect.EffectCreator;
import io.anuke.home.effect.Shaders;
import io.anuke.home.entities.Prototypes;
import io.anuke.home.world.*;
import io.anuke.home.world.blocks.Blocks;
import io.anuke.ucore.core.*;
import io.anuke.ucore.ecs.Basis;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.ecs.extend.processors.TileCollisionProcessor;
import io.anuke.ucore.facet.FacetLayerHandler;
import io.anuke.ucore.facet.Facets;
import io.anuke.ucore.graphics.Atlas;
import io.anuke.ucore.graphics.Textures;
import io.anuke.ucore.modules.ControlModule;
import io.anuke.ucore.util.Mathf;

public class EditorControl extends ControlModule{
	public Block selected = Blocks.air;
	public Prototype seltype = null;
	public View view = View.none;
	public BlockType blocktype = BlockType.wall;
	public Tool tool = Tool.pencil;
	public int brushsize = 1;
	
	public int offsetx1, offsety1, offsetx2, offsety2;
	public int line = -1;
	float mousedx, mousedy, lmousex, lmousey;
	
	public EditorControl(){
		atlas = new Atlas("sprites.atlas");
		
		//load prototypes
		Prototypes.player.getClass();
		Shaders.create();
		
		Textures.load("textures/");
		Textures.repeatWrap("fog1", "fog2", "fog3", "fog4");
		
		Settings.defaultList(
			"lastexport", System.getProperty("user.home"),
			"lastimport", System.getProperty("user.home")
		);
		
		Settings.load("io.anuke.home.editor");
		
		EffectCreator.load();
		
		Core.cameraScale = 4;
		
		Facets.instance().setLayerManager(new FacetLayerHandler());
		
		Basis.instance().addProcessor(new TileCollisionProcessor(Vars.tilesize, (x, y) -> false));
	}
	
	@Override
	public void init(){
		World.create();
		try{
			MapIO.load(Gdx.files.internal(Settings.has("lastimport") ? Settings.getString("lastimport") : "maps/corruption.hsv"));
		}catch(Exception e){
			e.printStackTrace();
			World.loadMap("corruption");
		}
		
		camera.position.set(World.getStartX() * Vars.tilesize, World.getStartY() * Vars.tilesize, 0);
		camera.update();
	}
	
	@Override
	public void update(){
		if(Inputs.keyUp(Keys.ESCAPE))
			Gdx.app.exit();
		
		if(Inputs.keyUp(Keys.SPACE)){
			switchBlockType();
			Evar.ui.updateWallButton();
		}
		
		clearColor = World.data().sky ? Vars.skyColor : Color.BLACK;
		
		drawDefault();
		
		Draw.setScreen();
		batch.draw(Draw.getSurface("darkness").texture(), 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
		Draw.end();
		
		Timers.update();
		Inputs.update();
	}
	
	@Override
	public void draw(){
		camera.update();
		drawBackground();
		
		if(Inputs.buttonRelease(Buttons.LEFT)){
			Renderer.updateFacets();
			Facets.instance().renderAll();
		}
		
		//Renderer.renderWorld();
		//Facets.instance().renderAll();
		//Renderer.updateFacets();
		//Facets.instance().renderAll();
		Renderer.renderWorld();
		Facets.instance().renderAll();
		
		
		
		int mousex = Mathf.scl2(Graphics.mouseWorld().x, Vars.tilesize),
				mousey = Mathf.scl2(Graphics.mouseWorld().y, Vars.tilesize);
		
		Draw.thick(4f);
		Draw.color(Color.CORAL);
		
		int bottomx = -Vars.tilesize/2 + offsetx1*Vars.tilesize;
		int bottomy = -Vars.tilesize/2 + offsety1*Vars.tilesize;
		int topx = -Vars.tilesize/2 + (offsetx2+World.width())*Vars.tilesize;
		int topy = -Vars.tilesize/2 + (offsety2+World.height())*Vars.tilesize;
		
		Color select = Color.PURPLE, normal = Color.CORAL;
		
		Draw.color(line == 0 ? select : normal);
		Draw.line(bottomx, bottomy, topx, bottomy);
		
		Draw.color(line == 1 ? select : normal);
		Draw.line(topx, bottomy, topx, topy);
		
		Draw.color(line == 2 ? select : normal);
		Draw.line(topx, topy, bottomx, topy);
		
		Draw.color(line == 3 ? select : normal);
		Draw.line(bottomx, topy, bottomx, bottomy);
		
		//Draw.linerect(-6, -6, Vars.tilesize*World.width(), Vars.tilesize*World.height());
		Draw.reset();
		
		view.draw();
		
		
		
		/*
		int brushsize = (!tool.radius() || seltype != null ? 1 : this.brushsize);
		
		for(int rx = -brushsize; rx <= brushsize; rx++){
			for(int ry = -brushsize; ry <= brushsize; ry++){
				if(Vector2.dst(rx, ry, 0, 0) < brushsize)
					Draw.rect("place", rx*Vars.tilesize + mousex, ry*Vars.tilesize + mousey);
			}
		}
		*/
		tool.draw(mousex, mousey);
		
		Basis.instance().update();
		//Draw.rect("place", mousex, mousey);
	}
	
	@Override
	public void resize(){
		camera.position.set(World.getStartX() * Vars.tilesize, World.getStartY() * Vars.tilesize, 0);
		camera.update();
	}
	
	public void switchBlockType(){
		blocktype = BlockType.values()[(blocktype.ordinal() + 1) % BlockType.values().length];
	}
	
	void drawBackground(){
		if(World.data().sky){
			Draw.color();
			int s = 400;
			float scl = 1f*camera.zoom;
			for(int i = 0; i < 4; i ++){
				Texture t = Textures.get("fog" + (i+1));
				int offset = (int)(Timers.time()/20*(i+1));
				batch.draw(t, camera.position.x-s/2, camera.position.y-s/2, s/2, s/2, s, s, scl, scl, 0f, offset, 0, s, s, false, false);
			}
		}
	}
}
