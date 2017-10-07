package io.anuke.home.entities.types.enemies.library;

import com.badlogic.gdx.graphics.Color;

import io.anuke.home.effect.Shaders.Glow;
import io.anuke.home.entities.traits.EnemyTrait;
import io.anuke.home.entities.traits.HealthBarTrait;
import io.anuke.home.entities.traits.LightTrait;
import io.anuke.home.entities.types.Enemy;
import io.anuke.home.entities.types.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.TraitList;
import io.anuke.ucore.ecs.extend.traits.FacetTrait;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;

public class Sol extends Enemy{
	public static Color color = new Color(0xf9f1bdff);
	
	public Sol(){
		speed = 2f;
	}
	
	@Override
	public void init(Spark spark){
		//Renderer.getEffect(LightEffect.class).addLight(4f);
	}
	
	@Override
	public void move(Spark spark){
		EnemyTrait trait = spark.get(EnemyTrait.class);
		Spark target = trait.target;
		
		trait.rot += Timers.delta();
		
		if(Timers.get(spark, "reload", 450)){
			Timers.runFor(70f, ()->{
				trait.rot = Mathf.slerp(trait.rot, spark.pos().angleTo(target, 0, raise()), 0.1f*Timers.delta());
				
				if(Timers.get(spark, "reload3", 5)){
					
					Angles.shotgun(4, 25f, 0f, f->{
						shoot(spark, Projectiles.lightshot, raise(), target, 5, f + Mathf.sin(Timers.time(), 0.3f, 60f));
					});
					
					shoot(spark, Projectiles.lightflash, raise(), target, 5, Mathf.range(50f));
					
					trait.moveTowardDeltaless(spark);
					Effects.shake(0.5f, 1f, spark);
				}
			}, ()->{
				
			});
			
			
		}
		
		if(Timers.get(spark, "reload2", 100)){
			
			for(int j = 0; j < 4; j ++){
				float angle = j*10f;
				Timers.run(j*15, ()->{
					for(int i = 0; i < 4; i ++){
						shoot(spark, Projectiles.lightshot, raise(), target, 5, i*90 + angle);
					}
				});
					
			}
			
		}
	}

	@Override
	public void draw(Spark spark, FacetTrait trait){
		trait.draw(spark, 12, ()->{
			
			float radius = 4 + Mathf.absin(Timers.time(), 10f, 4f);
			float raise = raise();
			float rot = spark.get(EnemyTrait.class).rot;
			
			spark.get(LightTrait.class).offset.y = raise;
			
			Draw.getShader(Glow.class).set(spark.pos().x, spark.pos().y + raise, color);
			Draw.beginShaders(Glow.class);
			
			Draw.rect("circle", spark.pos().x, spark.pos().y + raise, radius, radius);
			
			Draw.thick(2f);
			
			Draw.spikes(spark.pos().x, spark.pos().y + raise, 6f, 1f, 8, -rot);
			Draw.color(color);
			Draw.spikes(spark.pos().x, spark.pos().y + raise, 10f, 1f, 4, rot);
			
			Draw.spikes(spark.pos().x, spark.pos().y + raise, 6f, 4f, 4, rot);
			Draw.thick(1f);
			Draw.spikes(spark.pos().x, spark.pos().y + raise, 9f, 3f, 4, rot);
			Draw.color();
			
			Draw.reset();
			
			Draw.endShaders();
		});
	}
	
	@Override
	public TraitList traits(){
		TraitList list = super.traits();
		list.with(new LightTrait(44));
		
		HealthBarTrait bar = list.get(HealthBarTrait.class);
		bar.outline.a = 0.4f;
		bar.empty.a = 0.4f;
		bar.health.set(1f, 1f, 1f, 1f);
		
		return list;
	}
	
	float raise(){
		return Mathf.sin(Timers.time(), 20f, 3f) + 10;
	}
}
