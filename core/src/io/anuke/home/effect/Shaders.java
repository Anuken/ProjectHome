package io.anuke.home.effect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import io.anuke.ucore.core.Core;
import io.anuke.ucore.graphics.Shader;
import io.anuke.ucore.util.Timers;
import io.anuke.ucore.util.Tmp;

public class Shaders{
	
	public static void create(){
		new Outline();
		new Distort();
		new Wisp();
	}
	
	public static class Outline extends Shader{
		public Color color = new Color();
		
		public Outline(){
			super("outline", "outline");
		}
		
		@Override
		public void apply(){
			shader.setUniformf("u_color", color);
			shader.setUniformf("u_texsize", Tmp.v1.set(region.getTexture().getWidth(), region.getTexture().getHeight()));
		}
		
	}
	
	public static class Wisp extends Shader{
		public float screenx, screeny, time;
		
		public Wisp(){
			super("wisp", "outline");
		}
		
		public void set(float screenx, float screeny){
			this.screenx = screenx;
			this.screeny = screeny;
		}

		@Override
		public void apply(){
			Core.camera.project(Tmp.v31.set(screenx, screeny, 0));
			shader.setUniformf("time", Timers.time());
			shader.setUniformf("resolution", (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
			shader.setUniformf("pos", Tmp.v31.x/Gdx.graphics.getWidth(), Tmp.v31.y/Gdx.graphics.getHeight());
		}
	}
	
	public static class Distort extends Shader{
		public float offsetx, offsety;
		
		public Distort(){
			super("distort", "outline");
		}
		
		@Override
		public void apply(){
			Core.camera.project(Tmp.v31.set(offsetx, offsety, 0));
			shader.setUniformf("time", Timers.time());
			shader.setUniformf("offset", Tmp.v31.x/Gdx.graphics.getWidth(), Tmp.v31.y/Gdx.graphics.getHeight());
		}
		
	}
}
