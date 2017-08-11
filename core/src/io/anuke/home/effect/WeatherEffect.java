package io.anuke.home.effect;

import io.anuke.ucore.renderables.FuncRenderable;
import io.anuke.ucore.renderables.FuncRenderable.DrawFunc;
import io.anuke.ucore.renderables.RenderableList;

public abstract class WeatherEffect{
	RenderableList list = new RenderableList();
	
	public void clear(){
		list.free();
	}
	
	public abstract void init();
	public abstract void update();
	
	void draw(DrawFunc d){
		list.add(new FuncRenderable(d));
	}
}
