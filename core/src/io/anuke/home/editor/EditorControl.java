package io.anuke.home.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import io.anuke.home.Renderer;
import io.anuke.home.Vars;
import io.anuke.home.world.*;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Graphics;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.ecs.Basis;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.ecs.extend.processors.TileCollisionProcessor;
import io.anuke.ucore.graphics.Atlas;
import io.anuke.ucore.graphics.Textures;
import io.anuke.ucore.modules.RendererModule;
import io.anuke.ucore.renderables.DrawLayerManager;
import io.anuke.ucore.renderables.RenderableHandler;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class EditorControl extends RendererModule{
	public Block selected = Blocks.air;
	public Prototype seltype = null;
	public View view = View.none;
	public boolean walls;
	public Tool tool = Tool.pencil;
	public int brushsize = 1;
	
	public EditorControl(){
		atlas = new Atlas("projecthome.atlas");
		
		Textures.load("textures/");
		Textures.repeatWrap("fog1", "fog2", "fog3", "fog4");
		
		clearColor = new Color(0x889dabff);
		
		cameraScale = 4;
		
		RenderableHandler.instance().setLayerManager(new DrawLayerManager());
		
		Basis.instance().addProcessor(new TileCollisionProcessor(Vars.tilesize, (x, y)->false));
	}
	
	@Override
	public void init(){
		World.create();
		Generator.generate();
		camera.position.set(World.width()*Vars.tilesize/2, World.height()*Vars.tilesize/2, 0);
	}
	
	@Override
	public void update(){
		if(Inputs.keyUp(Keys.ESCAPE))
			Gdx.app.exit();
		
		if(Inputs.keyUp(Keys.SPACE)){
			walls = !walls;
			Evar.ui.updateWallButton();
		}
		
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
		
		int mousex = Mathf.scl2(Graphics.mouseWorld().x, Vars.tilesize),
				mousey = Mathf.scl2(Graphics.mouseWorld().y, Vars.tilesize);
		
		Draw.thick(3f);
		Draw.color(Color.CORAL);
		Draw.linerect(-6, -6, Vars.tilesize*World.width(), Vars.tilesize*World.height());
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
	
	public void resize(){
		setCamera(World.width()*Vars.tilesize/2, World.height()*Vars.tilesize/2);
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
