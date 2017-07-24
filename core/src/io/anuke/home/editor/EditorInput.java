package io.anuke.home.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import io.anuke.home.Renderer;
import io.anuke.home.Vars;
import io.anuke.home.world.Block;
import io.anuke.home.world.World;
import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.core.Graphics;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.modules.Module;
import io.anuke.ucore.util.Mathf;

public class EditorInput extends Module{
	int lworldx, lworldy;

	public EditorInput() {
		Inputs.addProcessor(this);
	}
	
	public void update(){
		if(Inputs.buttonRelease(Buttons.LEFT)){
			int worldx = Mathf.scl2(Graphics.mouseWorld().x, Vars.tilesize);
			int worldy = Mathf.scl2(Graphics.mouseWorld().y, Vars.tilesize);
			Evar.control.tool.up(worldx, worldy);
		}
	}

	void placeBlock(){
		int mousex = Mathf.scl2(Graphics.mouseWorld().x, Vars.tilesize), mousey = Mathf.scl2(Graphics.mouseWorld().y, Vars.tilesize);

		if(World.get(mousex, mousey) == null)
			return;

		Block block = Evar.control.selected;
		if(block.type.floor){
			if(World.get(mousex, mousey).floor != block){
				World.get(mousex, mousey).floor = block;
				Renderer.updateFloor(mousex, mousey);
			}
		}else{
			if(World.get(mousex, mousey).wall != block){
				World.get(mousex, mousey).wall = block;
				Renderer.updateWall(mousex, mousey);
				Renderer.updateFloor(mousex, mousey);
			}
		}
	}

	void placeType(){
		int mousex = Mathf.scl2(Graphics.mouseWorld().x, Vars.tilesize), mousey = Mathf.scl2(Graphics.mouseWorld().y, Vars.tilesize);
		
		if(World.get(mousex, mousey) == null)
			return;
		
		Evar.control.tool.spawnClicked(mousex, mousey);
	}

	void placeRad(int x, int y){
		if(Evar.control.tool.radius()){
			for(int rx = -Evar.control.brushsize; rx <= Evar.control.brushsize; rx++){
				for(int ry = -Evar.control.brushsize; ry <= Evar.control.brushsize; ry++){
					if(Vector2.dst(rx, ry, 0, 0) < Evar.control.brushsize)
						Evar.control.tool.clicked(rx + x, ry + y);
				}
			}
		}else{
			Evar.control.tool.clicked(x, y);
		}
	}

	public boolean touchDown(int screenX, int screenY, int pointer, int button){
		if(button == Buttons.LEFT && !Evar.ui.hasMouse()){
			int worldx = Mathf.scl2(Graphics.mouseWorld().x, Vars.tilesize);
			int worldy = Mathf.scl2(Graphics.mouseWorld().y, Vars.tilesize);
			Evar.control.tool.down(worldx, worldy);
			if(Evar.control.seltype == null){
				placeRad(worldx, worldy);
			}else{
				placeType();
			}
		}

		if(button == Buttons.MIDDLE){
			return true;
		}
		return false;
	}

	public boolean touchDragged(int screenX, int screenY, int pointer){
		if(Evar.ui.hasMouse())
			return false;

		if(Gdx.input.isButtonPressed(Buttons.MIDDLE)){
			float scale = 4f / DrawContext.camera.zoom;
			Vector3 position = DrawContext.camera.position;
			position.x -= Gdx.input.getDeltaX() / scale;
			position.y += Gdx.input.getDeltaY() / scale;
		}else if(Gdx.input.isButtonPressed(Buttons.LEFT)){
			int worldx = Mathf.scl2(Graphics.mouseWorld().x, Vars.tilesize);
			int worldy = Mathf.scl2(Graphics.mouseWorld().y, Vars.tilesize);
			Evar.control.tool.moved();

			if(worldx != lworldx || worldy != lworldy){
				if(Evar.control.seltype == null){
					placeRad(worldx, worldy);
				}else{
					placeType();
				}
				
				lworldx = worldx;
				lworldy = worldy;
			}
		}
		return false;
	}

	public boolean scrolled(int amount){
		if(Evar.ui.hasMouse())
			return false;

		DrawContext.camera.zoom += amount / 5f;
		DrawContext.camera.zoom = Mathf.clamp(DrawContext.camera.zoom, 0.05f, 10f);
		return false;
	}
}
