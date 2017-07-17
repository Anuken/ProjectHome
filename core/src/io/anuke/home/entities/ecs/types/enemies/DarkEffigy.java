package io.anuke.home.entities.ecs.types.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import io.anuke.home.entities.ecs.types.Enemy;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.Trait;
import io.anuke.ucore.ecs.extend.traits.RenderableTrait;
import io.anuke.ucore.util.Mathf;

public class DarkEffigy extends Enemy{
	private static final Color tent = new Color(0x500680ff);
	private static final float plife = 80;
	
	public DarkEffigy(){
		maxhealth = 4600;
		hitsize = 20;
		hitoffset = 10;
		height = 20;
		range = 400;
	}

	@Override
	public void move(Spark spark){
		
	}

	@Override
	public void draw(Spark spark, RenderableTrait trait){
		for(int i = 0; i < particles.length; i ++){
			particles[i] = new Particle();
			particles[i].reset();
			particles[i].life = Mathf.random(plife);
		}
	}
	
	public Trait data(){
		return new Data();
	}
	
	static class Data extends Trait{
		private Particle[] particles = new Particle[100];
		private float plife = 80;
		private float eyeflash = 0f;
		private float startx, starty;
		
		private float rotation = 0f;
		private Phase phase = Phase.values()[0];
		private float phasetime = 1200f;
		private float transitiontime = 260f;
	}
	
	private enum Phase{
		idle, swirl, blast, seek, chase, tentacles, spam, lanes;
	}
	
	private class Particle extends Vector2{
		float life;
		
		public float fract(){
			return 1f-life/plife;
		}
		
		public float ifract(){
			return life/plife;
		}
		
		public float sfract(){
			return (0.5f-Math.abs(life/plife-0.5f))*2f;
		}
		
		void reset(){
			set(Mathf.range(6), Mathf.random(0f, 20f));
			life = 0f;
		}
	}
}
