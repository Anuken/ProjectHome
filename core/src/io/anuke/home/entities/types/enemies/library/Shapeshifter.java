package io.anuke.home.entities.types.enemies.library;

import io.anuke.home.Renderer;
import io.anuke.home.effect.Shaders.Glow;
import io.anuke.home.entities.traits.EnemyTrait;
import io.anuke.home.entities.traits.LightTrait;
import io.anuke.home.entities.types.Enemy;
import io.anuke.home.entities.types.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.Trait;
import io.anuke.ucore.ecs.TraitList;
import io.anuke.ucore.ecs.extend.traits.FacetTrait;
import io.anuke.ucore.util.*;

public class Shapeshifter extends Enemy{
	int[] runes = new int[8];
	
	public Shapeshifter(){
		speed = 0.1f;
		
		for(int i = 0; i < runes.length; i ++){
			runes[i] = Mathf.random(0, 15-1);
		}
	}

	@Override
	public void move(Spark spark){
		EnemyTrait trait = spark.get(EnemyTrait.class);
		Spark target = trait.target;
		Data data = spark.get(Data.class);
		
		height = raise();
		
		trait.rot += Timers.delta() * (6.5f - data.sides);
		
		data.sides = (int)(3 + spark.health().healthfrac() * 3f);
		
		if(Timers.get(spark, "changephase", 600)){
			data.phase += 2;
			data.phase %= 3;
		}
		
		if(data.phase == 0){
			if(Timers.get(spark, "shoot1", 5f + (data.sides-3f)*8)){
				Angles.circle(data.sides, f->{
					float ang = trait.rot + f;
			
					shoot(spark, Projectiles.lightball, spark.pos().x, spark.pos().y + raise(), ang);
				});
			}
		}else if(data.phase == 1){
			if(Timers.get(spark, "teleport", 20 + data.sides*5)){
				
				//TODO effect
				Timers.runFor(data.sides*5, ()->{
					if(Timers.get(spark, "reload2", 2)){
						Effects.shake(0.5f, 2f);
						shoot(spark, Projectiles.lightflash, raise(), target, 4f, Mathf.range(30f));
						
						shoot(spark, Projectiles.lightshot, raise(), target, 5f, Mathf.range(20f));
					}
				});
				
				for(int i = 0; i < 9 - data.sides/2; i ++){
					Timers.run((i*6), ()->{
						Angles.circle(data.sides, f->{
							float ang = trait.rot + f;
					
							shoot(spark, Projectiles.lightball, spark.pos().x, spark.pos().y + raise(), ang);
						});
					});
				}
				
			}
		}else if(data.phase == 2){
			speed = 0.2f + (6f-data.sides)*0.15f;
			trait.moveToward(spark);
			
			if(Timers.get(spark, "waves", 5 - (6-data.sides)/2)){
				shoot(spark, Projectiles.lightball, raise(), target, 5f, Mathf.sin(Timers.time(), 8f, 15f) + 23);
				shoot(spark, Projectiles.lightball, raise(), target, 5f, Mathf.sin(-Timers.time(), 8f, 15f) - 23);
			}
		}
	}

	@Override
	public void draw(Spark spark, FacetTrait trait){
		
		Data data = spark.get(Data.class);
		
		trait.draw(spark, 12, ()->{
			float raise = raise();
			float rot = spark.get(EnemyTrait.class).rot;
			
			spark.get(LightTrait.class).offset.y = raise;
			
			Draw.getShader(Glow.class).set(spark.pos().x, spark.pos().y + raise, Sol.color);
			Draw.beginShaders(Glow.class);
			
			Draw.thick(1f);
			
			Draw.polygon(data.sides, spark.pos().x, spark.pos().y + raise, 7f, rot);
			
			for(int i = 0; i < runes.length; i ++){
				Angles.translation(i * 360f/runes.length -rot, 15f);
				
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
	public Trait data(){
		return new Data();
	}
	
	@Override
	public TraitList traits(){
		return super.traits().with(new LightTrait(82));
	}
	
	static class Data extends Trait{
		int sides = 6;
		int phase = 0;
	}
}
