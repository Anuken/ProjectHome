package io.anuke.home.entities.types.enemies.library;

import com.badlogic.gdx.graphics.Color;

import io.anuke.home.entities.traits.EnemyTrait;
import io.anuke.home.entities.traits.ParticleTrait;
import io.anuke.home.entities.traits.ParticleTrait.Particle;
import io.anuke.home.entities.types.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.TraitList;
import io.anuke.ucore.ecs.extend.Events.Death;
import io.anuke.ucore.ecs.extend.traits.FacetTrait;
import io.anuke.ucore.ecs.extend.traits.TileCollideTrait;
import io.anuke.ucore.facet.Sorter;
import io.anuke.ucore.util.Mathf;

public class Shade extends DarkEnemy{
	
	public Shade(){
		maxhealth = 400;
		range = 100;
		speed = 0.13f;
		
		event(Death.class, spark->{
			
			spark.get(ParticleTrait.class).emit = false;
			
			Timers.runFor(50f, ()->{
				spark.get(EnemyTrait.class).time -= Timers.delta();
				if(Timers.get(spark, "shadespark", 8)){
					Effects.effect("shadecloud", eyeColor, spark.pos().x + Mathf.range(6f), spark.pos().y + Mathf.range(6f) + raise());
				}
				Effects.shake(0.25f, 3f, spark);
			}, ()->{
				Effects.shake(3f, 4f, spark);
				callSuper(Death.class, spark);
			});
		});
	}

	@Override
	public void move(Spark spark){
		EnemyTrait enemy = spark.get(EnemyTrait.class);
		
		if(Timers.get(spark, "reload", 100)){
			shoot(spark, Projectiles.darkball, raise(), enemy.target, 15f, 0f);
			
			Timers.run(100f, ()->{
				spark.get(TileCollideTrait.class).move(spark, Mathf.range(12f), Mathf.range(12f));
			});
		}
		
		enemy.moveToward(spark);
	}

	@Override
	public void draw(Spark spark, FacetTrait trait){
		EnemyTrait enemy = spark.get(EnemyTrait.class);
		ParticleTrait part = spark.get(ParticleTrait.class);
		
		trait.draw(Sorter.object, Sorter.dark, ()->{
			
			Draw.alpha(Mathf.clamp(enemy.time/waketime));
			
			Draw.tint(Color.DARK_GRAY);
			
			float raise = raise();
			
			for(Particle p : part.particles){
				float rad = p.sfract()*7f + 1f;
				Draw.rect("circle", p.x, p.y + raise, rad, rad);
			}
			
			Draw.thick(3f);
			Draw.lineAngleCenter(spark.pos().x, spark.pos().y + raise, 90, 6f);
			
			Draw.thick(2f);
			Draw.lineAngleCenter(spark.pos().x, spark.pos().y + raise, 90, 8f);
			
			Draw.thick(1f);
			Draw.lineAngleCenter(spark.pos().x, spark.pos().y + raise, 90, 12f);
			
			Draw.thick(1f);
			Draw.tint(DarkEnemy.eyeColor);
			Draw.polygon(3, spark.pos().x, spark.pos().y + 10 + raise + Mathf.sin(Timers.time() + 2, 30f, 4f), 3f, Timers.time());
			
			Draw.reset();
		});
		
		trait.shadow(spark, 12);
	}
	
	float raise(){
		return 12f + Mathf.sin(Timers.time(), 40f, 4f);
	}
	
	@Override
	public TraitList traits(){
		TraitList list = super.traits().with(new ParticleTrait(50).setEmission(0.3f, 100f));
		return list;
	}

}
