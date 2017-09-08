package io.anuke.home.entities.types.enemies.library;

import io.anuke.home.entities.traits.DarkenTrait;
import io.anuke.home.entities.traits.EnemyTrait;
import io.anuke.home.entities.traits.ParticleTrait;
import io.anuke.home.entities.types.Enemy;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.TraitList;
import io.anuke.ucore.util.Mathf;

public abstract class DarkEnemy extends Enemy{
	float waketime = 60f;
	
	public DarkEnemy(){
		range = 100;
	}
	
	@Override
	public void update(Spark spark){
		EnemyTrait enemy = spark.get(EnemyTrait.class);
		
		if(enemy.target != null){
			if(enemy.rot <= 0.0001f){
				if(spark.has(ParticleTrait.class))
					spark.get(ParticleTrait.class).reset(spark);
			}
			enemy.rot += Mathf.delta();
		}else{
			enemy.rot -= Mathf.delta();
		}
		
		enemy.rot = Mathf.clamp(enemy.rot, 0, waketime);
	}
	
	@Override
	public TraitList traits(){
		return super.traits().with(new DarkenTrait());
	}
}
