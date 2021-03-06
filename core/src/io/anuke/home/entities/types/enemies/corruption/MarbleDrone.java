package io.anuke.home.entities.types.enemies.corruption;

import io.anuke.home.entities.traits.EnemyTrait;
import io.anuke.home.entities.types.Enemy;
import io.anuke.home.entities.types.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.extend.traits.FacetTrait;
import io.anuke.ucore.util.Mathf;

public class MarbleDrone extends Enemy{
	
	public MarbleDrone(){
		maxhealth = 140;
		hitoffset = 9;
	}

	@Override
	public void move(Spark spark){
		EnemyTrait trait = spark.get(EnemyTrait.class);
		trait.moveToward(spark);
		
		if(Timers.get(this, "reload", 80)){
			for(int i = 0; i < 5; i ++){
				Timers.run(i*9, ()->{
					if(trait.target == null || spark.health().dead) return;
					Effects.effect("orangeblap", spark.pos().x, spark.pos().y+8);
					shoot(spark, Projectiles.droneshot, spark.pos().x, spark.pos().y+8, spark.pos().angleTo(trait.target));
				});
			}
		}
	}

	@Override
	public void draw(Spark spark, FacetTrait trait){
		trait.draw(p->{
			float rise = Mathf.sin(Timers.time(), 16f, 2f)+6f;
			Draw.grect("marbledrone", spark.pos().x, spark.pos().y+rise);
			p.layer = spark.pos().y;
		});
		
		trait.shadow(spark, 8);
	}

}
