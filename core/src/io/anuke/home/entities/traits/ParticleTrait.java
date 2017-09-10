package io.anuke.home.entities.traits;

import com.badlogic.gdx.math.Vector2;

import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.Trait;
import io.anuke.ucore.util.Mathf;

public class ParticleTrait extends Trait{
	public Particle[] particles;
	public float particleLife = 80;
	public float xRange = 6f, yMin = 0f, yMax = 20f;
	public float speed = 0.4f;
	public boolean emit = true;
	
	public ParticleTrait(int amount){
		this(amount, 6f, 6f);
	}
	
	public ParticleTrait(int amount, float xrange, float yrange){
		this.xRange = xrange;
		this.yMin = -yrange;
		this.yMax = yrange;
		
		particles = new Particle[amount];
	}
	
	public ParticleTrait setEmission(float speed, float life){
		this.particleLife = life;
		this.speed = speed;
		return this;
	}
	
	public ParticleTrait setParticles(int amount){
		particles = new Particle[amount];
		for(int i = 0; i < particles.length; i ++){
			particles[i] = new Particle();
		}
		return this;
	}
	
	@Override
	public void added(Spark spark){
		reset(spark);
	}
	
	@Override
	public void init(Spark spark){
		for(int i = 0; i < particles.length; i ++){
			particles[i] = new Particle();
			particles[i].reset(spark);
			particles[i].life = Mathf.random(particleLife);
		}
	}
	
	@Override
	public void update(Spark spark){
		for(Particle part : particles){
			part.life += Mathf.delta();
			part.y += Mathf.delta()*speed;
			
			if(part.fract() <= 0f && emit){
				part.reset(spark);
			}
		}
		
		if(Mathf.delta() >= particleLife/5f){
			reset(spark);
		}
	}
	
	public void reset(Spark spark){
		for(int i = 0; i < particles.length; i ++){
			particles[i].reset(spark);
			particles[i].life = Mathf.random(particleLife);
		}
	}
	
	public class Particle extends Vector2{
		float life;
		
		public float fract(){
			return 1f-life/particleLife;
		}
		
		public float ifract(){
			return life/particleLife;
		}
		
		public float sfract(){
			return (0.5f-Math.abs(life/particleLife-0.5f))*2f;
		}
		
		void reset(Spark spark){
			set(Mathf.range(xRange) + spark.pos().x, Mathf.random(yMin, yMax) + spark.pos().y);
			life = 0f;
		}
	}
}
