package io.anuke.home.entities.types.enemies.library;

import io.anuke.home.Renderer;
import io.anuke.home.effect.Shaders.Glow;
import io.anuke.home.entities.traits.EnemyTrait;
import io.anuke.home.entities.traits.LightTrait;
import io.anuke.home.entities.types.Enemy;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.TraitList;
import io.anuke.ucore.ecs.extend.traits.FacetTrait;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class Solaris extends Enemy{
	int[] runes = new int[8];
	
	public Solaris(){
		speed = 0.1f;
		
		for(int i = 0; i < runes.length; i ++){
			runes[i] = Mathf.random(0, 15-1);
		}
	}

	@Override
	public void move(Spark spark){
		EnemyTrait trait = spark.get(EnemyTrait.class);
		Spark target = trait.target;
		
		trait.rot += Mathf.delta();
	}

	@Override
	public void draw(Spark spark, FacetTrait trait){
		
		trait.draw(spark, 12, ()->{
			
			float radius = 4 + Mathf.absin(Timers.time(), 10f, 4f);
			float raise = raise();
			float rot = spark.get(EnemyTrait.class).rot;
			
			spark.get(LightTrait.class).offset.y = raise;
			
			Draw.getShader(Glow.class).set(spark.pos().x, spark.pos().y + raise, Sol.color);
			Draw.beginShaders(Glow.class);
			
			Draw.rect("circle", spark.pos().x, spark.pos().y + raise, radius, radius);
			
			Draw.thick(2f);
			
			float base = 22f;
			
			Draw.spikes(spark.pos().x, spark.pos().y + raise, base + 6f, 1f, 8, -rot);
			
			//Draw.color(Sol.color);
			Draw.spikes(spark.pos().x, spark.pos().y + raise, base + 4f, 1f, 4, rot);
			Draw.spikes(spark.pos().x, spark.pos().y + raise, base + 0f, 4f, 4, rot);
			
			
			
			Draw.thick(1f);
			/*
			Draw.circle(spark.pos().x, spark.pos().y + raise, 24f);
			
			Draw.polygon(3, spark.pos().x, spark.pos().y + raise, 10f, rot);
			Draw.polygon(3, spark.pos().x, spark.pos().y + raise, 10f, rot + 60);
			Draw.polygon(3, spark.pos().x, spark.pos().y + raise, 10f, rot + 90);
			Draw.polygon(3, spark.pos().x, spark.pos().y + raise, 10f, rot + 60 + 90);
			Draw.polygon(4, spark.pos().x, spark.pos().y + raise, 24f, rot);
			Draw.polygon(4, spark.pos().x, spark.pos().y + raise, 24f, rot + 45);
			*/
			Draw.spikes(spark.pos().x, spark.pos().y + raise, 15f, 3f, 4, rot);
			
			for(int i = 0; i < runes.length; i ++){
				Angles.translation(i * 360f/runes.length + Timers.time(), 15f);
				
				Draw.rect(Renderer.getRune(runes[i]), spark.pos().x + Angles.x() - 1, spark.pos().y + Angles.y()*1f + raise);
			}
			
			Draw.reset();
			
			Draw.endShaders();
		});
	}
	
	float raise(){
		return Mathf.sin(Timers.time(), 20f, 3f) + 10;
	}
	
	@Override
	public TraitList traits(){
		return super.traits().with(new LightTrait(82));
	}
}
