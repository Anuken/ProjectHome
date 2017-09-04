package io.anuke.home.effect;

import com.badlogic.gdx.graphics.OrthographicCamera;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Core;

public class Darkness extends RenderEffect{
	public static final int layer = -999999;
	
	public float intensity = 0f;

	@Override
	public void init(){
		
		OrthographicCamera cam = Core.camera;
		
		draw(p->{
			p.layer = layer+1;
			
			Draw.color(0, 0, 0, intensity);
			Draw.rect("blank", cam.position.x, cam.position.y, 
					cam.viewportWidth, cam.viewportHeight);
			Draw.reset();
		});
	}

	@Override
	public void update(){
		
	}

}
