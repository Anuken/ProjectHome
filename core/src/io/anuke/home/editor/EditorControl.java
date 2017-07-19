package io.anuke.home.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import io.anuke.home.Renderer;
import io.anuke.home.Vars;
import io.anuke.home.world.Block;
import io.anuke.home.world.Generator;
import io.anuke.home.world.World;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Graphics;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.graphics.Atlas;
import io.anuke.ucore.graphics.Textures;
import io.anuke.ucore.modules.RendererModule;
import io.anuke.ucore.renderables.DrawLayerManager;
import io.anuke.ucore.renderables.RenderableHandler;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class EditorControl extends RendererModule{
	public Block selected = null;
	public Prototype seltype = null;
	public View view = View.none;
	
	public EditorControl(){
		atlas = new Atlas("projecthome.atlas");
		
		Textures.load("textures/");
		Textures.repeatWrap("fog1", "fog2", "fog3", "fog4");
		
		clearColor = new Color(0x889dabff);
		
		cameraScale = 4;
		
		RenderableHandler.instance().setLayerManager(new DrawLayerManager());
	}
	
	@Override
	public void init(){
		World.create();
		Generator.generate();
		camera.position.set(Vars.worldsize*Vars.tilesize/2, Vars.worldsize*Vars.tilesize/2, 0);
	}
	
	@Override
	public void update(){
		if(Inputs.keyUp(Keys.ESCAPE))
			Gdx.app.exit();
		
		drawDefault();
		
		Timers.update();
		Inputs.update();
	}
	
	@Override
	public void draw(){
		camera.update();
		drawBackground();
		Renderer.renderWorld();
		RenderableHandler.instance().renderAll();
		
		float mousex = Mathf.round2(Graphics.mouseWorld().x, Vars.tilesize),
				mousey = Mathf.round2(Graphics.mouseWorld().y, Vars.tilesize);
		
		Draw.thick(3f);
		Draw.color(Color.CORAL);
		Draw.linerect(-6, -6, Vars.tilesize*Vars.worldsize, Vars.tilesize*Vars.worldsize);
		Draw.reset();
		
		view.draw();
		
		Draw.rect("place", mousex, mousey);
	}
	
	public void resize(){
		setCamera(Vars.worldsize*Vars.tilesize/2, Vars.worldsize*Vars.tilesize/2);
		camera.update();
	}
	
	void drawBackground(){
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
