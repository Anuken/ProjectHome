package io.anuke.home.effect;

import com.badlogic.gdx.utils.Disposable;

import io.anuke.ucore.renderables.FuncRenderable;
import io.anuke.ucore.renderables.FuncRenderable.DrawFunc;
import io.anuke.ucore.renderables.RenderableList;

public abstract class RenderEffect implements Disposable{
	RenderableList list = new RenderableList();
	boolean enabled;
	
	/**This should reset this effect to its starting state. Called when the screen is resized or a world is loaded.*/
	public void reset(){
		
	}
	
	public void dispose(){}
	
	/**Called when this effect is created.*/
	public abstract void init();
	public abstract void update();
	
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	
	public boolean isEnabled(){
		return enabled;
	}
	
	void draw(DrawFunc d){
		list.add(new FuncRenderable(d));
	}
}
