package io.anuke.home.entities.types.enemies;

import io.anuke.home.entities.traits.EnemyTrait;
import io.anuke.home.entities.types.Enemy;
import io.anuke.home.entities.types.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.extend.traits.PosTrait;
import io.anuke.ucore.ecs.extend.traits.RenderableTrait;
import io.anuke.ucore.util.Geometry;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class Golem extends Enemy{
	
	public Golem(){
		maxhealth = 10;
		
		speed = 0.4f;
		
		height = 6;
		hitoffset = height;
		deathsound = "blockdie";
	}

	@Override
	public void move(Spark spark){
		
		if(Timers.get(this, "smash", 100)){
			float rdir = 30;
			Timers.runFor(rdir, ()->{
				spark.get(EnemyTrait.class).rot += 3f/rdir*Mathf.delta();
			}, ()->{
				if(spark.health().dead) return;
				
				spark.get(EnemyTrait.class).rot = -2f;
				Effects.shake(2f, 4f, spark);
				Effects.effect("eyeflash", spark.pos().x, spark.pos().y+11);
				Effects.effect("golemwave", spark);
				
				Geometry.circle(8, ang->{
					shoot(spark, Projectiles.golemshot, ang);
				});
				
				Timers.run(20f, ()->{
					spark.get(EnemyTrait.class).rot = 0f;
				});
			});
		}
	}

	@Override
	public void draw(Spark spark, RenderableTrait trait){
		trait.draw(p->{
			PosTrait pos = spark.pos();
			Draw.grect("golem-armless", pos.x, pos.y);
			
			Draw.grect("golem-arms", pos.x, pos.y + spark.get(EnemyTrait.class).rot);
			
			p.layer = pos.y;
		});
		
		trait.drawShadow(spark, 13, 0);
	}
}
