package io.anuke.home.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
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
	
	public EditorInput(){
		Inputs.addProcessor(this);
	}
	
	void placeBlock(){
		int mousex = Mathf.scl2(Graphics.mouseWorld().x, Vars.tilesize),
				mousey = Mathf.scl2(Graphics.mouseWorld().y, Vars.tilesize);
		
		if(World.get(mousex, mousey) == null) return;
		
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
	
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		if(button == Buttons.LEFT && !Evar.ui.hasMouse()){
			placeBlock();
		}
		
		if(button == Buttons.MIDDLE){
			return true;
		}
		return false;
	}
	
	public boolean touchDragged (int screenX, int screenY, int pointer) {
		if(Evar.ui.hasMouse()) return false;
		
		if(Gdx.input.isButtonPressed(Buttons.MIDDLE)){
			float scale = 4f/DrawContext.camera.zoom;
			Vector3 position = DrawContext.camera.position;
			position.x -= Gdx.input.getDeltaX()/scale;
			position.y += Gdx.input.getDeltaY()/scale;
		}else if(Gdx.input.isButtonPressed(Buttons.LEFT)){
			placeBlock();
		}
		return false;
	}
	
	public boolean scrolled (int amount) {
		if(Evar.ui.hasMouse()) return false;
		
		DrawContext.camera.zoom += amount/5f;
		DrawContext.camera.zoom = Mathf.clamp(DrawContext.camera.zoom, 0.05f, 10f);
		return false;
	}
}
