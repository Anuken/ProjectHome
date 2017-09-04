package io.anuke.home.effect;

import com.badlogic.gdx.graphics.Color;

import io.anuke.ucore.UCore;
import io.anuke.ucore.graphics.Shader;
import io.anuke.ucore.util.Timers;
import io.anuke.ucore.util.Tmp;

public class Shaders{
	
	public static void create(){
		new Outline();
		new Distort();
	}
	
	public static class Outline extends Shader{
		public Color color = Color.WHITE;
		
		public Outline(){
			super("outline", "outline");
		}
		
		@Override
		public void apply(){
			shader.setUniformf("u_color", color);
			shader.setUniformf("u_texsize", Tmp.v1.set(region.getTexture().getWidth(), region.getTexture().getHeight()));
		}
		
	}
	
	public static class Distort extends Shader{
		
		public Distort(){
			super("distort", "outline");
		}
		
		@Override
		public void apply(){
			UCore.log("applying");
			shader.setUniformf("u_time", Timers.time());
		}
		
	}
}
