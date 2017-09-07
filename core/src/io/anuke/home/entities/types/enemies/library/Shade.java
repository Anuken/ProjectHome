package io.anuke.home.entities.types.enemies.library;

import com.badlogic.gdx.graphics.Color;

import io.anuke.home.entities.traits.EnemyTrait;
import io.anuke.home.entities.traits.ParticleTrait;
import io.anuke.home.entities.traits.ParticleTrait.Particle;
import io.anuke.home.entities.types.Enemy;
import io.anuke.home.entities.types.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.Trait;
import io.anuke.ucore.ecs.TraitList;
import io.anuke.ucore.ecs.extend.traits.FacetTrait;
import io.anuke.ucore.facet.Sorter;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class Shade extends Enemy{
	float waketime = 60f;
	
	public Shade(){
		range = 100;
	}

	@Override
	public void move(Spark spark){
		EnemyTrait enemy = spark.get(EnemyTrait.class);
		
		if(Timers.get(spark, "reload", 100)){
			shoot(spark, Projectiles.darkball, raise(), enemy.target, 15f, 0f);
		}
	}
	
	@Override
	public void update(Spark spark){
		
		Data data = spark.get(Data.class);
		EnemyTrait enemy = spark.get(EnemyTrait.class);
		
		if(enemy.target != null){
			if(data.wake <= 0.0001f){
				spark.get(ParticleTrait.class).reset(spark);
			}
			data.wake += Mathf.delta();
		}else{
			data.wake -= Mathf.delta();
		}
		
		data.wake = Mathf.clamp(data.wake, 0, waketime);
	}

	@Override
	public void draw(Spark spark, FacetTrait trait){
		Data data = spark.get(Data.class);
		ParticleTrait part = spark.get(ParticleTrait.class);
		
		trait.draw(Sorter.object, Sorter.dark, ()->{
			
			//Draw.getShader(Outline.class).color = Color.BLACK;
			//Draw.beginShaders(Outline.class);
			
			Draw.alpha(data.wake/waketime);
			
			Draw.tint(Color.DARK_GRAY);
			
			float raise = raise();
			
			for(Particle p : part.particles){
				float rad = p.fract()*7f + 1f;
				Draw.rect("circle", p.x, p.y + raise, rad, rad);
			}
			
			Draw.thick(3f);
			Draw.lineAngleCenter(spark.pos().x, spark.pos().y + raise, 90, 6f);
			
			Draw.thick(2f);
			Draw.lineAngleCenter(spark.pos().x, spark.pos().y + raise, 90, 8f);
			
			Draw.thick(1f);
			Draw.lineAngleCenter(spark.pos().x, spark.pos().y + raise, 90, 12f);
			
			Draw.thick(1f);
			Draw.tint(Color.PURPLE);
			Draw.polygon(3, spark.pos().x, spark.pos().y + 10 + raise + Mathf.sin(Timers.time() + 2, 30f, 4f), 3f, Timers.time());
			
			Draw.reset();
			
			//Draw.endShaders();
		});
		
		trait.shadow(spark, 12);
	}
	
	float raise(){
		return 12f + Mathf.sin(Timers.time(), 40f, 4f);
	}
	
	@Override
	public TraitList traits(){
		return super.traits().with(new ParticleTrait(50).setEmission(0.3f, 100f));
	}
	
	@Override
	public Trait data(){
		return new Data();
	}
	
	static class Data extends Trait{
		public float wake;
	}

}
